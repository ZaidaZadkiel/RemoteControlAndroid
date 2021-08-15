package de.pro_open.remotecontrol;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class NetworkLooper extends Thread {
    public Handler mHandler;
    
    public void sendMessage(Runnable r){
      if(mHandler == null) {
        System.err.println("Handler not ready!");
        return;
      }
      System.out.println("Handler sending a message");
      mHandler.post(r);
    }
    
    public void run() {
        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            public void handleMessage(Message msg) {
                // process incoming messages here
              try {
                System.out.println("Received Message" + msg.getData().toString());
              } catch (Exception e){
                System.err.println("Failed to get message");
              }

            }
        };

        Looper.loop();
    }
}