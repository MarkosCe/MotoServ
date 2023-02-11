package com.example.motoserv.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.motoserv.R;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.DriverProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetBrandVehicle extends BottomSheetDialogFragment {

    private DriverProvider driverProvider;
    private AuthProvider authProvider;

    EditText mEditText;
    Button mButtonSave;
    Button mButtonCancel;

    String brandVehicle;

    public static BottomSheetBrandVehicle newInstance(String brand){
        BottomSheetBrandVehicle bottomSheetBrandVehicle = new BottomSheetBrandVehicle();
        Bundle args = new Bundle();
        args.putString("brand", brand);
        bottomSheetBrandVehicle.setArguments(args);
        return bottomSheetBrandVehicle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        brandVehicle = getArguments().getString("brand");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_brand, container, false);

        mEditText = view.findViewById(R.id.editTextBrand);
        mButtonSave = view.findViewById(R.id.button_save_brand);
        mButtonCancel = view.findViewById(R.id.button_cancel_brand);

        mEditText.setText(brandVehicle);

        driverProvider = new DriverProvider();
        authProvider = new AuthProvider();

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBrandVehicle();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private void updateBrandVehicle(){
        String brand = String.valueOf(mEditText.getText());
        if (!brand.equals("")){
            driverProvider.updateBrandVehicle(authProvider.getId(), brand).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dismiss();
                    Toast.makeText(getContext(), "La informacion del vehiculo se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
