package de.pro_open.remotecontrol;

import JavaUtils.TCPManager.TCPManager;
import JavaUtils.TCPManager.TcpConnection;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.security.InvalidParameterException;

public class NetworkLooper extends Thread {
  static Runnable sendLine(final TcpConnection conToServer, final String s, final TCPCallback cb) {
    if(s==null) throw new InvalidParameterException("string to send cannot be null");
    if(conToServer==null) throw new InvalidParameterException("received null connection to server");
    
    return new Runnable() {
      @Override
      public void run() {
        
        if(conToServer.writeLine(s)) {
          conToServer.flush();
          cb.callback(conToServer);
        } else {
          cb.reject("could not send line");
        }
        
      }
    };
    
  }
  
  static Runnable TCPConnect(final String ip, final int port, final TCPCallback callback) {
    if(callback == null){
      throw new InvalidParameterException("TCPCallback parameter cannot be null");
    }
    
    return new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("conToServer " + ip);
          TcpConnection connection = TCPManager.connect(ip, port, false, null);
          callback.callback(connection);
        } catch (IOException e) {
          callback.reject(e.getMessage());
          System.err.println("handled "+e.getMessage());
        }
        
      }
    };
  }
  
  public Handler mHandler;
  
  public void sendMessage(Runnable r) {
    if (mHandler == null) {
      System.err.println("Handler not ready!");
      return;
    }
    mHandler.post(r);
  }
  
  
  public void run() {
    Looper.prepare();
    
    mHandler = new Handler(Looper.myLooper()) {
      public void handleMessage(Message msg) {
        // process incoming messages here
        try {
          System.out.println("Received Message" + msg.getData().toString());
        } catch (Exception e) {
          System.err.println("Failed to get message");
        }
        
      }
    };
    
    Looper.loop();
  }
}