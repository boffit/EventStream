package com.courtyard.afterafx.eventstream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    public PhotoParse photo;
    private ParseFile image;
    private ParseFile thumbnail;
    private Uri fileUri;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQ_WIDTH = 560;
    private static final int REQ_HEIGHT = 560;

    private final static String TAG = "MainActivity";

    //onCreate
    //Called when the app is opened! Creates and sets variables
    /**---------------------------------------------------*/
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "OnCreate");

        //Set the content view to be the activity_main.xml file
        setContentView(R.layout.activity_main);

        //Create the capture button by finding the element with "button_capture" id in the xml
        final Button captureButton = (Button) findViewById(R.id.button_capture);
        Button profileButton = (Button) findViewById(R.id.button_profile);
        Button eventsButton = (Button) findViewById(R.id.button_events);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.

        mPreview = new CameraPreview(this, mCamera);
        final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


//        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
//
////            @Override
////            public void onPictureTaken(byte[] data, Camera camera) {
////
////                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
////                if (pictureFile == null) {
////                    Log.d(TAG, "Error creating media file, check storage permissions: ");
////                    return;
////                }
////
////                try {
////                    FileOutputStream fos = new FileOutputStream(pictureFile);
////                    fos.write(data);
////                    fos.close();
////                } catch (FileNotFoundException e) {
////                    Log.d(TAG, "File not found: " + e.getMessage());
////                } catch (IOException e) {
////                    Log.d(TAG, "Error accessing file: " + e.getMessage());
////                }
////            }
//        };


        // Add a listener to the Capture button
        captureButton.setOnClickListener(

                new View.OnClickListener() {

                    public void onClick(View v) {
                        if (mCamera == null)
                            return;

//                        // get an image from the camera
//                        mCamera.takePicture(new Camera.ShutterCallback() {
//
//                            @Override
//                            public void onShutter() {
//                                //nothing to do
//                            }
//                        }, null, new Camera.PictureCallback() {
//
//                            @Override
//                            public void onPictureTaken(byte[] data, Camera camera) {
//                                //saveScaledPhoto(data);
//                            }
//                        });

                        /**--------------------------------------------------------*/
                        //After you take a picture hide the buttons.

                        View b = findViewById(R.id.button_capture);
                        b.setVisibility(View.GONE);

                        View n = findViewById(R.id.button_profile);
                        n.setVisibility(View.GONE);

                        View m = findViewById(R.id.button_events);
                        m.setVisibility(View.GONE);

                        //View k = findViewById(R.id.)]

                        /**--------------------------------------------------------*/


                        //After taking a picture, open the NewPhotoFragment
                        FragmentManager manager = getFragmentManager();
                        Fragment fragment = manager.findFragmentById(R.id.photo_preview);


                        if (fragment == null) {
                            fragment = new CameraFragment();
                            //add id of the FrameLayout to fill, and the fragment that will hold
                            manager.beginTransaction().add(R.id.camera_preview, fragment).commit();
                        }

                        /**--------------------------------------------------------*/

                    }

                }

        );

        profileButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                    }
                }
        );

        eventsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, EventsActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }

    protected void onDestroy(){
        super.onDestroy();
        mCamera.release();
    }

    //onPause
    //Called when the app is paused by either pressing the BACK or HOME key
    /**---------------------------------------------------*/

    @Override
    protected void onPause() {
        //super.onPause();

        Log.d(TAG, "onPause");

        //isFinishing() called when app is closing!
        //Releases the Camera fully
        if (this.isFinishing()){
            //Release the camera when app is closed to background or exited
            mCamera.release();
            mCamera = null;
            Log.d(TAG, "isFinishing()");
        }

        //Called when switching between activities!
        // Only stops the preview but does not release the Camera
        if(mCamera != null && !this.isFinishing()){
            mCamera.stopPreview();
            //mCamera.release();
            Log.d(TAG, "NOT isFinishing()");
        }

        super.onPause();

    }

    //onResume
    /**---------------------------------------------------*/

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        if(mCamera == null){
            try {
                mCamera = Camera.open();
            } catch (Exception e){
                Log.i(TAG, "No camera: " + e.getMessage());
                //Toast.makeText(getActivity(), "No camera detected", Toast.LENGTH_LONG).show();
            }
        }
    }



    //Getters
    /**---------------------------------------------------*/

    public Uri getPhotoFileUri(){
        return fileUri;
    }

    public ParseFile getImageFile(){
        return image;
    }

    public ParseFile getThumbnailFile(){
        return thumbnail;
    }


    public PhotoParse getCurrentPhoto(){
        return photo;
    }


    //Create camera instance, a safe way to get an instance of the Camera object.
    /**---------------------------------------------------*/

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            Log.d(TAG, "Open Camera");
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.i(TAG, "Device doesn't have a Camera");
        }
        return c; // returns null if camera is unavailable
    }




    //Create a File for saving an image or video. Uses the environment external storage directory.
    //Creates each file using unique timestamp

    //Returns the File Object for the new image, or null if there was some error creating the file.
    /**---------------------------------------------------*/

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        Log.i(TAG, "Entering getOutputMediaFile");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "My Eventstream");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        // Make sure you have permission to write to the SD Card enabled.
        // in order to do this!!
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.i(TAG, "getOutputMediaFile failed to create directory");
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

    /**---------------------------------------------------*/

//    //Create a file URI for saving an image
//    //A URI strings is Googles way of requesting data in a content provider and
//    //for requesting actions.
//    private static Uri getOutputMediaFileUri(int type){
//        File output = getOutputMediaFile(type);
//        if(output != null){
//            return Uri.fromFile(output);
//        } else {
//            return null;
//        }
//
//    }
//
//    /**---------------------------------------------------*/
//    /**
//     * Takes the photo captured by the user, and saves the image and it's
//     * scaled-down thumbnail as ParseFiles. This occurs after the user captures
//     * a photo, but before the user chooses to publish to Anypic. Thus, these
//     * ParseFiles can later be associated with the Photo object itself.
//     *
//     * @param data The byte array containing the image data
//     * TODO (future) - do this in background AsyncTask
//     */
//
//    private void savePhotoImages(String pathToFile){
//
//        //Convert to bitmap to assist with resizing
//        Bitmap eventstreamImage = decodeSampledBitmapFromFile(pathToFile, REQ_WIDTH, REQ_HEIGHT);
//        //Bitmap eventstreamImage = BitmapFactory.decodeByteArray(pathToFile, 0, pathToFile.length());
//
//        //Override Android default landscape orientation and save portrait
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap rotatedImage = Bitmap.createBitmap(eventstreamImage, 0, 0, eventstreamImage.getWidth(), eventstreamImage.getHeight(), matrix, true);
//
//        //Make thumbnail with size of 86 pixels
//        Bitmap eventstreamThumbnail = Bitmap.createScaledBitmap(rotatedImage, 86, 86, false);
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        byte[] rotatedData = bos.toByteArray();
//
//        bos.reset(); //reset the stream to prepare for the thumbnail
//        eventstreamThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        byte[] thumbnailData = bos.toByteArray();
//
//        try{
//            //close the byte array output stream
//            bos.close();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//
//        //Create the ParseFiles and save them in the background
//
//        //Image
//        image = new ParseFile("photo.jpg", rotatedData);
//        thumbnail = new ParseFile("photo_thumbnail.jpg", thumbnailData);
//        image.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null){
//                    Toast.makeText(getApplicationContext(), "Error saving image file: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                } else {
//                    //saved image to Parse!
//                }
//            }
//        });
//
//        //Thumbnail
//        thumbnail.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null){
//                    Toast.makeText(getApplicationContext(), "Error saving thumbnail file: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                } else {
//                    //saved image to Parse!
//                }
//            }
//        });
//        Log.i(TAG, "Finished saving the photos to ParseFiles!");
//    }
//
//    /**---------------------------------------------------*/
//
//
//    /**
//     * This function takes the path to our file from which we want to create
//     * a Bitmap, and decodes it using a smaller sample size in an effort to
//     * avoid an OutOfMemoryError.
//     *
//     * See: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
//     *
//     * @param path The path to the file resource
//     * @param reqWidth The required width that the image should be prepared for
//     * @param reqHeight The required height that the image should be prepared for
//     * @return A Bitmap resource that has been scaled down to an appropriate size, so that it can conserve memory
//     *
//     */
//    public static Bitmap decodeSampledBitmapFromFile(String path,
//                                                     int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(path, options);
//    }
//
//    /**---------------------------------------------------*/
//
//    /**
//     * This is a helper function used to calculate the appropriate sampleSize
//     * for use in the decodeSampledBitmapFromFile() function.
//     *
//     * See: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
//     *
//     * @param options The BitmapOptions for the Bitmap resource that we want to scale down
//     * @param reqWidth The target width of the UI element in which we want to fit the Bitmap
//     * @param reqHeight The target height of the UI element in which we want to fit the Bitmap
//     * @return A sample size value that is a power of two based on a target width and height
//     */
//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }
//
//    /**---------------------------------------------------*/


}