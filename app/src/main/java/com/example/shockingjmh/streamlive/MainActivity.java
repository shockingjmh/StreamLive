package com.example.shockingjmh.streamlive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.app.Activity;

import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity{

    private Button startButton;
    private EditText InputIP, InputPort;

    private boolean status = true;
    private String ip = null;
    private int port = 0;
    private AlertDialog.Builder alert;
    private Intent streamPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputIP = (EditText) findViewById(R.id.InputIP);
        InputPort = (EditText) findViewById(R.id.InputPort);

        startButton = (Button) findViewById(R.id.start_button);

        startButton.setOnClickListener(startListener);

        alert = new AlertDialog.Builder(this);
        alert.setMessage("ip와 port를 입력을 해주세요!");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        streamPage = new Intent(this, Streaming.class);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

    }

    private final OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {

            try {
                ip = InputIP.getText().toString();
                port = Integer.parseInt(InputPort.getText().toString());
            }catch (NumberFormatException e){
                alert.show();
            }
            if(ip != null && port != 0){
                streamPage.putExtra("ip",ip);
                streamPage.putExtra("port", port);
                startActivityForResult(streamPage, 0);
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setMessage("종료하시겠습니까?");
            alertDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            alertDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setTitle("앱 종료");
            alert.show();
        }
        return false;
    }
}
