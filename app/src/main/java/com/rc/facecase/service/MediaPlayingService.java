package com.rc.facecase.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devbrackets.android.exomedia.AudioPlayer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.rc.facecase.R;
import com.rc.facecase.activity.PictureGamePlayActivity;
import com.rc.facecase.model.Items;
import com.rc.facecase.util.AllConstants;
import com.rc.facecase.util.Logger;

import org.parceler.Parcels;

import java.io.IOException;

import static com.rc.facecase.util.AllConstants.EXTRA_ACTION_START;
import static com.rc.facecase.util.AllConstants.EXTRA_ACTION_STOP;
import static com.rc.facecase.util.AllConstants.KEY_INTENT_BACKGROUND_MUSIC_SET;
import static com.rc.facecase.util.AllConstants.KEY_INTENT_EXTRA_ACTION;
import static com.rc.facecase.util.AllConstants.MEDIA_PLAYBACK_FINISHED;
import static com.rc.facecase.util.AllConstants.MEDIA_PLAYBACK_STOPPED;
import static com.reversecoder.library.statelayout.StateLayout.TAG;


public class MediaPlayingService extends Service {

    Items music = null;
    MediaPlayer audioPlayer = null;


    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            int action = intent.getIntExtra(KEY_INTENT_EXTRA_ACTION, -1);
            int bgMusicPlay = intent.getIntExtra(KEY_INTENT_BACKGROUND_MUSIC_SET, -1);
         //   String bgMusicPlay = intent.getExtras().getString(BACKGROUND_MUSIC_SET);
            switch (action) {
                case EXTRA_ACTION_START: {
                  //  Parcelable mParcelableItem = intent.getParcelableExtra(AllConstants.KEY_INTENT_EXTRA_MUSIC);
                    Log.e("bgMusicPlay",bgMusicPlay+">>>");
                    if (bgMusicPlay != -1){
//                    if (mParcelableItem!= null) {
//                        music = Parcels.unwrap(mParcelableItem);

                        //Set music listener

//                        audioPlayer = new MediaPlayer();
//                        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                      //  audioPlayer.setDataSource(Uri.parse(music.getSource()));

                        if (bgMusicPlay == 1) {
                            audioPlayer = MediaPlayer.create(getApplicationContext(), R.raw.timer);
                        } else if (bgMusicPlay == 2){
                            audioPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tada);

                        }

                        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (audioPlayer != null) {
                                    audioPlayer.stop();
                                    audioPlayer.start();

                                  //  music.setIsPlaying(MEDIA_PLAYBACK_FINISHED);
                                    //  sendUpdateToActivity(music);
                                }
                            }
                        });

                        //Start music
//                        audioPlayer.prepareAsync();
                        audioPlayer.start();
                     }
                       // updateSeekProgress();
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
        if ( audioPlayer != null) {
       //     music.setIsPlaying(MEDIA_PLAYBACK_STOPPED);
          //  sendUpdateToActivity(music);

            audioPlayer.stop();
            audioPlayer.release();
            audioPlayer = null;
        }

//        handler.removeCallbacks(runnableUpdate);
//        handler = null;
    }


}
