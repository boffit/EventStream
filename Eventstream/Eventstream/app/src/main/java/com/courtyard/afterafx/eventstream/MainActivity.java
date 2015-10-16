package com.courtyard.afterafx.eventstream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.parse.Parse;
import com.parse.ParseObject;


public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private final static String TAG = "KevinsMessage";

    //onCreate
    //Called when the app is opened! Creates and sets variables
    //---------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this, "jzne3U0W0NxkjF24gmuI0zZfZ0sRcXga9ag69ZfT", "84PuDG6RhBugWTautTFBdXVk3hb55a3PXd7O2eKr");

        //Create Parse Object
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

        Log.d(TAG, "OnCreate");

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

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Log.d(TAG, "Error creating media file, check storage permissions: ");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
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



    //onStart
    //--------------------------------------------------

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    //onPause
    //Called when the app is paused by either pressing the BACK or HOME key
    //--------------------------------------------------

//    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");

        //isFinishing() called when app is paused and closing!
        if (this.isFinishing()){
            //Release the camera when app is closed to background or exited
            releaseCamera();
            mCamera = null;
        }

    }

    //onStop
    //--------------------------------------------------

    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
       //releaseCamera();
    }

    //onDestroy
    //--------------------------------------------------

    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        releaseCamera();
    }

    //onResume
    //---------------------------------------------------

    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");

    }

    //onRestart
    //---------------------------------------------------

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    //Create camera instance, a safe way to get an instance of the Camera object.
    //---------------------------------------------------

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            Log.d(TAG, "Open Camera");
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    // Releases the camera
    //---------------------------------------------------

    private void releaseCamera(){
        if (mCamera != null){
            Log.d(TAG, "onRelease");
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }


    //Create a File for saving an image or video
    //---------------------------------------------------

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

    //---------------------------------------------------
}
