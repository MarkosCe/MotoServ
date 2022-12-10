package com.example.motoserv.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.driver.RegisterDriverActivity;
import com.example.motoserv.models.Client;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.ClientProvider;
import com.example.motoserv.utils.CompressorBitmapImage;
import com.example.motoserv.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class RegisterClientActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    RadioGroup mGender;
    ImageView mImageViewProfile;

    SharedPreferences mPreferences;

    private AuthProvider mAuthProvider;
    ClientProvider mClientProvider;

    //private FirebaseAuth mAuth;

    private ProgressBar mProgressBar;

    private File mImageFile;
    private String mImage;
    private final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        MyToolbar.show(this, "Completar registro", false);

        mClientProvider = new ClientProvider();
        mAuthProvider = new AuthProvider();
        //mAuth = FirebaseAuth.getInstance();
        mPreferences = getApplication().getSharedPreferences("preferences", MODE_PRIVATE);

        mTextInputUserName = findViewById(R.id.input_name_client);
        mGender = findViewById(R.id.groupbtn_gender_client);
        mImageViewProfile = findViewById(R.id.img_view_profile);
        mProgressBar = findViewById(R.id.progress_bar_profile);

        mProgressBar.setVisibility(View.GONE);

        mImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        final Button button = findViewById(R.id.btn_empezar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = mAuthProvider.getId();
                registerUserInfo(id);
            }
        });
    }

    private void imageChooser(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", e.getMessage());
            }
        }
    }

    void registerUserInfo(String id) {
        String userName = String.valueOf(mTextInputUserName.getText());
        if (!userName.equals("") && mGender.getCheckedRadioButtonId() != -1 && mImageFile != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            Client client = new Client();
            client.setId(id);
            client.setProvider(mPreferences.getString("provider", ""));
            client.setName(userName);
            client.setGender(getGender());
            saveImage(client);
        }else {
            Toast.makeText(RegisterClientActivity.this, "Completar informacion", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(Client client){
        byte[] imageByte = CompressorBitmapImage.getImage(this, mImageFile.getPath(), 500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("user_images").child(mAuthProvider.getId());
        UploadTask uploadTask = storage.putBytes(imageByte);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterClientActivity.this, "Successful: imagen subida", Toast.LENGTH_SHORT).show();
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image = uri.toString();
                            client.setImage(image);
                            create(client);
                        }
                    });
                }else {
                    Toast.makeText(RegisterClientActivity.this, "Error: al subir la foto de perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void create(Client client){
        mClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterClientActivity.this, "Successful: usuario registrado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterClientActivity.this, MapClientActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(RegisterClientActivity.this, "Error: Algo salío mal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getGender(){
        String gender = "";
        if (mGender.getCheckedRadioButtonId() == R.id.radio_hombre_client){
            gender = "Male";
        }else{
            gender = "Female";
        }
        return gender;
    }

}