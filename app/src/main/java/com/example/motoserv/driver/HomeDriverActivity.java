package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motoserv.LoginActivity;
import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.DriverProvider;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeDriverActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    private ImageView mImageViewProfile;
    private TextView mTextViewName;
    private CardView mCardViewCredits;
    private CardView mCardViewUpdateProfile;
    private CardView mCardViewHistory;

    private DriverProvider mDriverProvider;
    private AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);

        MyToolbar.show(this, "Home", false);

        // Get the Intent that started this activity and extract the string
        mPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);

        mDriverProvider = new DriverProvider();
        mAuthProvider = new AuthProvider();

        mImageViewProfile = findViewById(R.id.img_profile);
        mTextViewName = findViewById(R.id.txt_name);
        mCardViewCredits = findViewById(R.id.card_credits);
        mCardViewUpdateProfile = findViewById(R.id.card_update_profile);
        mCardViewHistory = findViewById(R.id.card_history);
        initProfile();
        initCards();

        Button mButtonStart = findViewById(R.id.btn_started);
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goMap();
            }
        });
    }

    private void initProfile(){
        mDriverProvider.getDriver(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = (String) snapshot.child("name").getValue();
                    String image = (String) snapshot.child("image").getValue();

                    mTextViewName.setText(name);
                    Picasso.get().load(image).into(mImageViewProfile);
                    Picasso.get().setIndicatorsEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initCards(){
        mCardViewCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeDriverActivity.this, "Credits", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeDriverActivity.this, SubscriptionActivity.class);
                startActivity(intent);
            }
        });

        mCardViewUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeDriverActivity.this, "Update Profile", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeDriverActivity.this, UpdateProfileDriverActivity.class);
                startActivity(intent);
            }
        });

        mCardViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeDriverActivity.this, "Ride History", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeDriverActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goMap(){
        Intent intent = new Intent(HomeDriverActivity.this, MapDriverActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show();
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut(){
        //disconnect();
        String provider= mPreferences.getString("provider", null);
        if (provider != null) {
            Toast.makeText(HomeDriverActivity.this, "No es nulo", Toast.LENGTH_SHORT).show();
            if (provider.equals("FACEBOOK")) {
                LoginManager.getInstance().logOut();
            }
        }
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeDriverActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}