package de.pro_open.remotecontrol;

import JavaUtils.TCPManager.TcpConnection;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.StringJoiner;

import static de.pro_open.remotecontrol.NetworkLooper.sendLine;

class LooperMessageSender {
  final NetworkLooper looper;
  final TcpConnection server;
  final MainActivity mainActivity;
  float prevX = 0;
  float prevY = 0;
  float preX = 0;
  float preY = 0;
  long ts = 0;
  long te = 0;
  boolean ready = false;
  
  TCPCallback cb = new TCPCallback() {
    @Override
    void callback(TcpConnection result) {
      ready=true;
    }
    
    @Override
    void reject(String message) {
      ready=false;
      mainActivity.runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                     mainActivity.broadcastUDP();
                                     Toast.makeText(mainActivity.getApplicationContext(), "Error sending to server", Toast.LENGTH_SHORT).show();
                                   }
                                 });
    }
  };
  
  public LooperMessageSender(NetworkLooper looper, TcpConnection server, MainActivity mainActivity) {
    this.looper = looper;
    this.server = server;
    this.mainActivity = mainActivity;
  
    sendCommand(new String[]{"hello"});
  }
  
  void sendCommand(String command[]){
    StringBuilder sb = new StringBuilder();
    for (String c: command) {
     sb.append(c);
     sb.append(" ");
    }
    looper.sendMessage(sendLine(server, sb.toString(), cb));
  }
  
}
