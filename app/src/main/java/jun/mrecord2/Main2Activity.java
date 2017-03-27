package jun.mrecord2;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 这个activity开始拍摄视频。
 */

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "cj";
    private SurfaceView surfaceview;
    private Button button, button2, button3, button4;
    private SurfaceHolder holder;
    private Camera camera;
    private MediaRecorder recorder;
    boolean flagRecord = false;//是否正在录像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
    }

    private void initView() {
        surfaceview = (SurfaceView) findViewById(R.id.surfaceview);

        holder = surfaceview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                startCamera();
                break;
            case R.id.button2:
                releaseCamera();
                break;
            case R.id.button3:
                //startRecord(10);
                start();
                break;
            case R.id.button4:
                stopRecord();
                break;
        }
    }

    public void startCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(640, 480);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
                camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                camera.setParameters(parameters);
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "setpreview错误: " + e.toString());
            }

        } else {
            Log.e(TAG, "摄像头正在使用");
        }
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            Log.e(TAG, "releaseCamera:释放摄像头");
        } else {
            Log.e(TAG, "releaseCamera: 嘿！摄像头已经释放了");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: ");
    }

    public void startRecord(final int timeMiao) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String format = dateFormat.format(new Date());
                String path = Environment.getExternalStorageDirectory() + "/";
                String name = path + format + ".mp4";
                File file = new File(name);
                recorder = new MediaRecorder();

                recorder.setCamera(camera);
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                recorder.setOutputFile(file.getAbsolutePath());
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                recorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
                //                recorder.setVideoSize(640,480);
                //              recorder.setVideoFrameRate(4);
                //              recorder.setVideoEncodingBitRate(5*1024*1024);//清晰度
                recorder.setPreviewDisplay(holder.getSurface());
                recorder.setOrientationHint(90);
                try {
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "prepare错误" + e.toString());
                    Toast.makeText(Main2Activity.this, e.toString(), Toast.LENGTH_LONG);
                }
                recorder.start();// Recording is now started

                flagRecord = true;

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopRecord();
                    }
                }, 1000 * timeMiao);

            }
        }.start();
    }

    public void start() {
        startCamera();
        camera.unlock(); // maybe not for your activity flow

        //1st. Initial state
        //摄像机简况
//        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        recorder = new MediaRecorder();
        recorder.setCamera(camera);

        //2nd. Initialized state
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        //3rd. config
        recorder.setOutputFormat(mProfile.fileFormat);
        recorder.setAudioEncoder(mProfile.audioCodec);
        recorder.setVideoEncoder(mProfile.videoCodec);
        recorder.setOutputFile("/sdcard/" + System.currentTimeMillis() + ".3gp");
        recorder.setVideoSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
        recorder.setVideoFrameRate(mProfile.videoFrameRate);
        recorder.setVideoEncodingBitRate(mProfile.videoBitRate);
        recorder.setAudioEncodingBitRate(mProfile.audioBitRate);
        recorder.setAudioChannels(mProfile.audioChannels);
        recorder.setAudioSamplingRate(mProfile.audioSampleRate);
        recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
            recorder.start();
            flagRecord = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (flagRecord) {
            // 如果正在录制，停止并释放资源
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            flagRecord = false;
            releaseCamera();
        } else {
            Log.e(TAG, "已经停止录制,根本没有开始");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

}
