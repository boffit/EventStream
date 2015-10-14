package com.courtyard.afterafx.eventstream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;

    /*
    Called when the app is opened! Creates and sets variables
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the content view to be the activity_main.xml file
        setContentView(R.layout.activity_main);

        //Create the capture button by finding the element with "button_capture" id in the xml
        Button captureButton = (Button) findViewById(R.id.button_capture);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        final PictureCallback mPicture = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

                if (pictureFile == null){
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFile.getAbsolutePath(), pictureFile.getName(), pictureFile.getName());
                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                }
            }
        };

        // Add a listener to the Capture button
        captureButton.setOnClickListener(

                new View.OnClickListener() {

                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
    }

    /*
    A safe way to get an instance of the Camera object.
    */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /*
    Called when the app is paused by either pressing the BACK or HOME key
     */
    @Override
    protected void onPause() {
        super.onPause();

        //isFinishing() called when app is paused and closing!
        if (this.isFinishing()){
            //Release the camera when app is closed to background or exited
            releaseCamera();
        }

        //*Having this line here makes the camera preview not show again after pressing the HOME key*
        //However, without this line, the camera wont be released when the HOME key is pressed so
        //opening the camera app after shows the message "Cannot connect to camera!"
        releaseCamera();
    }

    protected void onStop(){
        super.onStop();
        releaseCamera();
    }

    protected void onDestroy(){
        super.onDestroy();
        releaseCamera();
    }

    /*
    Releases the camera
     */
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    /*
    Create a File for saving an image or video
    */
    private  File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
}
