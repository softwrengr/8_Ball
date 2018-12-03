package com.squaresdevelopers.magicball.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squaresdevelopers.magicball.R;
import com.squaresdevelopers.magicball.shake.ShakeService;
import com.squaresdevelopers.magicball.utilities.GeneralUtils;
import com.squaresdevelopers.magicball.utilities.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    public static ImageView ivCircle, ivSpeech, ivSpeechDailog;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    private RecognitionProgressView recognitionProgressView;
    private SpeechRecognizer speechRecognizer;
    private Dialog speechDialog;
    private boolean aBooleanSpeech = false;
    String strUserVoice;
    MediaPlayer mediaPlayer;
    TextView tvMagicBallRelpy;
    private Animation shake;
    private TextToSpeech mTTS;

    boolean randomText = true;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        ivCircle = findViewById(R.id.circle_image);
        ivSpeech = findViewById(R.id.iv_speech);
        tvMagicBallRelpy = findViewById(R.id.magicaball_Answer);

         initializeTextToSpeach();

        GeneralUtils.permission(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.mysterious_sound);

        //recognizing real user voice
        ivSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customCartAdditionDialog();
                aBooleanSpeech = true;
                randomText = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (aBooleanSpeech) {
                            speechDialog.dismiss();
                        }
                    }
                }, 12000);

                if (!NetworkUtils.isNetworkConnected(MainActivity.this)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speechDialog.dismiss();
                            showRandomlyAnswer();

                        }
                    }, 4000);
                }

            }
        });

        //showing random 8Ball Questions
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (randomText) {
                    showRandomlyQuestions();
                    handler.postDelayed(this, 20000);
                }
            }
        };
        handler.postDelayed(runnable, 5000);

    }

    public void customCartAdditionDialog() {
        speechDialog = new Dialog(this);
        speechDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        speechDialog.setContentView(R.layout.custom_speech_dialog);
        speechDialog.setCancelable(true);
        int[] colors = {
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5)
        };

        int[] heights = {20, 24, 18, 23, 16};


        shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        ivSpeechDailog = speechDialog.findViewById(R.id.iv_speech_dialog);
        ivSpeechDailog.setAnimation(shake);


        recognitionProgressView = speechDialog.findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                showResults(results);
            }
        });
        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.setCircleRadiusInDp(2);
        recognitionProgressView.setSpacingInDp(2);
        recognitionProgressView.setIdleStateAmplitudeInDp(2);
        recognitionProgressView.setRotationRadiusInDp(10);
        recognitionProgressView.play();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            recognitionProgressView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recognitionProgressView.stop();
                    recognitionProgressView.play();
                    startRecognition();
                }
            }, 50);
        }


        speechDialog.show();
        speechDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        speechDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                aBooleanSpeech = false;
            }
        });

    }


    private void startRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, MainActivity.this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        speechRecognizer.startListening(intent);

    }

    private void showResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        strUserVoice = matches.get(0);
        recognitionProgressView.stop();
        speechDialog.dismiss();

        String strSpeech = matches.get(0);

        if (strSpeech.contains("suicide")) {
            GeneralUtils.watchYoutubeVideo(MainActivity.this, "sRo5Db_7yVI");
        }

        flipX();
        flipY();

        //showing answer of Magice Ball
        showAnswer();
        aBooleanSpeech = false;


    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Requires RECORD_AUDIO permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION_CODE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, ShakeService.class);
        startService(intent);
        randomText = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(this, ShakeService.class);
        stopService(intent);

        randomText = false;
    }


    private void flipX() {
        ObjectAnimator flip2 = ObjectAnimator.ofFloat(tvMagicBallRelpy, "rotationX", 0f, 360f);
        ;
        flip2.setDuration(3000);
        flip2.start();
    }


    private void flipY() {
        ObjectAnimator flip2 = ObjectAnimator.ofFloat(tvMagicBallRelpy, "rotationY", 0f, 360f);
        flip2.setDuration(5000);
        flip2.start();
    }


    private void showAnswer() {
        mediaPlayer.start();

        if (strUserVoice.equals(getResources().getString(R.string.should_i_go_to_school))) {
            tvMagicBallRelpy.setVisibility(View.VISIBLE);
            tvMagicBallRelpy.setText(getResources().getString(R.string.of_course));
            speak(getResources().getString(R.string.of_course));

        } else if (strUserVoice.equals(getResources().getString(R.string.should_i_take_dinner))) {
            tvMagicBallRelpy.setVisibility(View.VISIBLE);
            tvMagicBallRelpy.setText(getResources().getString(R.string.go_for_it));
            tvMagicBallRelpy.setText(getResources().getString(R.string.cant_help));
            speak(getResources().getString(R.string.cant_help));

        } else if (strUserVoice.equals(getResources().getString(R.string.should_project))) {
            tvMagicBallRelpy.setVisibility(View.VISIBLE);
            tvMagicBallRelpy.setText(getResources().getString(R.string.dont_even_think));
            speak(getResources().getString(R.string.dont_even_think));

        } else if (strUserVoice.equals(getResources().getString(R.string.new_job))) {
            tvMagicBallRelpy.setVisibility(View.VISIBLE);
            tvMagicBallRelpy.setText(getResources().getString(R.string.just_do_that));
            speak(getResources().getString(R.string.just_do_that));

        } else if (strUserVoice.equals(getResources().getString(R.string.quite_job))) {
            tvMagicBallRelpy.setVisibility(View.VISIBLE);
            tvMagicBallRelpy.setText(getResources().getString(R.string.no));
            speak(getResources().getString(R.string.no));

        } else if (strUserVoice.equals(getResources().getString(R.string.travel))) {
            tvMagicBallRelpy.setVisibility(View.VISIBLE);
            tvMagicBallRelpy.setText(getResources().getString(R.string.dont_even_think));
            speak(getResources().getString(R.string.dont_even_think));

        } else if (strUserVoice.equals(getResources().getString(R.string.my_master))) {
            tvMagicBallRelpy.setVisibility(View.VISIBLE);
            tvMagicBallRelpy.setText(getResources().getString(R.string.definately_yes));
            speak(getResources().getString(R.string.definately_yes));

        }
        else {
            Toast.makeText(this, strUserVoice, Toast.LENGTH_SHORT).show();
            if (strUserVoice.contains("suicide")) {
                tvMagicBallRelpy.setVisibility(View.VISIBLE);
                tvMagicBallRelpy.setText(getResources().getString(R.string.no));
                speak(getResources().getString(R.string.no));

            }
            else if (strUserVoice.contains("hurt") || strUserVoice.contains("heart")) {
                tvMagicBallRelpy.setVisibility(View.VISIBLE);
                tvMagicBallRelpy.setText(getResources().getString(R.string.no));
                speak(getResources().getString(R.string.no));

            }
            else if (strUserVoice.contains("stab")) {
                tvMagicBallRelpy.setVisibility(View.VISIBLE);
                tvMagicBallRelpy.setText(getResources().getString(R.string.no));
                speak(getResources().getString(R.string.no));

            }
            else if (strUserVoice.contains("shoot")) {
                tvMagicBallRelpy.setVisibility(View.VISIBLE);
                tvMagicBallRelpy.setText(getResources().getString(R.string.no));
                speak(getResources().getString(R.string.no));

            }
            else if (strUserVoice.contains("drawn")) {
                tvMagicBallRelpy.setVisibility(View.VISIBLE);
                tvMagicBallRelpy.setText(getResources().getString(R.string.no));
                speak(getResources().getString(R.string.no));

            }
            else if (strUserVoice.contains("hang")) {
                tvMagicBallRelpy.setVisibility(View.VISIBLE);
                tvMagicBallRelpy.setText(getResources().getString(R.string.no));
                speak(getResources().getString(R.string.no));

            }
            else if (strUserVoice.contains("jump")) {
                tvMagicBallRelpy.setVisibility(View.VISIBLE);
                tvMagicBallRelpy.setText(getResources().getString(R.string.no));
                speak(getResources().getString(R.string.no));

            }

            else {
                showRandomlyAnswer();
            }


        }
    }


    private void initializeTextToSpeach() {
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (mTTS.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, "No TTS in your device", Toast.LENGTH_SHORT).show();
                    //finish();
                } else {
                    mTTS.setLanguage(Locale.US);
                    speak(getResources().getString(R.string.shoot_me));
                }
            }
        });
    }

    private void speak(String s) {
        if (Build.VERSION.SDK_INT >= 21) {
            mTTS.speak(s, TextToSpeech.QUEUE_ADD, null, null);

        } else {
            mTTS.speak(s, TextToSpeech.QUEUE_ADD, null);
        }
    }


    private void showRandomlyAnswer() {
        String[] arrayOfStrings = MainActivity.this.getResources().getStringArray(R.array.random_answer_array);
        String randomString = arrayOfStrings[new Random().nextInt(arrayOfStrings.length)];

        tvMagicBallRelpy.setVisibility(View.VISIBLE);
        flipX();
        flipY();

        tvMagicBallRelpy.setText(randomString);
        speak(randomString);
    }

    private void showRandomlyQuestions() {
        String[] arrayOfStrings = MainActivity.this.getResources().getStringArray(R.array.random_question_array);
        String randomString = arrayOfStrings[new Random().nextInt(arrayOfStrings.length)];

        tvMagicBallRelpy.setVisibility(View.VISIBLE);
        flipX();
        flipY();

        tvMagicBallRelpy.setText(randomString);
        speak(randomString);
    }
}