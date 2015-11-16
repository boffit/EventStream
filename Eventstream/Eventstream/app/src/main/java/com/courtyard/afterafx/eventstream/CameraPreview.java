package com.courtyard.afterafx.eventstream;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    public static final String TAG = "CameraPreview";

    private SurfaceHolder mHolder;
    private Camera mCamera;

    /**--------------------------------------------------------*/

    public CameraPreview(Context context,Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();

        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    /**--------------------------------------------------------*/

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // nothing
    }

    /**--------------------------------------------------------*/

    public void surfaceCreated(SurfaceHolder holder) {

        try {
            if(mCamera != null){
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }
            //mCamera = Camera.open();
        } catch (Exception e){
            Log.e(TAG, "Error setting up preview", e);
            return;
        }

    }

    /**--------------------------------------------------------*/

    public void surfaceDestroyed(SurfaceHolder holder) {
        // nothing

    }

    /**--------------------------------------------------------*/

}