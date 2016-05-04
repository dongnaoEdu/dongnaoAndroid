package com.tz.music;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service{
	MyBinder mBinder = new MyBinder();
	MediaPlayer player = new MediaPlayer();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	
	 @Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}


	class MyBinder extends Binder{
		public MusicService getMusicService(){
			return MusicService.this;
		}
	}
	
	
	public void play(String path){
		player.reset();
		try {
			player.setDataSource(path);
//			player.setDataSource(this, Uri.parse("android.resource://"+this.getPackageName()+"/"+path));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			player.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.setOnPreparedListener(new OnPreparedListener() {
			
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.start();
			}
		});
	}
	
	public void pause(){
		if(player!= null){
			player.pause();
		}
	}
	
	public void stop(){
		player.stop();
		player.release();
	}
	
	
	public void conti(){
		player.start();
	}
}
