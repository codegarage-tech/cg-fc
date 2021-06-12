package com.rc.facecase.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devbrackets.android.exomedia.AudioPlayer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.rc.facecase.model.Items;
import com.rc.facecase.util.AllConstants;

import org.parceler.Parcels;

import static com.rc.facecase.util.AllConstants.EXTRA_ACTION_START;
import static com.rc.facecase.util.AllConstants.EXTRA_ACTION_STOP;
import static com.rc.facecase.util.AllConstants.KEY_INTENT_EXTRA_ACTION;
import static com.rc.facecase.util.AllConstants.KEY_INTENT_EXTRA_MUSIC;
import static com.rc.facecase.util.AllConstants.MEDIA_PLAYBACK_FINISHED;
import static com.rc.facecase.util.AllConstants.MEDIA_PLAYBACK_STOPPED;


public class MediaService extends Service {

    Items music = null;
    AudioPlayer audioPlayer = null;
    Intent broadcastIntentActivityUpdate;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            int action = intent.getIntExtra(KEY_INTENT_EXTRA_ACTION, -1);
            switch (action) {
                case EXTRA_ACTION_START: {
                    Parcelable mParcelableItem = intent.getParcelableExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC);
                    Log.e("getSource",mParcelableItem.toString()+">>>");

                    if (mParcelableItem!= null) {
                        music = Parcels.unwrap(mParcelableItem);
                        Log.d("From service: ", music.toString());
                        Log.e("getSource",music.getSource()+">>>");

                        audioPlayer = new AudioPlayer(getApplicationContext());
                        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        audioPlayer.setDataSource(Uri.parse(music.getSource()));

                        //Set music listener
                        audioPlayer.setOnCompletionListener(new OnCompletionListener() {
                            @Override
                            public void onCompletion() {
                                if (audioPlayer != null) {
                                    audioPlayer.stopPlayback();
                                    audioPlayer.start();

                                    music.setIsPlaying(MEDIA_PLAYBACK_FINISHED);
                                  //  sendUpdateToActivity(music);
                                }
                            }
                        });

                        //Start music
                        audioPlayer.prepareAsync();
                        audioPlayer.start();

                       // updateSeekProgress();
                    }
                    break;
                }
                case EXTRA_ACTION_STOP: {

                    destroyService();

                    break;
                }
                default: {
                    break;
                }
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        destroyService();

        super.onDestroy();
    }

    private void destroyService() {
        if (audioPlayer.isPlaying()) {
            music.setIsPlaying(MEDIA_PLAYBACK_STOPPED);
          //  sendUpdateToActivity(music);

            audioPlayer.stopPlayback();
            audioPlayer.release();
            audioPlayer = null;
        }

//        handler.removeCallbacks(runnableUpdate);
//        handler = null;
    }


}
