package com.tz.music;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tz.music.MusicService.MyBinder;

public class MainActivity extends Activity implements OnSeekBarChangeListener, OnClickListener {
    private SeekBar sb;
    private TextView time_progress;
    private TextView music_duration;
    private ImageView play_pause_music;
    private MyServiceConnection conn;
    private MusicService musicService;
	private Handler handler = new Handler();
	private MediaPlayer player;
	private ImageView next;
	private ImageView pre;
	private TextView musicName;
//	private String [] musics = new String[]{
//			String.valueOf(R.raw.stronger),
//			String.valueOf(R.raw.unbreakble)
//	};
	private List<MusicInfo> musics = new ArrayList<MusicInfo>();
	private List<String> musicsPath = new ArrayList<String>();
	private int index;
	private Runnable run_progress = new Runnable(){
				public void run() {
					sb.setProgress(player.getCurrentPosition());
					time_progress.setText(sencondToString(player.getCurrentPosition()));
					handler.postDelayed(run_progress, 100);
				};
	};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        while(cursor!=null&&cursor.moveToNext()){
        	String path = cursor.getString(cursor.getColumnIndex("_data"));
        	String musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        	int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        	MusicInfo info = new MusicInfo();
        	info.setMusicPath(path);
        	info.setMusicName(musicName);
        	info.setDuration(duration);
        	if(!info.getMusicName().equals("")||!info.getMusicPath().endsWith("")||duration != 0){
        		Log.i("INFO", "music:"+info.getDuration());
        		musics.add(info);
        	}
        }
        Intent service = new Intent(this,MusicService.class);
        this.startService(service);
        conn = new MyServiceConnection();
        this.bindService(service, conn, Context.BIND_AUTO_CREATE);
    }
    
    private class MyServiceConnection implements ServiceConnection{

		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicService.MyBinder mBinder = (MyBinder) service;
			musicService = mBinder.getMusicService();
			
			Log.i("INFO", "service conned");
			player = musicService.player;
			if(!player.isPlaying()){
					playMusicByStatu(0);
					play_pause_music.setImageResource(R.drawable.pause);
					player.setOnCompletionListener(new OnCompletionListener() {
						
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							playMusicByStatu(1);
						}
					});
			}else{
				play_pause_music.setImageResource(R.drawable.pause);
			}
			sb.setMax(player.getDuration());
			handler.removeCallbacks(run_progress);
			handler.post(run_progress);
		}

		public void onServiceDisconnected(ComponentName name) {
			
		}
    	
    }
    
    /**
     * 
     */
	private void initView() {
		sb = (SeekBar) findViewById(R.id.music_progress);
		sb.setOnSeekBarChangeListener(this);
		next = (ImageView) findViewById(R.id.next_music);
		pre = (ImageView) findViewById(R.id.pre_music);
		musicName = (TextView) findViewById(R.id.center_content);
		next.setOnClickListener(this);
		pre.setOnClickListener(this);
		time_progress = (TextView) findViewById(R.id.time_progress);
		music_duration = (TextView) findViewById(R.id.music_duration);
		play_pause_music = (ImageView) findViewById(R.id.play_pause_music);
		play_pause_music.setOnClickListener(this);
	}
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if(fromUser){
			if(player!= null){
				player.seekTo(progress);
			}
		}
	}
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_music:
			playMusicByStatu(1);
			break;
		case R.id.pre_music:
			playMusicByStatu(2);
			break;
		default:
			break;
		}
		if(player!=null){
			if(player.isPlaying()){
				musicService.pause();
				play_pause_music.setImageResource(R.drawable.play);
			}else{
				musicService.conti();
				play_pause_music.setImageResource(R.drawable.pause);
			}
		}
	}
	
	public void playMusicByStatu(int statu){
		switch (statu) {
		case 0:
			break;
		case 1:
			//next
			index++;
			if(index == musics.size()){
				index = 0;
			}
			break;
		case 2:
			//back
			index--;
			if(index == -1){
				index = musics.size() - 1;
			}
			break;
		default:
			break;
		}
		playMusic(index);
		sb.setMax(player.getDuration());
	}
	
	/**
	 * playMusic
	 */
	
	public void playMusic(int index){
		if(musics.size()>0){
			musicService.play(musics.get(index).getMusicPath());
			musicName.setText(musics.get(index).getMusicName());
			music_duration.setText(sencondToString(musics.get(index).getDuration()));
		}
	}
	
	/**
	 * second parse
	 */
	public String sencondToString(int millsenconds){
		int seconds = millsenconds/1000;
		int second = seconds%60;
		int minute = (seconds - second)/60;
		DecimalFormat df = new DecimalFormat("00");
		return df.format(minute)+":"+df.format(second);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(conn!=null){
			this.unbindService(conn);
		}
	}
}