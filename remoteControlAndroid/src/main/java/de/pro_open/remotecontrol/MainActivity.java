package de.pro_open.remotecontrol;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import java.net.InetAddress;

import JavaUtils.TCPManager.TcpConnection;
import JavaUtils.UDPUtils.UDPBroadcast;

public class MainActivity extends AppCompatActivity {
  TcpConnection conToServer;
  NetworkLooper events = new NetworkLooper();
  Boolean server_on = false;
  String server_ip = "unset";
  
  View.OnClickListener oc = new View.OnClickListener() {
    @Override
    public void onClick(final View view) {
      server_ip = view.getTag().toString();
      MainActivity.this.setTitle(getString(R.string.ConnectedTitle, server_ip));
      
      TCPCallback cb = new TCPCallback() {
        @Override
        void callback(TcpConnection result) {
          connectionSuccess(result);
        }
  
        @Override
        void reject(String message) {
          connectionRejected(message);
        }
      };
      events.sendMessage(NetworkLooper.TCPConnect(server_ip, 45340, cb));
      
    }
  };
  
  private void connectionRejected(String s){
    Toast.makeText(MainActivity.this.getBaseContext().getApplicationContext(),
      "error connecting to server",
      Toast.LENGTH_LONG ).show();
  }
  
  @SuppressLint("ClickableViewAccessibility")
  private void connectionSuccess(final TcpConnection connection) {
    runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      conToServer = connection;
                      System.out.println("clientConnection success " + connection.isConnected());
  
                      final RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_main);
                      rl.removeAllViews();
//                      rl.setOnTouchListener(new onTouchSender(events, conToServer, MainActivity.this));
                      rl.setOnClickListener(new onClickSender(events, conToServer, MainActivity.this));
                    }
                  });
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    broadcastUDP();
  }
  
  @Override
  public void onBackPressed() {
    
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle(R.string.app_name);
    builder.setIcon(R.mipmap.ic_launcher);
    builder.setMessage("Do you want to exit?")
      .setCancelable(false)
      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          conToServer.disconnect();
          finish();
        }
      })
      .setNegativeButton("No", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
        }
      });
    AlertDialog alert = builder.create();
    alert.show();
    
  }
  
  public void broadcastUDP() {
    final int udpWaitTimeout = 2000;
    setContentView(R.layout.activity_main);
  
    setTitle("Searching for server...");
    TextView tv = (TextView) findViewById(R.id.noserveronlinetv);
    tv.setVisibility(View.INVISIBLE);
    LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
    ll.removeAllViews();
    ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
    pb.setVisibility(View.VISIBLE);
    Button b = (Button)findViewById(R.id.retryudpscan);
    b.setVisibility(View.INVISIBLE);
  
    server_on = false;
    server_ip = "unset";
  
    if(events!=null && !events.isAlive()) events.start();
    
    UDPBroadcast.startNewBroadcastRequest(4960, "ping", true, udpWaitTimeout, new UDPBroadcast.UDPBroadcastResponseListener() {
      @Override
      public void process(String response, final InetAddress address) {
        if (response != null && response.equalsIgnoreCase("server_online")) {
          server_on = true;
          final String hostname = address.getHostName();
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              addServerAsOnline(address.getHostAddress(), hostname);
            }
          });
        }
      }
    });
    
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(udpWaitTimeout);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            finishedSearch();
          }
        });
      }
    }).start();
  }
  
  public void addServerAsOnline(String ip, String hostname) {
    System.out.println(ip + ":" + hostname);
    Button server = new Button(this);
    server.setTag(ip);
    server.setText(getString(R.string.serverHostIP,  hostname, ip));
    server.setOnClickListener(oc);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      server.setBackground(getResources().getDrawable(R.drawable.button_border));
    }
    
    LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
    ll.addView(server);
  }
  
  public void finishedSearch() {
    ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
    if (pb != null) {
      pb.setVisibility(View.INVISIBLE);
    }
    if (!server_on) {
      Button b = (Button)findViewById(R.id.retryudpscan);
      b.setVisibility(View.VISIBLE);
      b.setOnClickListener( new View.OnClickListener(){
        @Override
        public void onClick(View v) {
          broadcastUDP();
        }
      } );
      
      TextView tv = (TextView) findViewById(R.id.noserveronlinetv);
      tv.setBackgroundColor(Color.WHITE);
      tv.setText("No Server found!\nDownload the server software for Linux, Windows or Mac on www.test.de");
      tv.setVisibility(View.VISIBLE);
    } else {
    
    }
    
  }
  
  
}