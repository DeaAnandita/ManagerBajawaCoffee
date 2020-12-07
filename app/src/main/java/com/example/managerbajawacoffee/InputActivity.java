package com.example.managerbajawacoffee;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class InputActivity extends AppCompatActivity implements IPickResult {

    EditText etnama,etharga,etjenis;
    Button btnTambah;
    ImageView ivimage;

    private ProgressDialog progressBar;

    private Bitmap selectedImage;
    private String selectedImagePathfoto = "";

    String foto="";
    File fileselectedImagePath;

//    private static final String TAG = MainActivity.class.getSimpleName();
//    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        etnama = findViewById(R.id.etNama);
        etharga = findViewById(R.id.etHarga);
        etjenis = findViewById(R.id.etJenis);
        btnTambah = findViewById(R.id.btnTambah);
        ivimage = findViewById(R.id.ivAddImage);

        progressBar = new ProgressDialog(this);

        ivimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foto = "true";
                PickImageDialog.build(new PickSetup()).show(getSupportFragmentManager());
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kode = etnama.getText().toString();
                String merk = etharga.getText().toString();
                String warna = etjenis.getText().toString();
                progressBar.setTitle("Menambahkan...");
                progressBar.show();
                AndroidNetworking.upload("")
                        .addMultipartFile("gambar", fileselectedImagePath)
                        .setPriority(Priority.HIGH)
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {

                            }
                        })
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("hasil", "onResponse: ");
                                try {
                                    JSONObject hasil = response.getJSONObject("hasil");
                                    String status = hasil.getString("STATUS");
                                    String message = hasil.getString("MESSAGE");
                                    Log.d("STATUS", "onResponse: " + status);
                                    if (status.equals("SUCCESS")) {
                                        Intent intent = new Intent(InputActivity.this, ViewDataActivity.class);
                                        startActivity(intent);
                                        finish();
                                        progressBar.dismiss();
                                    } else {
                                        Toast.makeText(InputActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                progressBar.dismiss();
                                Log.d("dea", "onError: " + anError.getErrorDetail());
                                Log.d("dea", "onError: " + anError.getErrorBody());
                                Log.d("dea", "onError: " + anError.getErrorCode());

                            }
                        });
                DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {

                            case DialogInterface.BUTTON_POSITIVE:
                                Log.d("test", "onClick: " + selectedImagePathfoto);
                                if (selectedImagePathfoto.equalsIgnoreCase("")
                                        || selectedImagePathfoto == ""
                                        || selectedImagePathfoto == null) {
                                    Toast.makeText(InputActivity.this, "Gambar Kosong!", Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent intent = new Intent(InputActivity.this, ViewDataActivity.class);
                                    intent.putExtra("selectedImagePathfoto", selectedImagePathfoto);
                                }
                            case  DialogInterface.BUTTON_NEGATIVE :
                                Toast.makeText(getApplicationContext(), "Data gagal di simpan", Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Apakah anda yakin ingin simpan data?").setPositiveButton("Ya", dialog)
                        .setNegativeButton("Tidak", dialog).show();
                progressBar.dismiss();
            }
        });

    }
    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null & foto.equalsIgnoreCase("true")) {

            selectedImagePathfoto=r.getPath().toString();
            Log.d("test", "onPickResult: "+selectedImagePathfoto);

            selectedImage = r.getBitmap();
            ivimage.setImageBitmap(selectedImage);
            foto="";

        }}


//        loadProfileDefault();
//
////        btnimage.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                onProfileImageClick();
////            }
////        });
//
//        // Clearing older images from cache directory
//        // don't call this line if you want to choose multiple images in the same activity
//        // call this once the bitmap(s) usage is over
//        ImagePickerActivity.clearCache(this);
//    }
//
//    private void loadProfile(String url) {
//        Log.d("check", "Image cache path: " + url);
//
//        Glide.with(this).load(url)
//                .into(ivimage);
//        ivimage.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
//    }
//
//    private void loadProfileDefault() {
//        Glide.with(this).load(R.mipmap.home_bg)
//                .into(ivimage);
//        ivimage.setColorFilter(ContextCompat.getColor(this, R.color.profile_default_tint));
//    }
//
//
//    void onProfileImageClick() {
//        Log.d("masuk", "masuk ");
//        Dexter.withActivity(this)
//                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        if (report.areAllPermissionsGranted()) {
//                            showImagePickerOptions();
//                        }
//
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
//    }
//
//    private void showImagePickerOptions() {
//        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
//            @Override
//            public void onTakeCameraSelected() {
//                launchCameraIntent();
//            }
//
//            @Override
//            public void onChooseGallerySelected() {
//                launchGalleryIntent();
//            }
//        });
//    }
//
//    private void launchCameraIntent() {
//        Intent intent = new Intent(InputActivity.this, ImagePickerActivity.class);
//        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
//
//        // setting aspect ratio
//        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//
//        // setting maximum bitmap width and height
//        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
//        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
//        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
//
//        startActivityForResult(intent, REQUEST_IMAGE);
//    }
//
//    private void launchGalleryIntent() {
//        Intent intent = new Intent(InputActivity.this, ImagePickerActivity.class);
//        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
//
//        // setting aspect ratio
//        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//        startActivityForResult(intent, REQUEST_IMAGE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE) {
//            if (resultCode == Activity.RESULT_OK) {
//                Uri uri = data.getParcelableExtra("path");
//                try {
//                    // You can update this bitmap to your server
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//
//                    // loading profile image from local cache
//                    loadProfile(uri.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * Showing Alert Dialog with Settings option
//     * Navigates user to app settings
//     * NOTE: Keep proper title and message depending on your app
//     */
//    private void showSettingsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(InputActivity.this);
//        builder.setTitle(getString(R.string.dialog_permission_title));
//        builder.setMessage(getString(R.string.dialog_permission_message));
//        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                InputActivity.this.openSettings();
//            }
//        });
//        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//
//    }
//
//    // navigating user to app settings
//    private void openSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        startActivityForResult(intent, 101);
//    }
//
}