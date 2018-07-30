package edu.niu.z1807314.udpbroadcaster;

import android.content.Context;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Vijay";
    int count = 0;
    Button startBroadcast,stopBroadcast;
    String messageString = "Hello";
    int portNum = 11111;
    //String ipAddress = "10.0.10.2";
    String ipAddress = "127.0.0.1";
    DatagramSocket s = new DatagramSocket();
    TextView status,portval,ipval;
    Timer t = new Timer();

    public MainActivity() throws SocketException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBroadcast = findViewById(R.id.start_broadcast);
        stopBroadcast = findViewById(R.id.stop_broadcast);
        status = findViewById(R.id.StatusValue);
        portval = findViewById(R.id.PORTVAL);
        ipval = findViewById(R.id.IPVAL);
        portval.setText(String.valueOf(portNum));
        ipval.setText(ipAddress);
        startBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Starting Thread");
                status.setText("Broadcasting...");
                status.setTextColor(Color.GREEN);
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String msg = updateString(count);
                                    InetAddress local = InetAddress.getByName(ipAddress);
                                    int msgLength = msg.length();
                                    byte[] message = msg.getBytes();
                                    DatagramPacket p = new DatagramPacket(message,msgLength,local,portNum);
                                    count++;
                                    s.setBroadcast(true);
                                    //s.setSoTimeout(2000);
                                    s.send(p);
                                    String str = new String(p.getData(),"UTF-8");
                                    Log.d(TAG,"Sending Datagram..." + str);
                                    //s.close();
                                }
                                catch (SocketException se) {
                                    Log.d(TAG,"SE");
                                    Log.e(TAG,se.toString());
                                } catch (IOException ie) {
                                    Log.d(TAG,"IE");
                                    Log.e(TAG,ie.toString());
                                } catch (Exception e) {
                                    Log.d(TAG,"E");
                                    Log.e(TAG,e.toString());
                                }
                            }
                        });
                        thread.start();
                    }
                };
                t = new Timer();
                t.schedule(timerTask,2000,2000);
            }
        });
        stopBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status.setText("Stopped");
                status.setTextColor(Color.RED);
                t.cancel();
                count = 0;
                Log.d(TAG,"Stopped Thread");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Stopped Thread");
        t.cancel();
        s.close();
        count = 0;
    }

    private String updateString(int count) {
        return (messageString + ":" + count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"On Resume");
    }
}
