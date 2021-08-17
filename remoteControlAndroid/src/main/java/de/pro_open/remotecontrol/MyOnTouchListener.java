package de.pro_open.remotecontrol;

import JavaUtils.TCPManager.TcpConnection;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static de.pro_open.remotecontrol.NetworkLooper.sendLine;

class MyOnTouchListener implements View.OnTouchListener {
  private final NetworkLooper looper;
  private final TcpConnection server;
  private final MainActivity mainActivity;
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
      //MainActivity.this.runOnUiThread(new Toast(MainActivity.this.getApplicationContext(), "Error sending socket", Toast.LENGTH_SHORT));
    }
  };
  
  public MyOnTouchListener(NetworkLooper looper, TcpConnection server, MainActivity mainActivity) {
    this.looper = looper;
    this.server = server;
    this.mainActivity = mainActivity;
  
    looper.sendMessage(sendLine(server, "hello", cb));
  }
  
  
  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    if(server==null){
      System.err.println("Error getting server");
      return false;
    }
    
    System.out.println("server ontouch " + (server.isConnected() ? "connected" : "not connected"));
    
    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
      prevX = motionEvent.getX();
      prevY = motionEvent.getY();
      preX = motionEvent.getX();
      preY = motionEvent.getY();
      ts = System.currentTimeMillis();
    }
    
    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
      looper.sendMessage(sendLine(
        server,
    "mouse " + (int) (motionEvent.getX() - prevX) + " " + (int) (motionEvent.getY() - prevY),
        cb
      ));
      
      prevX = motionEvent.getX();
      prevY = motionEvent.getY();
      te = System.currentTimeMillis();
  
    }
    
    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
      te = System.currentTimeMillis();
      if (
        (te - ts) < 300 &&
        (int) (motionEvent.getX() - preX) < 20 &&
        (int) (motionEvent.getY() - preY) < 20
      ) {
        looper.sendMessage(sendLine(server, "leftClick", cb));
      } else if ((te - ts) > 150 && (int) (motionEvent.getX() - preX) < 20 && (int) (motionEvent.getY() - preY) < 20) {
        looper.sendMessage(sendLine(server, "rightClick", cb));
      }
    }
    return true;
  }
}
