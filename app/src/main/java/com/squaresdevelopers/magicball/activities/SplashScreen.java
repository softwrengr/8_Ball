package com.squaresdevelopers.magicball.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squaresdevelopers.magicball.R;

public class SplashScreen extends AppCompatActivity {


    private ImageView bounceBallImage;
    private TextView tv8Ball;

    private  String TAG = "AnimationStarter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);

        tv8Ball = findViewById(R.id.tv_8ball);

        flipX();
        flipY();


//        bounceBallImage = findViewById(R.id.bounceBallImage);
//
//        bounceBallImage.clearAnimation();
//        TranslateAnimation transAnim = new TranslateAnimation(0, 0, 0,
//                getDisplayHeight() / 2);
//        transAnim.setStartOffset(500);
//        transAnim.setDuration(5000);
//        transAnim.setFillAfter(true);
//        transAnim.setInterpolator(new BounceInterpolator());
//        transAnim.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//                Log.i(TAG, "Starting button dropdown animation");
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Log.i(TAG,
//                        "Ending button dropdown animation. Clearing animation and setting layout");
//                bounceBallImage.clearAnimation();
//                final int left = bounceBallImage.getLeft();
//                final int top = bounceBallImage.getTop();
//                final int right = bounceBallImage.getRight();
//                final int bottom = bounceBallImage.getBottom();
//                bounceBallImage.layout(left, top, right, bottom);
//
//            }
//        });
//        bounceBallImage.startAnimation(transAnim);
//

        new Handler().

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }
                }, 4000);
    }

//    private int getDisplayHeight() {
//        return this.getResources().getDisplayMetrics().heightPixels;
//    }



    private void flipX() {
        ObjectAnimator flip2 = ObjectAnimator.ofFloat(tv8Ball, "rotationX", 0f, 360f);
        ;
        flip2.setDuration(2000);
        flip2.start();
    }


    private void flipY() {
        ObjectAnimator flip2 = ObjectAnimator.ofFloat(tv8Ball, "rotationY", 0f, 360f);
        flip2.setDuration(2000);
        flip2.start();
    }
}