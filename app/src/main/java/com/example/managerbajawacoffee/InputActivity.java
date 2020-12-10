package com.example.managerbajawacoffee;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class InputActivity extends AppCompatActivity implements IPickResult {

    EditText etnama, etharga, etjenis;
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
        etharga = findViewById(R.id.etHarga);
        etjenis = findViewById(R.id.etJenis);
        btnTambah = findViewById(R.id.btnTambah);
        ivimage = findViewById(R.id.ivAddImage);
        tvLinkFoto = findViewById(R.id.tvLinkFoto);
        fileselectedImagePath = new File(selectedImagePathfoto);

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
//                                             String kode = etnama.getText().toString();
//                                             String nama = etnama.getText().toString();
//                                             String jenis = etharga.getText().toString();
//                                             String harga = etjenis.getText().toString();
//
//                                             progressBar.setTitle("Menambahkan...");
//                                             progressBar.show();

                AndroidNetworking.upload("http://192.168.6.159/apibajawa/insertimage.php")
                        .addMultipartFile("gambar", fileselectedImagePath)
                        .setPriority(Priority.MEDIUM)
                        .setOkHttpClient(((Initial) getApplication()).getOkHttpClient())
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(InputActivity.this, "masuk", Toast.LENGTH_SHORT).show();
                                String message = response.optString("message");
                                String status = response.optString("status");
                                String url = response.optString("url");
                                Log.d("TEST", "url: " + url);
                                Toast.makeText(InputActivity.this, url + message, Toast.LENGTH_LONG).show();
                                tvLinkFoto.setText("http://" + url);
                                Glide.with(InputActivity.this).load("http://" + url).into(ivimage);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("dea", "onError: " + anError.getErrorBody());
                                Log.d("dea", "onError: " + anError.getLocalizedMessage());
                                Log.d("dea", "onError: " + anError.getErrorDetail());
                                Log.d("dea", "onError: " + anError.getResponse());
                                Log.d("dea", "onError: " + anError.getErrorCode());
                            }
                        });

//                AndroidNetworking.upload("http://192.168.6.159/apibajawa/insertimage.php")
//                        .addMultipartFile("gambar", fileselectedImagePath)
//                        .setPriority(Priority.MEDIUM)
//                                                     .setOkHttpClient(((Initial) getApplication()).getOkHttpClient())
//                        .build()
//                        .getAsJSONObject(new JSONObjectRequestListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                String message = response.optString("message");
//                                String status = response.optString("status");
//                                String url = response.optString("url");
//                                Log.d("TEST", "url: " + url);
//                                Toast.makeText(InputActivity.this, url + message, Toast.LENGTH_LONG).show();
//                                tvLinkFoto.setText("http://" + url);
//                                Glide.with(InputActivity.this).load("http://" + url).into(ivimage);
//                            }
//
//                            @Override
//                            public void onError(ANError anError) {
//                                Log.d("dea", "onError: " + anError.getErrorBody());
//                                Log.d("dea", "onError: " + anError.getLocalizedMessage());
//                                Log.d("dea", "onError: " + anError.getErrorDetail());
//                                Log.d("dea", "onError: " + anError.getResponse());
//                                Log.d("dea", "onError: " + anError.getErrorCode());
//                            }
//                        });
//
            }
        });

    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null & foto.equalsIgnoreCase("true")) {

            try {
                File fileku = new Compressor(this)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(new File(r.getPath()));

                selectedImagePathfoto = fileku.getAbsolutePath();
                fileselectedImagePath = new File(selectedImagePathfoto);

                Log.d("RBA", "onPickResult: " + selectedImagePathfoto);
                Toast.makeText(this, "Masuk", Toast.LENGTH_SHORT).show();

                //selectedImageFile = new File(r.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }

        selectedImagePathfoto = r.getPath().toString();
        Log.d("test", "onPickResult: " + selectedImagePathfoto);

        selectedImage = r.getBitmap();
        ivimage.setImageBitmap(selectedImage);
        foto = "";

    }
}
