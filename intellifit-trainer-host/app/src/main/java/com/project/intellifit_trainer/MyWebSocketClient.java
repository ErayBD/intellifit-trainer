package com.project.intellifit_trainer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MyWebSocketClient {

    OkHttpClient client;
    WebSocket webSocket;
    StartWorkoutActivity activity; // Activity referansı
    boolean isConnected = false;

    public MyWebSocketClient(StartWorkoutActivity activity) {
        this.client = new OkHttpClient();
        this.activity = activity;
    }

    public void start() {
        isConnected = true;
//        Log.d("WebSocket", "MyWebSocketClient WebSocket client started");
        Request request = new Request.Builder().url("ws://192.168.60.81:12345").build();
        this.webSocket = client.newWebSocket(request, new EchoWebSocketListener());
    }


    private class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            isConnected = true;
//            Log.d("WebSocket", "MyWebSocketClient Socket opened");
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
//            Log.d("WebSocket", "Received a Message from Python server.");
            byte[] byteArray = bytes.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            activity.runOnUiThread(() -> activity.updateImageView(bitmap));
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
//            Log.d("WebSocket", "MyWebSocketClient sent a message: " + text); // Burada Log.d kullanıyoruz
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            isConnected = false;
            // Diğer işlemler...
        }
    }
}
