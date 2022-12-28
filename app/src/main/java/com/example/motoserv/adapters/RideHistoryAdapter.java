package com.example.motoserv.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motoserv.R;
import com.example.motoserv.models.RideHistory;
import com.example.motoserv.providers.ClientProvider;
import com.example.motoserv.providers.DriverProvider;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RideHistoryAdapter extends FirebaseRecyclerAdapter<RideHistory, RideHistoryAdapter.ViewHolder> {

    private ClientProvider clientProvider;

    public RideHistoryAdapter(FirebaseRecyclerOptions<RideHistory> options){
        super(options);
        clientProvider = new ClientProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RideHistory model) {
        holder.textViewOrigin.setText(model.getOrigin());
        holder.textViewDestination.setText(model.getDestination());
        clientProvider.getClient(model.getIdClient()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = (String) snapshot.child("name").getValue();
                    holder.textViewName.setText(name);
                    if (snapshot.hasChild("image")){
                        String image = (String) snapshot.child("image").getValue();
                        Picasso.get().load(image).into(holder.imageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewProfile;
        private TextView textViewName;
        private TextView textViewOrigin;
        private TextView textViewDestination;

        public ViewHolder(View view){
            super(view);
            imageViewProfile = view.findViewById(R.id.img_card_photo);
            textViewName = view.findViewById(R.id.txt_card_name);
            textViewOrigin = view.findViewById(R.id.txt_card_origin);
            textViewDestination = view.findViewById(R.id.txt_card_destination);
        }
    }
}
