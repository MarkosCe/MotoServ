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

public class BottomSheetUsername extends BottomSheetDialogFragment {

    private DriverProvider driverProvider;
    private AuthProvider authProvider;

    EditText mEditText;
    Button mButtonSave;
    Button mButtonCancel;

    String username;

    public static BottomSheetUsername newInstance(String username){
        BottomSheetUsername bottomSheetUsername = new BottomSheetUsername();
        Bundle args = new Bundle();
        args.putString("username", username);
        bottomSheetUsername.setArguments(args);
        return bottomSheetUsername;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        username = getArguments().getString("username");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_username, container, false);

        mEditText = view.findViewById(R.id.editTextName);
        mButtonSave = view.findViewById(R.id.button_save);
        mButtonCancel = view.findViewById(R.id.button_cancel);

        mEditText.setText(username);

        driverProvider = new DriverProvider();
        authProvider = new AuthProvider();

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUsername();
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

    private void updateUsername(){
        String name = String.valueOf(mEditText.getText());
        if (!name.equals("")){
            driverProvider.updateUsername(authProvider.getId(), name).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dismiss();
                    Toast.makeText(getContext(), "El nombre de usuario se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
