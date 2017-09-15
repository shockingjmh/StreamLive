package com.example.shockingjmh.streamlive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by shockingjmh on 2016-06-01.
 */
public class Splash extends Activity {

    private ImageView image;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        image = (ImageView)findViewById(R.id.music);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        image.setAnimation(animation);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000);
    }

    private class splashhandler implements Runnable{

        @Override
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            Splash.this.finish();
        }
    }
}
