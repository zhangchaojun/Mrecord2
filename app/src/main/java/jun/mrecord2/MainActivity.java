package jun.mrecord2;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

/**
 * 这个activity就是初始化相机，mainActivity2 开始拍摄。一步一步走。
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "cj";
    private SurfaceView surfaceview;
    private Button button;
    private SurfaceHolder holder;
    private Camera camera;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        holder = surfaceview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        button.setOnClickListener(this);
        button2.setOnClickListener(this);
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

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

}
