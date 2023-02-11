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
import com.example.motoserv.fragments.BottomSheetBrandVehicle;
import com.example.motoserv.fragments.BottomSheetPlateVehicle;
import com.example.motoserv.fragments.BottomSheetUsername;
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
    private TextInputEditText mTextViewName;
    private TextInputEditText mTextViewBrand;
    private TextInputEditText mTextViewPlate;
    private Button mButtonUpdate;

    private TextView mEditUserName;
    private TextView mEditBrandVehicle;
    private TextView mEditPlateVehicle;

    private BottomSheetUsername mBottomSheetUsername;
    private BottomSheetBrandVehicle mBottomSheetBrandVehicle;
    private BottomSheetPlateVehicle mBottomSheetPlateVehicle;

    private DriverProvider mDriverProvider;
    private AuthProvider mAuthProvider;

    ValueEventListener mEventListener;

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
        mTextViewBrand = findViewById(R.id.input_update_brand);
        mTextViewPlate = findViewById(R.id.input_update_plate);
        //mButtonUpdate = findViewById(R.id.btn_update_profile);

        mEditUserName = findViewById(R.id.edit_username);
        mEditBrandVehicle = findViewById(R.id.edit_brand_vehicle);
        mEditPlateVehicle = findViewById(R.id.edit_plate_vehicle);
        mEditUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetName();
            }
        });
        
        mEditBrandVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetBrand();
            }
        });
        
        mEditPlateVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetPlate();
            }
        });

        mDriverProvider = new DriverProvider();
        mAuthProvider = new AuthProvider();

        initComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "ondestroy", Toast.LENGTH_SHORT).show();
        if (mEventListener != null){
            mDriverProvider.getDriver(mAuthProvider.getId()).removeEventListener(mEventListener);
        }
    }

    private void initComponents(){

        getDriverData();

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

    }

    private void openBottomSheetName(){
        String nameCurrent = String.valueOf(mTextViewName.getText());
        mBottomSheetUsername = BottomSheetUsername.newInstance(nameCurrent);
        mBottomSheetUsername.show(getSupportFragmentManager(), mBottomSheetUsername.getTag());
    }
    
    private void openBottomSheetBrand(){
        String brandCurrent = String.valueOf(mTextViewBrand.getText());
        mBottomSheetBrandVehicle = BottomSheetBrandVehicle.newInstance(brandCurrent);
        mBottomSheetBrandVehicle.show(getSupportFragmentManager(), mBottomSheetBrandVehicle.getTag());
    }

    private void openBottomSheetPlate(){
        String plateCurrent = String.valueOf(mTextViewPlate.getText());
        mBottomSheetPlateVehicle = BottomSheetPlateVehicle.newInstance(plateCurrent);
        mBottomSheetPlateVehicle.show(getSupportFragmentManager(), mBottomSheetPlateVehicle.getTag());
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
                saveImage();
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
        mEventListener = mDriverProvider.getDriver(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = (String) snapshot.child("name").getValue();
                    String image = (String) snapshot.child("image").getValue();
                    String brandVehicle = (String) snapshot.child("brand_vehicle").getValue();
                    String plateVehicle = (String) snapshot.child("plate_vehicle").getValue();

                    Picasso.get().load(image).into(mImageViewProfile);
                    Picasso.get().setIndicatorsEnabled(true);
                    mTextViewName.setText(name);
                    mTextViewBrand.setText(brandVehicle);
                    mTextViewPlate.setText(plateVehicle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveImage(){
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
                            updateImageProfile(image);
                        }
                    });
                }else {
                    //mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateProfileDriverActivity.this, "Error: al subir la foto de perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateImageProfile(String path){
        mDriverProvider.updateImageProfile(mAuthProvider.getId(), path).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UpdateProfileDriverActivity.this, "La imagen de perfil se ha actualizado", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UpdateProfileDriverActivity.this, "Error al actualizar la imagen de perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}