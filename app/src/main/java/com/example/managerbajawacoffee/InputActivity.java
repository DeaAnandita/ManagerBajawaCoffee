package com.example.managerbajawacoffee;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class InputActivity extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

    EditText etnama, etharga, etjenis, etKode;
    Button btnTambah;
    ImageView ivimage;
    TextView tvLinkFoto;

    private ProgressDialog progressBar;
    private Bitmap selectedImage;
    private String selectedImagePathfoto = "";

    String foto = "";
    private File fileselectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        etnama = findViewById(R.id.etNama);
        etKode = findViewById(R.id.etKode);
        etharga = findViewById(R.id.etHarga);
        etjenis = findViewById(R.id.etJenis);
        btnTambah = findViewById(R.id.btnTambah);
        ivimage = findViewById(R.id.ivAddImage);
        fileselectedImagePath = new File(selectedImagePathfoto);

        progressBar = new ProgressDialog(this);

        ivimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                        .cameraButton(ButtonType.Button)
                        .galleryButton(ButtonType.Button)
                        .singleSelectTitle(R.string.pick_single)
                        .requestTag("single")
                        .show(getSupportFragmentManager(), null);
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kode = etKode.getText().toString();
                String nama = etnama.getText().toString();
                String jenis = etjenis.getText().toString();
                String harga = etharga.getText().toString();

                progressBar.setTitle("Menambahkan...");
                progressBar.show();

                AndroidNetworking.upload(BaseUrl.url + "image.php")
                        .addMultipartFile("avatar", fileselectedImagePath)
                        .setPriority(Priority.MEDIUM)
                        .setOkHttpClient(((Initial) getApplication()).getOkHttpClient())
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
//                                String message = response.optString("message");
//                                String status = response.optString("status");
                                String url = response.optString("url");
                                Log.d("TEST", "url: " + url);
                                //Toast.makeText(InputActivity.this, url + message, Toast.LENGTH_LONG).show();
                                //tvLinkFoto.setText("http://" + url);
//                                Glide.with(InputActivity.this).load("http://" + url).into(ivimage);
//                                progressBar.dismiss();

                                AndroidNetworking.post(BaseUrl.url + "insertproduk.php")
                                        .addBodyParameter("kodeMakanan", kode)
                                        .addBodyParameter("namaMakanan", nama)
                                        .addBodyParameter("jenisMakanan", jenis)
                                        .addBodyParameter("hargaMakanan", harga)
                                        .addBodyParameter("avatar", url)
                                        .setPriority(Priority.LOW)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("hasil", "onResponse: ");
                                                try {
                                                    JSONObject hasil = response.getJSONObject("hasil");
                                                    Boolean status = hasil.getBoolean("respon");
//                                                    String message = hasil.getString("MESSAGE");
                                                    Log.d("STATUS", "onResponse: " + status);
                                                    if (status) {
                                                        Toast.makeText(InputActivity.this, "Add product successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(InputActivity.this, MainMenuActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        progressBar.dismiss();
                                                    } else {
//                                                        Toast.makeText(InputActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
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
                            }

                            @Override
                            public void onError(ANError anError) {
                                progressBar.dismiss();
                                Log.d("dea", "onError: " + anError.getErrorBody());
                                Log.d("dea", "onError: " + anError.getLocalizedMessage());
                                Log.d("dea", "onError: " + anError.getErrorDetail());
                                Log.d("dea", "onError: " + anError.getResponse());
                                Log.d("dea", "onError: " + anError.getErrorCode());
                            }
                        });
            }
        });

    }

    @Override
    public void onImagesSelected(List<? extends Uri> list, String s) {
        for (Uri uri : list) {
            Glide.with(this).load(uri).into(ivimage);
            fileselectedImagePath = new File(getUriRealPath(this, uri));
        }
    }

    /*
    This method can parse out the real local file path from a file URI.
    */
    private String getUriRealPath(Context ctx, Uri uri) {
        String ret = "";
        if (isAboveKitKat()) {
            // Android sdk version number bigger than 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        } else {
            // Android sdk version number smaller than 19.
            ret = getImageRealPath(getContentResolver(), uri, null);
        }
        return ret;
    }

    /*
    This method will parse out the real local file path from the file content URI.
    The method is only applied to android sdk version number that is bigger than 19.
    */
    private String getUriRealPathAboveKitkat(Context ctx, Uri uri) {
        String ret = "";
        if (ctx != null && uri != null) {
            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(ctx, uri)) {
                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);
                // Get uri authority.
                String uriAuthority = uri.getAuthority();
                if (isMediaDoc(uriAuthority)) {
                    String[] idArr = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];
                        // Second item is document real id.
                        String realDocId = idArr[1];
                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;
                        ret = getImageRealPath(getContentResolver(), mediaContentUri, whereClause);
                    }
                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");
                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));
                    ret = getImageRealPath(getContentResolver(), downloadUriAppendId, null);
                } else if (isExternalStoreDoc(uriAuthority)) {
                    String[] idArr = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];
                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private boolean isAboveKitKat() {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private boolean isDocumentUri(Context ctx, Uri uri) {
        boolean ret = false;
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this document is provided by ExternalStorageProvider. Return true means the file is saved in external storage. */
    private boolean isExternalStoreDoc(String uriAuthority) {
        boolean ret = false;
        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            ret = true;
        }
        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. return true means this file is a downloaed file. */
    private boolean isDownloadDoc(String uriAuthority) {
        boolean ret = false;
        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            ret = true;
        }
        return ret;
    }

    /*
    Check if MediaProvider provide this document, if true means this image is created in android media app.
    */
    private boolean isMediaDoc(String uriAuthority) {
        boolean ret = false;
        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            ret = true;
        }
        return ret;
    }

    /*
    Check whether google photos provide this document, if true means this image is created in google photos app.
    */
    private boolean isGooglePhotoDoc(String uriAuthority) {
        boolean ret = false;
        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            ret = true;
        }
        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";
        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);
        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {
                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;
                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }
                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);
                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
