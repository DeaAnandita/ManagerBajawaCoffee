package com.example.managerbajawacoffee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainMenuActivity extends AppCompatActivity {

    CardView cdInput,cdTrans,cdDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        cdInput = findViewById(R.id.cdInput);
        cdTrans = findViewById(R.id.cdTransaksi);
        cdDownload = findViewById(R.id.cdDownload);

        cdInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this,InputActivity.class));
            }
        });

        cdTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this,ViewDataActivity.class));
            }
        });
        cdDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}