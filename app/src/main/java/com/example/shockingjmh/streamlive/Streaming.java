package com.example.shockingjmh.streamlive;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.net.SocketException;

/**
 * Created by shockingjmh on 2016-06-01.
 */
public class Streaming extends Activity {

    private ImageButton start_streaming;
    private ImageButton stop_streaming;
    private ImageButton exit;
    private TextView textView;
    private Bundle extras;
    private String AUDIO_IP;
    private int AUDIO_PORT;
    private boolean streamingState = true;
    private boolean recvState = true;
    static final String LOG_TAG = "UdpStream";
    static final int SAMPLE_RATE = 44100;
    static final int SAMPLE_INTERVAL = 20; // milliseconds
    static final int SAMPLE_SIZE = 5; // bytes per sample
    static final int BUF_SIZE = 3800;//SAMPLE_INTERVAL*SAMPLE_INTERVAL*SAMPLE_SIZE*2;

    private Intent intent;
    private MulticastSocket sock;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streaming);

        extras = getIntent().getExtras();
        AUDIO_IP = extras.getString("ip");
        AUDIO_PORT = extras.getInt("port");

        start_streaming = (ImageButton)findViewById(R.id.start_streaming);
        stop_streaming = (ImageButton)findViewById(R.id.stop_streaming);
        exit = (ImageButton)findViewById(R.id.exit);
        textView = (TextView)findViewById(R.id.connectState);

        intent = new Intent(this, MainActivity.class);


        start_streaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecvAudio();
                streamingState = true;
                recvState = true;
                textView.setText("Connect");
            }
        });

        stop_streaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("pause");
                recvState = false;
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                streamingState = false;
                recvState = false;
                textView.setText("disconnect");
                if(sock != null) {
                    sock.close();
                }
                finish();
            }
        });

        if(this.isFinishing() == true){
            Log.e("finish", "finish");
            streamingState = false;
            recvState = false;
            textView.setText("Bye");

            if(sock != null) {
                sock.close();
            }
            this.finish();
        }
    }
    public void RecvAudio()
    {
        Thread thrd = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Log.e(LOG_TAG, "start recv thread, thread id: "
                        + Thread.currentThread().getId());
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE,
                        AudioTrack.MODE_STREAM);
                track.play();
                try
                {
                    sock = new MulticastSocket(AUDIO_PORT);
                    InetAddress address = InetAddress.getByName("239.0.0.1");
                    sock.joinGroup(address);

                    byte[] buf = new byte[BUF_SIZE];

                    DatagramPacket pack = new DatagramPacket(buf, BUF_SIZE);
                    while(streamingState)
                    {
                        while(recvState) {
                            sock.receive(pack);
                            Thread.sleep(3);
                            Log.d(LOG_TAG, "recv pack: " + pack.getLength());
                            track.write(pack.getData(), 0, pack.getLength());
                        }
                    }
                    sock.close();
                }
                catch (SocketException se)
                {
                    Log.e(LOG_TAG, "SocketException: " + se.toString());
                }
                catch (IOException ie)
                {
                    Log.e(LOG_TAG, "IOException" + ie.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } // end run

        });
        thrd.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return false;
    }
}
