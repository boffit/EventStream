package com.courtyard.afterafx.eventstream;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.GetDataCallback;
import com.parse.ParseFile;


//This fragment is the preview page for accepting or denying the new photo. If the user accepts it will be imported into the Parse Database.
//When they accept a new Photo object gets created on Parse with teh associated ParseFFile containing the image.

public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private Button saveButton;
    private Button cancelButton;
    private ParseImageView photoView;
    private ProgressDialog progressDialog;


    /**--------------------------------------------------------------------------*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**--------------------------------------------------------------------------*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        photoView = (ParseImageView) v.findViewById(R.id.photo_preview);

        saveButton = (Button) v.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressDialog(ProgressDialog.show(getActivity(), "", "Publishing Photo...", true, true));
                //PhotoParse photoParse = ((MainActivity) getActivity()).getCurrentPhoto();
                PhotoParse imageParse = new PhotoParse();
                imageParse.getImage();

                System.out.println(ParseUser.getCurrentUser());

                //When the user clicks "Save" upload the picture to Parse
                //Associate the picture with the current user
                if (ParseUser.getCurrentUser() != null){
                imageParse.setUser(ParseUser.getCurrentUser());
            }

                //Add the image
               // imageParse.setImage(((MainActivity) getActivity()).getImageFile());

                //Add the thumbnail
                //imageParse.setThumbnail(((MainActivity) getActivity()).getThumbnailFile());

                //Save the picture and return
                imageParse.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(com.parse.ParseException e) {
                        getProgressDialog().dismiss();
                        if (e == null) {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        });

        cancelButton=((Button)v.findViewById(R.id.cancel_button));
        cancelButton.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        }
    }

    );

        getFragmentManager().popBackStack();
        return v;
}

    /**--------------------------------------------------------------------------*/

    //On resume, check and see if a photo has been set form the CameraPreview.
    //If it has, load the (full) image in this fragment and make the preview image visable.

    @Override
        public void onResume(){
        super.onResume();
        ParseFile photoFile = ((MainActivity) getActivity()).getImageFile();
        if (photoFile != null){
            Log.i(TAG, "The photo WAS Taken");
            photoView.setParseFile(photoFile);
            photoView.loadInBackground(new GetDataCallback(){
                @Override
                public void done(byte[] data, com.parse.ParseException e){
                    photoView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            photoView.setVisibility(View.INVISIBLE);
        }
    }

    /**--------------------------------------------------------------------------*/

    public ProgressDialog getProgressDialog(){
        return progressDialog;
    }

    /**--------------------------------------------------------------------------*/

    public void setProgressDialog(ProgressDialog pd){
        progressDialog = pd;
    }

    /**--------------------------------------------------------------------------*/



    }
