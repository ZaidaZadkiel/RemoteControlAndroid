package de.pro_open.remotecontrol;

import JavaUtils.TCPManager.TcpConnection;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import static de.pro_open.remotecontrol.NetworkLooper.sendLine;

public class onTouchSender extends LooperMessageSender implements View.OnTouchListener {

  onTouchSender(NetworkLooper looper, TcpConnection server, MainActivity mainActivity){
    super(looper, server, mainActivity);
  }
  
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    if(server==null){
      System.err.println("Error getting server");
      return false;
    }

    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
      prevX = motionEvent.getX();
      prevY = motionEvent.getY();
      preX = motionEvent.getX();
      preY = motionEvent.getY();
      ts = System.currentTimeMillis();
    }
    
    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
      sendCommand(
        new String[]{
          "mouse",
          Integer.toString(Math.round(motionEvent.getX() - prevX)),
          Integer.toString(Math.round(motionEvent.getY() - prevY))
        });
      
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
        sendCommand(new String[]{"leftClick"});
      } else if ((te - ts) > 150 && (int) (motionEvent.getX() - preX) < 20 && (int) (motionEvent.getY() - preY) < 20) {
        sendCommand(new String[]{"rightClick"});
      }
    }
    return true;
  }
}
