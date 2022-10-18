package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.models.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterDriverActivity extends AppCompatActivity {

    private Button mButtonSelectIne;
    private ImageView mImageViewIne;
    private Button mButtonSelectComp;
    private ImageView mImageViewComprob;

    private static final int IMAGE_INE_CODE = 101;
    private static final int IMAGE_COMPROB_CODE = 102;

    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this, "Completar registro", false);

        mAuth = FirebaseAuth.getInstance();

        mImageViewIne = findViewById(R.id.image_view_ine);
        mImageViewComprob = findViewById(R.id.image_view_cdomicilio);

        mButtonSelectIne = findViewById(R.id.btn_foto_ine);
        mButtonSelectIne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(RegisterDriverActivity.this)
                        .crop(16f,9f)	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(620, 312)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(IMAGE_INE_CODE);
            }
        });

        mButtonSelectComp = findViewById(R.id.btn_foto_comprob);
        mButtonSelectComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(RegisterDriverActivity.this)
                        .crop(3f,2f)	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(620, 620)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(IMAGE_COMPROB_CODE);
            }
        });

        final Button button = findViewById(R.id.btn_listo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = mAuth.getCurrentUser().getUid();
                saveUser(id);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            Uri uri = data.getData();

            switch (requestCode) {
                case IMAGE_INE_CODE:
                    mImageViewIne.setImageURI(uri);
                    break;
                case IMAGE_COMPROB_CODE:
                    mImageViewComprob.setImageURI(uri);
                    break;
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    void saveUser(String id){
        User user = new User();
        user.setName("Prueba");
        mDatabase.child("Users").child("Conductores").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterDriverActivity.this, "Correcto", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this, MapDriverActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterDriverActivity.this, "Algo falló", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}