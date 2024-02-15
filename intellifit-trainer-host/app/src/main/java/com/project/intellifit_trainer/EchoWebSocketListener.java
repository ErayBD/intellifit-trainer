package com.project.intellifit_trainer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EchoWebSocketListener extends WebSocketListener { // alici
    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
//        Log.d("WebSocket", "EchoWebSocketListener Socket opened");// Sunucuya mesaj gönderme
        // Görüntü verisini göndermek için webSocket.send(ByteString.of(imageBytes)); kullanabilirsiniz
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        byte[] byteArray = bytes.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //activity.runOnUiThread(() -> activity.updateImageView(bitmap));
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
//        Log.e("WebSocket", "EchoWebSocketListener Error: " + t.getMessage(), t); // Burada Log.e kullanıyoruz ve hata detaylarını da ekliyoruz
    }


    // Diğer gerekli metotlar (onClosing, onClose, onFailure, vb.)
}
