package com.example.bios90.voicedemo2;


import android.app.Application;

import com.sac.speech.Logger;
import com.sac.speech.Speech;


public class App extends Application
{
    String textStr;

    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }

    public String getTextStr()
    {
        return textStr;
    }

    public void setTextStr(String textStr)
    {
        this.textStr = textStr;
    }
}
