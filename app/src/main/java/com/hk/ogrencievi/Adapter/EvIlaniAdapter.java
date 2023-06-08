package com.hk.ogrencievi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hk.ogrencievi.Model.EvIlani;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EvIlaniAdapter extends RecyclerView.Adapter<EvIlaniAdapter.PostHolder> {
    private final ArrayList<EvIlani> evIlanis;
    private final EIAListener listener;

    public EvIlaniAdapter(ArrayList<EvIlani> evIlanis, EIAListener listener) {
        this.evIlanis = evIlanis;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EvIlaniAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.evilani_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvIlaniAdapter.PostHolder holder, int position) {
        EvIlani evIlani = evIlanis.get(holder.getAdapterPosition());

        holder.titleText.setText(evIlani.getAd()+" "+evIlani.getSoyad());
        holder.okulText.setText(evIlani.getOkul());
        Picasso.get().load(evIlani.getPp()).into(holder.imageView);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return evIlanis.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        TextView titleText,okulText;
        ImageView imageView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.evilani_row_const);
            titleText = itemView.findViewById(R.id.evilanirow_title);
            okulText = itemView.findViewById(R.id.evilanirow_okul);
            imageView = itemView.findViewById(R.id.evilanirow_image);
        }
    }

    public interface EIAListener<T>{
        void onCardClicked(int position);
    }
}
