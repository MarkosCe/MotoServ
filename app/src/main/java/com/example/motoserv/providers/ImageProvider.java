package com.example.motoserv.providers;

import android.content.Context;

import com.example.motoserv.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorageReference;

    public ImageProvider(){
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask save(Context context, File file){
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 400);
        StorageReference storage = mStorageReference.child(new Date() + ".jpg");
        UploadTask uploadTask = storage.putBytes(imageByte);
        return uploadTask;
    }

}
