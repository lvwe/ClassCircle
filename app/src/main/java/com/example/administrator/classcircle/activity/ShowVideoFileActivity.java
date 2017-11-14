package com.example.administrator.classcircle.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.administrator.classcircle.C;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.NetworkUtil;
import com.example.administrator.classcircle.utils.PixelUtil;

public class ShowVideoFileActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private VideoView mVideoView;
    private LinearLayout mControllerLayout;
    private ImageView mIvController;
    private ImageView mIvScreen;
    private ImageView mIvVolume;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private SeekBar mSeekBarPlay;
    private SeekBar  mSeekBarVolume;
    private ImageView mIvOperationBg;
    private ImageView mIvOperationPercent;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayoutVideoView;
    private RelativeLayout mRelativeLayoutController;
    private FrameLayout mFrameLayoutLoadingProgress;
    private TextView mTvLoading;
    private ProgressBar mProgressBarLoading;

    private AudioManager mAudioManager;
    private Runnable mRunnable;

    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isFullScreen = false;
    private boolean isAdjust = false;  //手势滑动不合法
    private int threshold = 54;   //是否误触的临界值
    private float mBrightness;
    private long lastDown = -1;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_video_file;
    }

    @Override
    protected void init() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initLayout();
        checkNetworkStatesTallUser();
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/swxg2.mp4";

        mUiHandler.sendEmptyMessage(C.UPDATE_UI);

        //mVideoView.setVideoURI(Uri.parse("http://192.168.197.2:8080/video/swxg.mp4"));

        //切换横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hindController();
        mIvScreen.setVisibility(View.GONE);

    }

    private void checkNetworkStatesTallUser() {
        Log.d(TAG, "checkNetworkStatesTallUser: +++++"+networkStates());
        if (networkStates() == C.NO_NETWORK){
            showAlertDialog("","当前无网络，请打开网络从新尝试","确定","");
            mProgressBarLoading.setVisibility(View.GONE);
            mTvLoading.setText("无连接...");
        }else if (networkStates() == C.NETWORK_MOBILE){
            showAlertDialog("","当前为数据流量，是否播放","播放","取消");
        }else{
            Intent intent = getIntent();
            String uri = intent.getStringExtra("FILE_URL");
//            mVideoView.setVideoURI(Uri.parse("http://bmob-cdn-14149.b0.upaiyun.com/2017/10/01/eb91ff0f40e9a0b980e92e78fe3427fd.mp4"));
            mVideoView.setVideoURI(Uri.parse(uri));
            mVideoView.start();
        }

    }


    private int networkStates() {

        if (NetworkUtil.isMobileConnect(this)){
            return C.NETWORK_MOBILE;
        }
        if (NetworkUtil.isWifiConnected(this)){
            return C.WIFI_NETWORK;
        }
        if (NetworkUtil.isNetworkConnected(this)) {
            return C.NETWORK_IS_CONNECTED;
        }

        return C.NO_NETWORK;

    }

    private void showAlertDialog(String title, String msg, String pos, String nav) {
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this,R.style.AlertDialog).create();
        // 设置对话框标题
        isExit.setTitle(title);
        // 设置对话框消息
        isExit.setMessage(msg);
        // 添加选择按钮并注册监听
        isExit.setButton(AlertDialog.BUTTON_POSITIVE, pos, listener);
        isExit.setButton(AlertDialog.BUTTON_NEGATIVE, nav, listener);
        // 显示对话框
        isExit.show();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    if (networkStates() == C.NETWORK_IS_CONNECTED){
                        mVideoView.start();
                    }
                    mProgressBarLoading.setVisibility(View.VISIBLE);
                    mTvLoading.setText("加 载 中...");
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    finish();
                    break;
                default:
                    break;

            }
        }
    };

    private void hindController() {
        if (mRelativeLayoutController.getVisibility() == View.VISIBLE &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mUiHandler.postDelayed(mRunnable = new Runnable() {
                @Override
                public void run() {
                    mRelativeLayoutController.setVisibility(View.GONE);
                }
            }, 3000);

        }
    }

    private void updateTimeWithTimeFormat(TextView textView, int millisecond) {
        int second = millisecond / 1000;
        int h = second / 3600;
        int m = second % 3600 / 60;
        int s = second % 60;
        String str = null;
        if (h != 0) {
            str = String.format("%02d:%02d:%02d", h, m, s);
        } else {
            str = String.format("%02d:%02d", m, s);
        }
        textView.setText(str);
    }

    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == C.UPDATE_UI) {
                int currentPosition = mVideoView.getCurrentPosition();
                int totalDuration = mVideoView.getDuration();
                updateTimeWithTimeFormat(mTvCurrentTime, currentPosition);
                updateTimeWithTimeFormat(mTvTotalTime, totalDuration);
                mSeekBarPlay.setMax(totalDuration);
                mSeekBarPlay.setProgress(currentPosition);
                mUiHandler.sendEmptyMessageDelayed(C.UPDATE_UI, C.SEND_MESSAGE_DELAY);
            }
            if (msg.what == C.DOUBLE_CLICK_EVENT) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mIvController.setImageResource(R.drawable.play_btn_style);
                    mRelativeLayoutController.setVisibility(View.VISIBLE);

                } else {
                    mVideoView.start();
                    mIvController.setImageResource(R.drawable.pause_btn_style);
                    mRelativeLayoutController.setVisibility(View.VISIBLE);
                }
                hindController();
            }
            if (msg.what == C.SHOW_CONTROLLER) {
                if (mRelativeLayoutController.getVisibility() == View.GONE &&
                        getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mRelativeLayoutController.setVisibility(View.VISIBLE);
                    hindController();
                }
            }
        }
    };




    @Override
    protected void initListener() {

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "onError: ---");
                showAlertDialog("", "无法播放此视频", "", "确定");
                return true;     //不弹出系统默认错误
//                return false;    弹出系统默认错误
            }
        });

        //播放完成监听

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: ---");
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared: ---");
                mFrameLayoutLoadingProgress.setVisibility(View.GONE);
            }
        });


        mIvController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mIvController.setImageResource(R.drawable.play_btn_style);
                    mVideoView.pause();
                    mUiHandler.removeMessages(C.UPDATE_UI);
                } else {
                    mIvController.setImageResource(R.drawable.pause_btn_style);
                    mVideoView.start();
                    mUiHandler.sendEmptyMessage(C.UPDATE_UI);
                }
            }
        });

        mSeekBarPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTimeWithTimeFormat(mTvCurrentTime, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUiHandler.removeMessages(C.UPDATE_UI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mVideoView.seekTo(progress);
                mUiHandler.sendEmptyMessage(C.UPDATE_UI);
            }
        });

        mSeekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置当前设备音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mIvScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    //切换竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mUiHandler.removeCallbacks(mRunnable);
                } else {
                    //切换横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    hindController();
                }
            }
        });
        //控制VideoView手势事件
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            float lastX = 0, lastY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        long nowDown = System.currentTimeMillis();
                        if (nowDown - lastDown < C.DOUBLE_CLICK_DELAY_TIME) {
                            mUiHandler.sendEmptyMessage(C.DOUBLE_CLICK_EVENT);
                        } else {
                            lastDown = nowDown;
                            if (mRelativeLayoutController.getVisibility() == View.GONE) {
                                mUiHandler.sendEmptyMessage(C.SHOW_CONTROLLER);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //当前的X    手指滑动的偏移量
                        float deltaX = x - lastX;
                        float deltaY = y - lastY;
                        float absDetlaX = Math.abs(deltaX);  //偏移量的绝对值
                        float absDetlaY = Math.abs(deltaY);
                        Log.d(TAG, "onTouch: ----------absDetlaX" + absDetlaX + "absDetlay" + absDetlaY);
                        if (absDetlaX > threshold && absDetlaY > threshold) {
                            Log.d(TAG, "onTouch: ----------absDetlaX" + absDetlaX + "absDetlay" + absDetlaY);
                            if (absDetlaX < absDetlaY) {
                                isAdjust = true;
                            } else {
                                isAdjust = false;
                            }
                        } else if (absDetlaX < threshold && absDetlaY > threshold) {
                            isAdjust = true;
                        } else if (absDetlaX > threshold && absDetlaY < threshold) {
                            isAdjust = true;
                        }
                        Log.d(TAG, "onTouch: ---手势是否合法" + isAdjust);
                        if (isAdjust) {
                            //判断手势合法后   区分手势调节音量     亮度
                            if (x < mScreenWidth / 2) {
                                //调节亮度
                                if (deltaY > 20) {
                                    //降低亮度
                                } else {
                                }
                                changeBrightness(-deltaY);
                            } else {
                                //调节音量
                                if (deltaY > 20) {
                                    //减小音量
                                } else {
                                }
                                changeVolume(-deltaY);
                            }
                            isAdjust = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mFrameLayout.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
    }

    private void changeBrightness(float deltaX) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        mBrightness = attributes.screenBrightness;
        float index = deltaX / mScreenHeight / 3;
        mBrightness = index + mBrightness;
        if (mBrightness > 1.0f) {
            mBrightness = 1.0f;
        }
        if (mBrightness < 0.01f) {
            mBrightness = 0.01f;
        }
        attributes.screenBrightness = mBrightness;
        if (mFrameLayout.getVisibility() == View.GONE) {
            mFrameLayout.setVisibility(View.VISIBLE);
        }
        mIvOperationBg.setImageResource(R.drawable.video_brightness_bg);
        ViewGroup.LayoutParams layoutParams = mIvOperationPercent.getLayoutParams();
        if ((int) (PixelUtil.dp2px(70) * mBrightness) >= (int)PixelUtil.dp2px(70) *1.0f){
            layoutParams.width = (int) (PixelUtil.dp2px(70) * 1.0f);
        }else {
            layoutParams.width = (int) (PixelUtil.dp2px(70) * mBrightness);
        }
        mIvOperationPercent.setLayoutParams(layoutParams);
        getWindow().setAttributes(attributes);
    }

    private void changeVolume(float value) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index = (int) (value / mScreenHeight * max);
        int volume = Math.max(current + index, 0);
        if (volume == 0) {
            mIvVolume.setImageResource(R.drawable.video_volume_mute);
        } else {
            mIvVolume.setImageResource(R.drawable.video_volume);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        if (mFrameLayout.getVisibility() == View.GONE) {
            mFrameLayout.setVisibility(View.VISIBLE);
        }
        mIvOperationBg.setImageResource(R.drawable.video_voice_bg);
        ViewGroup.LayoutParams layoutParams = mIvOperationPercent.getLayoutParams();
        if ((int) (PixelUtil.dp2px(74) * (float) volume / max) >= PixelUtil.dp2px(74) * 1){
            layoutParams.width = PixelUtil.dp2px(74) * 1;
        }else {
            layoutParams.width = (int) (PixelUtil.dp2px(74) * (float) volume / max);
        }
        mIvOperationPercent.setLayoutParams(layoutParams);
        mSeekBarVolume.setProgress(volume);
    }

    private void setVideoViewScale(int width, int height) {
        //Ctrl+Alt=V 引入变量
        ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mVideoView.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = mRelativeLayoutVideoView.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = height;
        mRelativeLayoutVideoView.setLayoutParams(layoutParams1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mIvVolume.setVisibility(View.VISIBLE);
            mSeekBarVolume.setVisibility(View.VISIBLE);
            isFullScreen = true;
            //移除半屏状态
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            //设置全屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hindController();
            mIvScreen.setImageResource(R.drawable.exit_full_screen_style);
        } else {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dp2px(240));
            mIvVolume.setVisibility(View.GONE);
            mSeekBarVolume.setVisibility(View.GONE);
            isFullScreen = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            mIvScreen.setImageResource(R.drawable.full_screen_style);

        }
    }

    private void initLayout() {
        PixelUtil.initContent(this);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mControllerLayout = (LinearLayout) findViewById(R.id.controller_layout);
        mIvController = (ImageView) findViewById(R.id.pause_img);
        mIvScreen = (ImageView) findViewById(R.id.screen_img);
        mSeekBarPlay = (SeekBar) findViewById(R.id.play_seek);
        mSeekBarVolume = (SeekBar) findViewById(R.id.volume_seek);
        mTvCurrentTime = (TextView) findViewById(R.id.time_current_tv);
        mTvTotalTime = (TextView) findViewById(R.id.time_total_tv);
        mRelativeLayoutVideoView = (RelativeLayout) findViewById(R.id.videoView_layout);
        mIvVolume = (ImageView) findViewById(R.id.volume_img);
        mIvOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mIvOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        mRelativeLayoutController = (RelativeLayout) findViewById(R.id.relative_controller);
        mFrameLayoutLoadingProgress = (FrameLayout) findViewById(R.id.frame_layout_loading_progress);
        mTvLoading = (TextView) findViewById(R.id.text_view_loading);
        mProgressBarLoading = (ProgressBar) findViewById(R.id.progress_bar_loading);


        mFrameLayout = (FrameLayout) findViewById(R.id.layout_frame);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSeekBarVolume.setMax(streamMaxVolume);
        mSeekBarVolume.setProgress(streamVolume);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUiHandler.removeMessages(C.UPDATE_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUiHandler.removeMessages(C.UPDATE_UI);
    }


}
