package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.client.RegisterClientActivity;
import com.example.motoserv.models.Client;
import com.example.motoserv.models.Driver;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.DriverProvider;
import com.example.motoserv.utils.CompressorBitmapImage;
import com.example.motoserv.utils.FileUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class RegisterDriverActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    RadioGroup mGender;
    ImageView mImageViewProfile;
    FloatingActionButton mFloatingButton;

    SharedPreferences mPreferences;
    String typeAcc;

    private AuthProvider mAuthProvider;
    DriverProvider mDriverProvider;

    private ProgressBar mProgressBar;

    private File mImageFile;
    private String mImage;
    private static final int IMAGE_PROFILE_CODE = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this, "Completar registro", false);

        mDriverProvider = new DriverProvider();
        mAuthProvider = new AuthProvider();

        mPreferences = getApplication().getSharedPreferences("preferences", MODE_PRIVATE);

        mTextInputUserName = findViewById(R.id.input_user_name);
        mGender = findViewById(R.id.group_button_gender);
        mImageViewProfile = findViewById(R.id.img_view_profile_driver);
        mProgressBar = findViewById(R.id.progress_bar_profile_driver);
        mFloatingButton = findViewById(R.id.button_select_profile);

        mProgressBar.setVisibility(View.GONE);

        typeAcc = getIntent().getStringExtra("typeAcc");

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        final Button button = findViewById(R.id.btn_listo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = mAuthProvider.getId();
                registerUserInfo(id);
            }
        });
    }

    private void imageChooser(){
        ImagePicker.with(RegisterDriverActivity.this)
                .crop(12f,12f)	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(500, 500)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(IMAGE_PROFILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PROFILE_CODE) {
            assert data != null;
            try {
                Uri uri = data.getData();
                mImageFile = FileUtil.from(this, uri);
                mImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", e.getMessage());
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUserInfo(String id){
        String userName = String.valueOf(mTextInputUserName.getText());
        if (!userName.equals("") && mGender.getCheckedRadioButtonId() != -1 && mImageFile != null){
            mProgressBar.setVisibility(View.VISIBLE);

            Driver driver = new Driver();
            driver.setId(id);
            driver.setProvider(mPreferences.getString("provider",""));
            driver.setName(userName);
            driver.setGender(getGender());
            saveImage(driver);
            //create(driver);
        }else {
            Toast.makeText(RegisterDriverActivity.this, "Completar informacion", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(Driver driver){
        byte[] imageByte = CompressorBitmapImage.getImage(this, mImageFile.getPath(), 500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("user_images").child(mAuthProvider.getId());
        UploadTask uploadTask = storage.putBytes(imageByte);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterDriverActivity.this, "Successful: imagen subida", Toast.LENGTH_SHORT).show();
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image = uri.toString();
                            driver.setImage(image);
                            create(driver);
                        }
                    });
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterDriverActivity.this, "Error: al subir la foto de perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void create(Driver driver){
        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterDriverActivity.this, "Registradooo", Toast.LENGTH_SHORT).show();
                    if (typeAcc != null){
                        Toast.makeText(RegisterDriverActivity.this, typeAcc, Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(RegisterDriverActivity.this, RegisterDriverTwoActivity.class);
                    intent.putExtra("typeAcc", typeAcc);
                    startActivity(intent);
                    finish();
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterDriverActivity.this, "Algo salío mal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getGender(){
        String gender = "";
        if (mGender.getCheckedRadioButtonId() == R.id.radio_hombre){
            gender = "Male";
        }else{
            gender = "Female";
        }
        return gender;
    }
}