package com.project.intellifit_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraDevice;
import android.content.Context;
import android.view.Surface;
import android.view.TextureView;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

import okio.ByteString;

public class StartWorkoutActivity extends AppCompatActivity {

    private MyWebSocketClient myWebSocketClient; // WEBSOCKET
    private static final int CAMERA_REQUEST_CODE = 100;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraCharacteristics cameraCharacteristics;
    private AutoFitTextureView textureView;
    private ImageView imageView; // ImageView tanımı
    private final int IMAGE_SEND_INTERVAL = 100; // milisaniye
    private final Handler imageSendHandler = new Handler();
    private final Runnable imageSendRunnable = new Runnable() {
        @Override
        public void run() {
            sendImage();
            imageSendHandler.postDelayed(this, IMAGE_SEND_INTERVAL);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        imageSendHandler.postDelayed(imageSendRunnable, IMAGE_SEND_INTERVAL);
    }


    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Size previewSize = chooseOptimalSize(cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(SurfaceTexture.class),
                    width, height);
            if (previewSize != null) {
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                configureTransform(width, height, previewSize);
            }
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Yüzey boyutu değiştiğinde gereken işlemler
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // Yüzey güncellendiğinde gereken işlemler
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }

        // onDisconnected ve onError metodları
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startworkout);

        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(textureListener);
        imageView = findViewById(R.id.processedImageView);

        getWorkoutNames();

        myWebSocketClient = new MyWebSocketClient(this);
        myWebSocketClient.start();

        // Burayi kamera izinlerini kontrol etmek ve kamera önizlemesini başlatmak için kullanicam
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // İzin verilmemiş, izin isteme
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            // İzin zaten verilmiş, kamera önizlemesini başlat
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, kamera önizlemesini başlat
                openCamera();
            } else {
                // İzin verilmedi, kullanıcıya uyarı göster
            }
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[0];
            cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler); // Arkaplan handler'ını kullan
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (null == cameraDevice) {
                                return;
                            }
                            cameraCaptureSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // Konfigürasyon hatası durumunda yapılacak işlemler
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight) {
        List<Size> bigEnough = new ArrayList<>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * textureViewHeight / textureViewWidth
                    && option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                bigEnough.add(option);
            }
        }

        if (bigEnough.size() > 0) {
            Size optimalSize = Collections.min(bigEnough, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                            (long) rhs.getWidth() * rhs.getHeight());
                }
            });
//            Log.d("CameraDebug", "Optimal Size: " + optimalSize.getWidth() + "x" + optimalSize.getHeight());
            return optimalSize;
        }

//        Log.d("CameraDebug", "Using default size: " + choices[0].getWidth() + "x" + choices[0].getHeight());
        return choices[0];
    }


    private void configureTransform(int viewWidth, int viewHeight, Size previewSize) {
        if (null == textureView || null == previewSize) {
            return;
        }

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getWidth(), previewSize.getHeight());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.CENTER);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }

        textureView.setTransform(matrix);
    }

    // textureview'den yakalanan goruntunun byte array'a donusturulup, optimize edilmesi
    private byte[] getCameraImage() {
        Bitmap bitmap = textureView.getBitmap();

        int width = bitmap.getWidth() / 2;
        int height = bitmap.getHeight() / 2;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//      bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
//      resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream);
        resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 30, stream);

        bitmap.recycle();
        resizedBitmap.recycle();
        return stream.toByteArray();
    }


    public void updateImageView(Bitmap bitmap) {
//        Log.d("WebSocket", "Sending image..."); // Gönderim öncesi log
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
//        textureView.setVisibility(View.GONE);
    }


    // Görüntü gönderme metodu
    public void sendImage() {
        new Thread(() -> {
            if (myWebSocketClient != null && myWebSocketClient.isConnected) {

//                JSONObject jsonMessage = new JSONObject();
//                try {
//                    jsonMessage.put("exercise", "dumbbell_curl");
//                    // JSON mesajını gönder
//                    myWebSocketClient.webSocket.send(jsonMessage.toString());
//                    Log.d("WebSocket", "JSON message sent: " + jsonMessage.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                Log.d("WebSocket", "Sending image...");
                byte[] bitmapBytes = getCameraImage();
                Log.d("WebSocket", "IMG size: " + bitmapBytes.length);
                myWebSocketClient.webSocket.send(ByteString.of(bitmapBytes));
            } else {
//                Log.d("WebSocket", "WebSocket is not connected.");
            }
        }).start();
    }

    public void getWorkoutNames() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("selected_workout").child("exercises");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot exerciseSnapshot : dataSnapshot.getChildren()) {
                    String exerciseName = exerciseSnapshot.child("name").getValue(String.class);
                    Integer repCount = exerciseSnapshot.child("repCount").getValue(Integer.class);
                    Integer setCount = exerciseSnapshot.child("setCount").getValue(Integer.class);
                    if (exerciseName != null) {
                        // Egzersiz adını formatla: küçük harfe çevir, boşlukları alt çizgi ile değiştir
                        String formattedName = exerciseName.toLowerCase().replace(" ", "_");

                        // Formatlanmış adı Python sunucunuza gönderin
                        sendExerciseName(formattedName, repCount, setCount);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemler
//                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void sendExerciseName(String exerciseName, Integer repCount, Integer setCount) {
        // WebSocket veya başka bir yöntemle Python sunucunuza gönderme işlemi burada yapılacak
        // Örneğin:
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("exercise", exerciseName);
            jsonMessage.put("repCount", repCount);
            jsonMessage.put("setCount", setCount);
            // JSON mesajını gönder
            myWebSocketClient.webSocket.send(jsonMessage.toString());
            Log.d("WebSocket", "JSON message sent: " + jsonMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
