package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UpdateProfileDriverActivity extends AppCompatActivity {

    private ImageView mImageViewProfile;
    private FloatingActionButton mFloatingButton;
    private TextView mTextViewName;
    private Button mButtonUpdate;

    private DriverProvider mDriverProvider;
    private AuthProvider mAuthProvider;

    private File mImageFile;
    private static final int IMAGE_PROFILE_CODE = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_driver);

        MyToolbar.show(this, "Actualizar perfil", true);

        mImageViewProfile = findViewById(R.id.img_update_profile_driver);
        mFloatingButton = findViewById(R.id.img_update_camera);
        mTextViewName = findViewById(R.id.input_update_name);
        mButtonUpdate = findViewById(R.id.btn_update_profile);

        mDriverProvider = new DriverProvider();
        mAuthProvider = new AuthProvider();

        initComponents();
    }

    private void initComponents(){

        getDriverData();

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void imageChooser(){
        ImagePicker.with(UpdateProfileDriverActivity.this)
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
                //mImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                Picasso.get().load(mImageFile).into(mImageViewProfile);
                Picasso.get().setIndicatorsEnabled(true);
            }catch (Exception e){
                Log.d("ERROR", e.getMessage());
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDriverData(){
        mDriverProvider.getDriver(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = (String) snapshot.child("name").getValue();
                    String image = (String) snapshot.child("image").getValue();

                    Picasso.get().load(image).into(mImageViewProfile);
                    Picasso.get().setIndicatorsEnabled(true);
                    mTextViewName.setHint(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile(){
        String name = (String) mTextViewName.getText();
        if (!name.equals("") && mImageViewProfile != null){
            saveImage(name);
        }
    }

    private void saveImage(String name){
        byte[] imageByte = CompressorBitmapImage.getImage(this, mImageFile.getPath(), 500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("user_images").child(mAuthProvider.getId());
        UploadTask uploadTask = storage.putBytes(imageByte);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UpdateProfileDriverActivity.this, "Successful: imagen subida", Toast.LENGTH_SHORT).show();
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image = uri.toString();
                            Driver driver = new Driver();
                            driver.setId(mAuthProvider.getId());
                            driver.setName(name);
                            driver.setImage(image);
                            update(driver);
                        }
                    });
                }else {
                    //mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateProfileDriverActivity.this, "Error: al subir la foto de perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void update(Driver driver){
        mDriverProvider.update(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UpdateProfileDriverActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UpdateProfileDriverActivity.this, "Error: no se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}