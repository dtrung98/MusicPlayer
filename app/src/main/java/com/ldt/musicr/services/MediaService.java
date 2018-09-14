package com.ldt.musicr.services;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ldt.musicr.MediaData.FormatDefinition.PlayController;
import com.ldt.musicr.MediaData.Song_OnLoad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by trung on 7/21/2017.
 */

public class MediaService extends Service {
    public MediaService()
    {
     // blank
    }
    public void setPlayController_Playlists(ArrayList<Song_OnLoad> list)
    {

        playController.setList(list);
    }
    public void setPos(int pos)
    {
        playController.setPosPlaying(pos);
    }
    public PlayController playController = new PlayController();
   private Random rnd= new Random();
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private MediaPlayer mediaPlayer;
    private boolean running = false;
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            Log.d("handleMessage called!", "From Service ");

            final String filePath = playController.getNowPlaying();
            if (!running)
            {  mediaPlayer = new MediaPlayer();
            running = true;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playController.Next();
                    mp.reset();
                    try {
                        mp.setDataSource(playController.getNowPlaying());
                    } catch (IOException e) {

                    }
                    try {
                        mp.prepare();
                    } catch (IOException e) {

                    }
                    mediaPlayer.start();
                }
            });
        }
            else try {
                mediaPlayer.reset();
            } catch (NullPointerException ignored){};

            try {
                mediaPlayer.setDataSource(filePath);
            } catch (IOException ignored) {

            }
            try {
                mediaPlayer.prepare();
            } catch (IOException ignored) {

            }
            mediaPlayer.start();

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
         //   stopSelf(msg.arg1);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("onBind called!","From Service ");
        return null;
    }

 @Override
    public void onCreate()
 {
 //    Toast.makeText(MediaService.this,"Service now created!",Toast.LENGTH_SHORT).show();

     Log.d("onCreate called!","From Service ");
// Start up the thread running the service.  Note that we create a
     // separate thread because the service normally runs in the process's
     // main thread, which we don't want to block.  We also make it
     // background priority so CPU-intensive work will not disrupt our UI.
     HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
     thread.start();

     // Get the HandlerThread's Looper and use it for our Handler
     mServiceLooper = thread.getLooper();
     mServiceHandler = new ServiceHandler(mServiceLooper);


 }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId) {
        Log.d("onStartCommand called!", "From Service ");
        // get the Playlist
        ArrayList<String> myList = intent.getStringArrayListExtra("DanhSachPhat_Data");
        if (myList != null) {
            playController.setData(myList);
        }
         //get the posTop playing
        int pos= intent.getIntExtra("NowPlaying", 0);
        playController.setPosPlaying(pos);
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
//        move(msg);
        MakeThisMediaPlayerVolumeDownButNotSwitchSong(mediaPlayer);
        mServiceHandler.sendMessage(msg);
        return START_STICKY;
    }
    private boolean endMove= true;
    public void move(final Message msg){
        if(mediaPlayer==null) {mServiceHandler.sendMessage(msg); return;}
        if(!mediaPlayer.isPlaying()) {mServiceHandler.sendMessage(msg);return;}
        if(endMove) endMove=false; else return;

        final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int volume_level= am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        ValueAnimator va = ValueAnimator.ofFloat( 1,0f);
        int mDuration = 300; //in millis
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float  number= ((float)(animation.getAnimatedValue()));
          //    am.setStreamVolume(AudioManager.STREAM_MUSIC,(int)number,0);
                mediaPlayer.setVolume(number,number);
               if(number==0) {
                   if(mediaPlayer!=null)
                  mediaPlayer.release();
                //  am.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level, 0);
                   mediaPlayer.setVolume(1,1);
                   endMove=true;
                   mServiceHandler.sendMessage(msg);
               }
            }
        });
    //   va.setRepeatCount(1);
        va.start();
    }
    public void MakeThisMediaPlayerVolumeDownButNotSwitchSong(final MediaPlayer mpx){
        if(mediaPlayer==null) { return;}
        if(!mediaPlayer.isPlaying()) {return;}
        if(endMove) endMove=false;
        final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int volume_level= am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        ValueAnimator va = ValueAnimator.ofFloat( 1,0f);
        int mDuration = 350; //in millis
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float  number= ((float)(animation.getAnimatedValue()));
                //    am.setStreamVolume(AudioManager.STREAM_MUSIC,(int)number,0);
                mpx.setVolume(number,number);
                if(number==0) {
                    mpx.reset();
                        mpx.release();

                    //  am.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level, 0);
                    endMove=true;
                }
            }
        });
        //   va.setRepeatCount(1);
        va.start();
    }
    @Override
    public void onDestroy()
    {
     super.onDestroy();
    }
}
