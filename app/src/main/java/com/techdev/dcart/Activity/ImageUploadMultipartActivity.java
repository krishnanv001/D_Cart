package com.techdev.dcart.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techdev.dcart.APICalls.Api;
import com.techdev.dcart.APICalls.ImageResponse;
import com.techdev.dcart.APICalls.Respond;
import com.techdev.dcart.BuildConfig;
import com.techdev.dcart.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageUploadMultipartActivity extends AppCompatActivity {

    private String TAG = ImageUploadMultipartActivity.class.getSimpleName();

    public static final int PERMISSION_REQUEST_CODE = 200;
    public static final int REQUEST_IMAGE = 1;
    public static final int REQUEST_IMAGE_Gallary = 2;

    Uri fileUri;
    File mPhotoFile;
    Button add_field_button, buttonSubmit;
    LinearLayout linearphotoupload;
    int delecount = 0;    ImageView dummyimage;
    AppCompatImageView image;String stat_edit = "",imagePath,edit_base64,desc = "My Image",mediaPath;
    int requestcode;
    List<String> gtsImages = new ArrayList<>();
    private AlertDialog.Builder builder;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private boolean pressedLater;
    private boolean pressedDontAskAgain;
    private long laterPressedTime;TextView textViewHeading;
    private final String[] permissions = {"Camera", "Media & Storage"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_multipart);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        linearphotoupload           = findViewById(R.id.linearphotoupload_gts);
        buttonSubmit                = findViewById(R.id.gts_final_submit_);
        add_field_button            = findViewById(R.id.add_field_button_gts);

        dummyimage                  =   findViewById(R.id.dummyimage);

        builder = new AlertDialog.Builder(ImageUploadMultipartActivity.this);

        // calling sharedpreferences and getting shared preferences values
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        pressedLater = sharedPref.getBoolean(getResources().getString(R.string.later), false);
        pressedDontAskAgain = sharedPref.getBoolean(getResources().getString(R.string.dont_ask_again), false);
        laterPressedTime = sharedPref.getLong(getResources().getString(R.string.later_pressed_time), 0);

        // Check if all the permissions were been granted
        // If not granted show a dialog requesting permissions from the user.
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
        {

            alertDialogeForAskingPermissions();

            // check if pressedLater variable is been true
        }
        else if (pressedLater) {

            add_field_button.setEnabled(false);
            if (laterPressedTime != 0) {

                add_field_button.setEnabled(false);
                // check if its been 1 hour since later is been pressed.
                Date dateObj = new Date();
                long timeNow = dateObj.getTime();
                long oneHourLater = laterPressedTime + (3600 * 1000);
                if (oneHourLater <= timeNow) {

                    requestPermission();
                    editor.putBoolean(getResources().getString(R.string.later), false);
                    editor.commit();
                }
            }

        }
        // If pressed don't ask again the app should bot request permissions again.
        else if(!pressedDontAskAgain)
        {
            requestPermission();
        }
        else
        {
            Log.d(TAG, "onCreate: all allowed");
        }
        /////////////////////////////


        add_field_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dummyimage.setVisibility(View.GONE);

                onAddField(view);

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                  //  Log.d(TAG, "onClick: Path :"+imagePath.toString());

                    uploadImage();
                    
                   // TodayImageGallery(fileUri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        
    }

    ///////////////////////////////////////////////////
    //====================Image Upload to server and Store name in database
    private void uploadImage() {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ImageUploadMultipartActivity.this);
        progressDialog.setMessage("Image UpLoading...");
        progressDialog.show();

      //  ApiConfig service = APIClient.getRetroClient().create(ApiConfig.class);
        File file = new File(imagePath.trim().toString());
        System.out.println("Tab 1 final image path=>"+imagePath .trim().toString());

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        Api service = retrofit.create(Api.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody userID = RequestBody.create(okhttp3.MultipartBody.FORM, "1");
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
        Call<Respond> resultCall = service.uploadImage(body,userID);
        resultCall.enqueue(new Callback<Respond>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(Call<Respond> call, retrofit2.Response<Respond> response) {

                try {

                    Log.e(TAG,"image-result-->"+ new Gson().toJson(response.body()));
                    progressDialog.dismiss();
                    // Response Success or Fail
                    if (response.isSuccessful()) {

                        progressDialog.dismiss();
                        if (response.body()!=null && response.body().getError()) {

                            String imgName = response.body().getImageName();

                            Log.d(TAG, "onResponse: Image is:"+imgName);

    //                        RequestOptions requestOptions = new RequestOptions();
    //                        requestOptions.placeholder(R.drawable.green_black_avatar);
    //                        requestOptions.error(R.drawable.green_black_avatar);
    //
    //                        Glide.with(ImageUploadMultipartActivity.this)
    //                                .setDefaultRequestOptions(requestOptions)
    //                                .load(imgName)
    //                                .apply(RequestOptions.circleCropTransform())
    //                                .into(imageView);

                            Toast.makeText(ImageUploadMultipartActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(ImageUploadMultipartActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

                    }
                    else {
                        Log.d(TAG, "onResponse: Image- upload-fails:"+response.body().getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ImageUploadMultipartActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    }
                    //  imageView.setImageDrawable(null);
                    imagePath = null;

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }
            @Override
            public void onFailure(Call<Respond> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
            }
        });

    }

    /////////////////////////////////////////////////////////////////
    

    //U_See_Image Picker Method
    public void onAddField(View v) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field, null);
        // Add the new row before the add field button.
        image = (AppCompatImageView) rowView.findViewById(R.id.photouploadminus);
        final AppCompatImageView delete_button = (AppCompatImageView) rowView.findViewById(R.id.delete_button);
        delecount++;
        delete_button.setTag(delecount);
        Log.e("deltag", String.valueOf(delecount));

        if (stat_edit.equalsIgnoreCase("1")) {
            delete_button.setVisibility(View.GONE);
        } else {
            delete_button.setVisibility(View.VISIBLE);
        }

        selectImage();

        Log.e("count linear", String.valueOf(linearphotoupload.getChildCount() - 4));
        linearphotoupload.addView(rowView, linearphotoupload.getChildCount() - 1);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    gtsImages.remove(delecount - 1);
                    Log.e("indexlay", String.valueOf(delecount));

                }
                catch (Exception e) {
                    Log.e("photodel", e.toString());
                    Log.e("indexlay", String.valueOf(delecount));
                }

                onDelete(view);
            }
        });

    }

    public void onDelete(View v) {

        linearphotoupload.removeView((View) v.getParent());
        delecount--;
        Log.e(TAG, "afterdel-Image_count->:" + String.valueOf(delecount));

        if(delecount == 0)
        {
            dummyimage.setVisibility(View.VISIBLE);
        }
        else {
            dummyimage.setVisibility(View.GONE);
        }
        if (String.valueOf(delecount).equalsIgnoreCase("3")) {
            add_field_button.setVisibility(View.GONE);
        }
        else {
            add_field_button.setVisibility(View.VISIBLE);
        }

    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(ImageUploadMultipartActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    requestcode = 1;

                    //Without create folder and Get Thumbnail Image part of code
//                    Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(photoCaptureIntent, REQUEST_IMAGE);

                    //create folder and Get Original Image part of code
                    dispatchTakePictureIntent();


                }
                else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE_Gallary);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_IMAGE) {

                if (resultCode == RESULT_OK) {

/////////////////  Camera Original image create in folder and Image to convert Base 64 format  /////////////////////
                    Log.e("camera", "Camera - Process");
                    Log.e(TAG, "onActivityResult: Camera-Image-Path :" + mPhotoFile
                            +"\n File-Name:"+mPhotoFile.getName());

                    Glide.with(ImageUploadMultipartActivity.this)
                            .load(mPhotoFile)
                            .error(R.mipmap.ic_launcher_round)
                            .override(300, 300)
                            .centerCrop()
                            .into(image);

                  //  fileUri = Uri.parse(mPhotoFile.toString());

                      imagePath = mPhotoFile.toString();

                    //calling the upload file method after choosing the file
                  //  uploadFile(selectedImage, "My Image");

                    gtsImages.add(mPhotoFile.toString());

                    Log.d(TAG, "onActivityResult: array size-->" + gtsImages.size());

                    if (gtsImages.size() > 2) {
                        add_field_button.setVisibility(View.GONE);
                    } else {
                        add_field_button.setVisibility(View.VISIBLE);
                    }


                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(ImageUploadMultipartActivity.this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageUploadMultipartActivity.this, "Else", Toast.LENGTH_SHORT).show();
                }

            }

            else if (requestCode == REQUEST_IMAGE_Gallary) {

                if (resultCode == RESULT_OK) {

                    if (data != null) {

                        Uri imageUri = data.getData();

                       // imagePath = getRealPathFromURI(imageUri);
                        imagePath   = String.valueOf(imageUri);

                        Log.d(TAG, "onActivityResult: Gallery-Image-path :" + imagePath);

                   // }

                    Uri selectedImage = data.getData();

                    // imagePath = getRealPathFromURI(selectedImage);
                    Log.e(TAG, "Image PathGallery:" + "" + selectedImage);
                    //  image.setImageURI(selectedImage);
                    Glide.with(ImageUploadMultipartActivity.this)
                            .load(selectedImage)
                            .error(R.mipmap.ic_launcher_round)
                            .override(300, 300)
                            .centerCrop()
                            .into(image);


                    //View of Layouts
                    // convert_base_64(selectedImage);

                    Log.d(TAG, "onActivityResult: Gallery-array size-->" + gtsImages.size());
                    if (gtsImages.size() > 2) {
                        add_field_button.setVisibility(View.GONE);

                    } else {
                        add_field_button.setVisibility(View.VISIBLE);
                    }

                }

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(ImageUploadMultipartActivity.this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageUploadMultipartActivity.this, "Else", Toast.LENGTH_SHORT).show();
                }
            }
            /////////////////////////////////////
//            else {
//                Log.d(TAG, "onActivityResult: Works fine");
//            }

            else {
                Log.d(TAG, "onActivityResult: nothing happens");

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ExceptionPhoto", e.toString());
        }

    }

    private void convert_base_64(Uri uridata) throws FileNotFoundException {

        final Uri imageUri = uridata;
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        edit_base64 = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d(TAG, "convert_base_64: Result :" + edit_base64);

        gtsImages.add(edit_base64);

    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            mPhotoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);

            }
        }
    }

    /**
     * Create file with current timestamp name
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());//yyyyMMddHHmmss
        String mFileName = "GTS_Image_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    public void showAlertDialog(Activity activity, String msg)
    {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    //////////////////////////
    /*
     * This method is fetching the absolute path of the image file
     * if you want to upload other kind of files like .pdf, .docx
     * you need to make changes on this method only
     * Rest part will be the same
     * */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(ImageUploadMultipartActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {

        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void TodayImageGallery(Uri fileUri) {

        //creating a file
        File file = new File(fileUri.getPath());
        //File file = new File(getRealPathFromURI(fileUri));
        //creating request body for file
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), "My Image");
        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        Api api = retrofit.create(Api.class);
        //creating a call and calling the upload image method
        Call<ImageResponse> call = api.todayImage(requestFile, descBody);
        //finally performing the call
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                try {

                    if (response.isSuccessful()) {

                        if (response.body()!=null && !response.body().isError()) {
                            Toast.makeText(getApplicationContext(), "File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    //////////////////////////////


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
            {

                //few important permissions were not been granted
                // ask the user again.
                //  Toast.makeText(ImageUploadMultipartActivity.this, "Please Allow only Access this app", Toast.LENGTH_SHORT).show();

                alertDialoge();
            }
        }
    }

    private void alertDialoge() {
        //code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialoge_desc).setTitle(R.string.we_request_again);
        builder.setCancelable(false)
                .setPositiveButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                })
                .setNegativeButton(R.string.dont_ask_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'Don't Ask Again' Button
                        // the sharedpreferences value is true

                        Toast.makeText(ImageUploadMultipartActivity.this, "Please allow permissions only access", Toast.LENGTH_SHORT).show();
                        editor.putBoolean(getResources().getString(R.string.dont_ask_again), true);
                        editor.commit();
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void alertDialogeForAskingPermissions() {

        //code to Set the message and title from the strings.xml file
        builder.setMessage(getResources().getString(R.string.app_name) + " needs access to " + permissions[0] + ", " +
                permissions[1]).setTitle(R.string.permissions_required);
        //Setting message manually and performing action on button click
        //builder.setMessage("Do you want to close this application ?")
        builder.setCancelable(false).setPositiveButton(R.string.later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Toast.makeText(ImageUploadMultipartActivity.this, "Please Allow to Continue this app", Toast.LENGTH_SHORT).show();

                //Image upload button disable
                add_field_button.setEnabled(false);

                // Action for 'Later'
                //Saving later boolean value as true, also saving time of pressed later
                Date dateObj = new Date();
                long timeNow = dateObj.getTime();
                editor.putLong(getResources().getString(R.string.later_pressed_time), timeNow);
                editor.putBoolean(getResources().getString(R.string.later), true);
                editor.commit();
                dialog.cancel();
            }
        })
                .setNegativeButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
//                    ||
//                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                            != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                            != PackageManager.PERMISSION_GRANTED
            )
            {
                ActivityCompat.requestPermissions(ImageUploadMultipartActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                //Manifest.permission.READ_SMS,
                                //Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
        }
    }

}