package com.example.motoserv.driver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.utils.FileUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;

public class RegisterDriverTwoActivity extends AppCompatActivity {

    private ImageView mImageViewIne;
    private ImageView mImageViewComprob;
    Button mButtonReady;

    private File mImageIne;
    private File mImageCd;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;

    String typeAccount;

    private static final int IMAGE_INE_CODE = 101;
    private static final int IMAGE_COMPROB_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver_two);

        MyToolbar.show(this, "Completar registro", false);
        mPreferences = getApplication().getSharedPreferences("preferences", MODE_PRIVATE);
        editor = mPreferences.edit();

        typeAccount = getIntent().getStringExtra("typeAcc");

        mImageViewIne = findViewById(R.id.image_view_ine);
        mImageViewComprob = findViewById(R.id.image_view_cdomicilio);

        Button mButtonSelectIne = findViewById(R.id.btn_foto_ine);
        Button mButtonSelectComp = findViewById(R.id.btn_foto_comprob);
        mButtonReady = findViewById(R.id.btn_listo_two);

        mButtonSelectIne.setOnClickListener(view -> ImagePicker.with(RegisterDriverTwoActivity.this)
                .crop(16f,9f)	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(620, 312)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(IMAGE_INE_CODE));

        mButtonSelectComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(RegisterDriverTwoActivity.this)
                        .crop(16f,9f)	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(620, 312)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(IMAGE_COMPROB_CODE);
            }
        });

        mButtonReady.setOnClickListener(view -> updateUserInfo());

    }

    void updateUserInfo(){
        if (mImageIne != null && mImageCd != null) {
            editor.putBoolean("finish", true);
            editor.putString("typeAcc", typeAccount);
            editor.apply();
            Intent intent = new Intent(RegisterDriverTwoActivity.this, HomeDriverActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Completa la informacion", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            assert data != null;
            Uri uri = data.getData();

            switch (requestCode) {
                case IMAGE_INE_CODE:
                    try {
                        mImageViewIne.setImageURI(uri);
                        mImageIne = FileUtil.from(this, uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case IMAGE_COMPROB_CODE:
                    try {
                        mImageViewComprob.setImageURI(uri);
                        mImageCd = FileUtil.from(this, uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }
}