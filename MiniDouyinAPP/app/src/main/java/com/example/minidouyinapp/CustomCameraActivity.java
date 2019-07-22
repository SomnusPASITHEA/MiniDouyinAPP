package com.example.minidouyinapp;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minidouyinapp.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.minidouyinapp.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.example.minidouyinapp.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.example.minidouyinapp.utils.Utils.getOutputMediaFile;

public class CustomCameraActivity extends Activity {

    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;
    private String TAG = "CustomCamera";
    private SurfaceHolder surfaceHolder;
    private Camera.AutoFocusCallback myAutoFocusCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        mSurfaceView = findViewById(R.id.img);
        //todo 给SurfaceHolder添加Callback(摄像头数据实时显示)
        mCamera = getCamera(CAMERA_TYPE);
        surfaceHolder = mSurfaceView.getHolder();
        startPreview(surfaceHolder);
        findViewById(R.id.img_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null,null,mPicture);
                Toast.makeText(CustomCameraActivity.this,"拍照完成",Toast.LENGTH_SHORT).show();
            }});

        findViewById(R.id.img_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 录制，第一次点击是start，第二次点击是stop
                if (isRecording) {
                    //todo 停止录制
                    mMediaRecorder.stop();
                    releaseMediaRecorder();
                    findViewById(R.id.img_record).setBackground(findViewById(R.id.img_photo).getBackground());
                    Toast.makeText(CustomCameraActivity.this,"录制完成",Toast.LENGTH_SHORT).show();
                    isRecording = false;
                    Log.d(TAG, "onCreate: isRecording = false");

                } else {
                    //todo 录制
                    if(prepareVideoRecorder() == true) {
                        Log.d(TAG, "onCreate: isRecording = true1");
                        mMediaRecorder.start();
                        findViewById(R.id.img_record).setBackgroundColor(Color.WHITE);
                        isRecording = true;
                        Log.d(TAG, "onCreate: isRecording = true2");
                    }
                }
            }
        } );

        findViewById(R.id.img_facing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 切换前后摄像头
                if(Camera.getNumberOfCameras() > 1) {
                    releaseCameraAndPreview();
                    if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
                        mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    } else {
                        mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                    }
                    try {
                        mCamera.setPreviewDisplay(surfaceHolder);
//                    mCamera.setDisplayOrientation(getCameraDisplayOrientation(CAMERA_TYPE));
                        mCamera.startPreview();
                    } catch (Exception e){
//                    处理异常
                    }
                }
            }
        });
        /*findViewById(R.id.btn_facing).setOnClickListener(v -> {
            //todo 切换前后摄像头
            if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK) {
                CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            mCamera = getCamera(CAMERA_TYPE);
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                Point bestPreviewSizeValue1 = findBestPreviewSizeValue(parameters.getSupportedPreviewSizes());
                parameters.setPreviewSize((int)(bestPreviewSizeValue1.x*0.98), bestPreviewSizeValue1.y);
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                Log.d("133132", "set parameters fail");
            }
            startPreview(surfaceHolder);
        });*/
//        findViewById(R.id.btn_zoom).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //todo 调焦，需要判断手机是否支持
//            }
//        });
    }
    //    Timer timer = new Timer();
//    TimerTask outstandingTask = new TimerTask() {
//        @Override
//        public void run() {
//            try {
//                mCamera.autoFocus(myAutoFocusCallback);
//                Log.d("zxzxz", "run:zxzxzx ");
//            } catch (RuntimeException re) {
//            }
//        }
//    };
    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        mCamera = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
//        设置摄像头旋转
        mCamera.setDisplayOrientation(getCameraDisplayOrientation(CAMERA_TYPE));
//        设置摄像头分辨率
        Camera.Parameters parameters= mCamera.getParameters();
        List<Camera.Size> previewSize = parameters.getSupportedVideoSizes();
        int h = mSurfaceView.getHeight();
        int w = mSurfaceView.getWidth();
        size = getOptimalPreviewSize(previewSize,w,h);
        parameters.setPreviewSize(size.width,size.height);
        parameters.setRotation(getCameraDisplayOrientation(CAMERA_TYPE));
        mCamera.setParameters(parameters);

        return mCamera;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        //todo 开始预览
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
//                    mCamera.setDisplayOrientation(getCameraDisplayOrientation(CAMERA_TYPE));
                    mCamera.startPreview();
                } catch (Exception e){
//                    处理异常
                }

            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                releaseCameraAndPreview();
            }
        });
    }


    private MediaRecorder mMediaRecorder;

    private static Point findBestPreviewSizeValue(List<Camera.Size> sizeList){
        int bestX = 0;
        int bestY = 0;
        int size = 0;
        for (Camera.Size nowSize : sizeList){
            int newX = nowSize.width;
            int newY = nowSize.height;
            int newSize = Math.abs(newX * newX) + Math.abs(newY * newY);
            float ratio = (float) (newY * 1.0 / newX);
            if(newSize >= size && ratio != 0.75){//确保图片是16:9
                bestX  = newX;
                bestY = newY;
                size = newSize;
            }else if(newSize < size){
                continue;
            }
        }
        if(bestX > 0 && bestY > 0){
            return new Point(bestX,bestY);
        }
        return null;

    }
    public File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
//            2019.7.21  22:03新增修改照片存储后旋转问题
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bmp = BitmapFactory.decodeFile(mediaFile.getAbsolutePath(),bmOptions);
//            Utils.rotateImage(bmp,mediaFile.getAbsolutePath());

//            Log.d(TAG, "getOutputMediaFile: 图片旋转完毕");

//            bmp  = Utils.rotateImage(bmp,mediaFile.getAbsolutePath());
//                imageView.setImageBitmap(bmp);
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        Log.d("FilePath", "getOutputMediaFile: " + mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
        CustomCameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)));
        return mediaFile;
    }

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder
        if(mMediaRecorder != null){
            releaseMediaRecorder();
        }
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
 //       2019.7.21  21:50，解决录制视频后旋转问题
//        mMediaRecorder.setOrientationHint(90);
//        Log.d(TAG, "getOutputMediaFile: 视频旋转完毕");

        rotationDegree = getCameraDisplayOrientation(CAMERA_TYPE);
        mMediaRecorder.setOrientationHint(rotationDegree);
        if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        }else {//前置摄像头使用QUALITY_HIGH会报fail-19
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        }
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);
        try{
            mMediaRecorder.prepare();
            Log.d(TAG, "prepareVideoRecorder: mediarecorder.prepare成功");
            return true;
        } catch (Exception e) {
            Log.d(TAG, "prepareVideoRecorder: 准备mediarecorde异常");
            releaseMediaRecorder();
            return false;
        }
    }


    private void releaseMediaRecorder() {
        //todo
        if(mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mCamera.lock();
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();

        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}
