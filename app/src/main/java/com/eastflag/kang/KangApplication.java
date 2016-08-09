package com.eastflag.kang;

import android.app.Application;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by eastflag on 2016-01-08.
 */
public class KangApplication extends Application {
    private SoundPool mSoundPool;
    public static KangApplication sApp;

    private HashMap<Integer, Integer> mSoundMap = new HashMap<Integer, Integer>();

    private final int SOUND_BUTTON = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        sApp = this;

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        mSoundMap.put(SOUND_BUTTON, mSoundPool.load(getApplicationContext(), R.raw.button_click, 1));
    }

    public void soundButton() {
        mSoundPool.play(mSoundMap.get(SOUND_BUTTON), 0.5f, 0.5f, 0, 0, 1);
    }
}
