package de.pro_open.remotecontrol;

import JavaUtils.TCPManager.TcpConnection;
import android.view.MotionEvent;
import android.view.View;

import static de.pro_open.remotecontrol.NetworkLooper.sendLine;

public class onClickSender extends LooperMessageSender implements View.OnClickListener {
  
  onClickSender(NetworkLooper looper, TcpConnection server, MainActivity mainActivity){
    super(looper, server, mainActivity);
  }
  
  @Override
  public void onClick(View v) {
  
    if(server==null){
      System.err.println("Error getting server");
      return;
    }

    sendCommand(new String[]{"click"});
  }
}
