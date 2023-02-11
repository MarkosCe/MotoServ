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

public class BottomSheetPlateVehicle extends BottomSheetDialogFragment {

    private DriverProvider driverProvider;
    private AuthProvider authProvider;

    EditText mEditText;
    Button mButtonSave;
    Button mButtonCancel;

    String plateVehicle;

    public static BottomSheetPlateVehicle newInstance(String plate){
        BottomSheetPlateVehicle bottomSheetPlateVehicle = new BottomSheetPlateVehicle();
        Bundle args = new Bundle();
        args.putString("plate", plate);
        bottomSheetPlateVehicle.setArguments(args);
        return bottomSheetPlateVehicle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        plateVehicle = getArguments().getString("plate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_plate, container, false);

        mEditText = view.findViewById(R.id.editTextPlate);
        mButtonSave = view.findViewById(R.id.button_save_plate);
        mButtonCancel = view.findViewById(R.id.button_cancel_plate);

        mEditText.setText(plateVehicle);

        driverProvider = new DriverProvider();
        authProvider = new AuthProvider();

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePlateVehicle();
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

    private void updatePlateVehicle(){
        String plate = String.valueOf(mEditText.getText());
        if (!plate.equals("")){
            driverProvider.updatePlateVehicle(authProvider.getId(), plate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dismiss();
                    Toast.makeText(getContext(), "La informacion del vehiculo se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
