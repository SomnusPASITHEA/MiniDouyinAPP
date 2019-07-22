package com.example.minidouyinapp;




import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

public class PlayVideoActivity extends Activity {

    private String url;
    private VideoView videoView;
    //    private SeekBar videoProgress;

    private String[] mPermissionArray = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
    };

//    PlayVideoActivity(String url){
//        this.url = url;
//    }
//    private Handler handler = new Handler();
//    private Runnable runnable = new Runnable() {
//        public void run() {
//            if (videoView.isPlaying()) {
//                int current = videoView.getCurrentPosition();
//                videoProgress.setProgress(current);
//            }
//            handler.postDelayed(runnable, 500);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_play_video);
        //setTitle("视频播放");
        url = getIntent().getStringExtra("videoUrl");
        ActivityCompat.requestPermissions(this,mPermissionArray, PackageManager.PERMISSION_GRANTED);
        videoView = findViewById(R.id.videoView);
//        url = "https://sf3-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt7217xbo2wz3cem41/1634743fa99a7496_1563609307692.mp4";
        videoView.setVideoPath(url);
//        videoView.setVideoPath(getVideoPath(R.raw.bytedance));


//        videoProgress = findViewById(R.id.videoProgress);
//        videoProgress.setMax(videoView.getDuration());//修改progress范围


//        handler.postDelayed(runnable,0);
        MediaController mc = new MediaController(PlayVideoActivity.this);
        videoView.setMediaController(mc);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector mGesture;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGesture == null) {
//                    原参数context
                    mGesture = new GestureDetector(PlayVideoActivity.this, new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDown(MotionEvent e) {
                            //返回false的话只能响应长摁事件
                            return true;
                        }

                        @Override
                        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                            return super.onScroll(e1, e2, distanceX, distanceY);
                        }
                    });

                    mGesture.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                        //                        触发单击视频事件
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            if(videoView.isPlaying()){
                                videoView.pause();
                            } else {
                                videoView.start();
                            }
                            return true;
                        }
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            Toast.makeText(PlayVideoActivity.this, "假装有赞", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        @Override
                        public boolean onDoubleTapEvent(MotionEvent e) {
                            return false;
                        }
                    });

                }
                return mGesture.onTouchEvent(event);
            }
        });

//        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                return false;
//            }
//        });


//        btn = findViewById(R.id.playOrPause);
//        btn.setOnClickListener(new View.OnClickListener() {
//            //            boolean play = false;//再次点击按钮时，flase表示暂停，true表示开始
//            @Override
//            public void onClick(View view) {
//                handler.postDelayed(runnable, 0);
//                if(!(videoView.isPlaying())){
//                    handler.postDelayed(runnable,0);
//                    videoView.start();
//                    btn.setText("Pause");
////                    videoProgress.setMax(videoView.getDuration());//修改progress范围
//
//                }else{
//                    videoView.pause();
//                    btn.setText("Play");
//                }
//            }
//        });

        //进度条拖拽
//        videoProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(fromUser == true) {
//                    videoView.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }


    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(runnable);
    }
}

