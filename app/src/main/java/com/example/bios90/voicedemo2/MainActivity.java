package com.example.bios90.voicedemo2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SpeechDelegate,Speech.stopDueToDelay
{

    private static final String TAG = "MainActivity";

    App app;

    private boolean bound = false;
    TextView textView,activated;
    Intent intent;

    boolean getText;

    Button start,stop,reload;

    WebView webView;

    //////////////////
    Locale locale;
    public static SpeechDelegate delegate;

    RxPermissions rxPermissions;

    String fullStr="";
    String partStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start=(Button)findViewById(R.id.btnStart);
        stop=(Button)findViewById(R.id.btnCancel);
        textView =(TextView)findViewById(R.id.tvForText);
        activated=(TextView)findViewById(R.id.tvActivated);
        reload=findViewById(R.id.btnReload);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://bestbanksapp.ru/index.html");

        app=(App)getApplicationContext();
        rxPermissions = new RxPermissions(this);
        locale = new Locale("en","US");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},123);
        }



        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initSpeechRecognizer();
            }
        });

        stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopRecognizer();
            }
        });

        reload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                webView.loadUrl("http://bestbanksapp.ru/index.html");
            }
        });

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},123);
    }

    private void initSpeechRecognizer()
    {

        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening())
        {
            Speech.getInstance().stopListening();
            muteBeepSoundOfRecorder();
        } else
            {
            System.setProperty("rx.unsafe-disable", "True");

                    try
                    {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().setLocale(locale).startListening(null, this);
                    }
                    catch (Exception exc)
                    {
                        //showSpeechNotSupportedDialog();
                    }

            };
            muteBeepSoundOfRecorder();
    }




    @Override
    public void onSpecifiedCommandPronounced(String event)
    {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try
        {
            if (Speech.getInstance().isListening())
            {
                muteBeepSoundOfRecorder();
                Speech.getInstance().stopListening();
            } else
                try
                {
                    Speech.getInstance().stopTextToSpeech();
                    Speech.getInstance().setLocale(locale).startListening(null, this);
                } catch (Exception exc)
                {
                    //showSpeechNotSupportedDialog();
                }

            muteBeepSoundOfRecorder();
        }
        catch (Exception e)
        {

        }

    }

    private void muteBeepSoundOfRecorder()
    {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    @Override
    public void onStartOfSpeech()
    {
        Log.e(TAG, "onStartOfSpeech: " );
    }

    @Override
    public void onSpeechRmsChanged(float value)
    {

    }

    @Override
    public void onSpeechPartialResults(List<String> results)
    {
        Log.e(TAG, "onSpeechPartialResults: ");
        makeText(null,results.get(0));
    }

    @Override
    public void onSpeechResult(String result)
    {
        Log.e(TAG, "onSpeechResult: ");
        makeText(result,null);
    }

    void makeText(String full,String part)
    {
        String newStr="";
        if(full!=null)
        {
            newStr = fullStr+" "+full;
            if(newStr.length()>320)
            {
                int i = full.length();
                try
                {
                    newStr = newStr.substring(i);
                }
                catch (Exception e)
                {

                }
            }

            fullStr=newStr;
        }
        else if(part!=null)
        {
            newStr = fullStr+" "+part;

            if(newStr.length()>320)
            {
                int i = part.length();
                try
                {
                    newStr = newStr.substring(i);
                }
                catch (Exception e)
                {

                }
            }
        }

        textView.setText(newStr);
        //if(part!=null)
        {
            sendTextToWeb(newStr);
        }
    }

    void sendTextToWeb(String str)
    {
        Log.e(TAG, "sendTextToWeb: " );
        //webView.loadUrl("javascript:myFunction('" + str + "')");
        webView.loadUrl("javascript:myFunction('" + str + "')");
    }

    @Override
    protected void onDestroy()
    {
        // prevent memory leaks when activity is destroyed
        stopRecognizer();
        super.onDestroy();
    }

    private void stopRecognizer()
    {
        try
        {
            Speech.getInstance().shutdown();
        }
        catch (Exception e)
        {

        }
    }

    //region Custom JavaScript Interface
    private class WebAppInterface
    {
        Context mContext;

        WebAppInterface(Context c)
        {
            mContext = c;
        }

        @android.webkit.JavascriptInterface
        public void showToast1(String str)
        {
            try
            {
                textView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        stop.performClick();
                    }
                });
            }
            catch (Exception e)
            {

            }
        }

        @android.webkit.JavascriptInterface
        public void showToast(String toast)
        {

            Toast.makeText(mContext, "Распознование Активированно", Toast.LENGTH_SHORT).show();

            try
            {
                textView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textView.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    start.performClick();
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, "run: ____-----___----"+e.getMessage() );
                                }
                            }
                        });
                    }
                });
            }
            catch (Exception e)
            {

            }
        }
    }
    //endregion
}