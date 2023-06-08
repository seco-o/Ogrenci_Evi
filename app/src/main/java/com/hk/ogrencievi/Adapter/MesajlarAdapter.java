package com.hk.ogrencievi.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hk.ogrencievi.Model.MesajlarItem;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MesajlarAdapter extends RecyclerView.Adapter<MesajlarAdapter.PostHolder> {
    private final ArrayList<MesajlarItem> mesajlarItems;
    private final MAListener listener;

    public MesajlarAdapter(ArrayList<MesajlarItem> mesajlarItems,
                           MAListener listener) {
        this.mesajlarItems = mesajlarItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MesajlarAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mesajlar_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajlarAdapter.PostHolder holder, int position) {
        MesajlarItem item = mesajlarItems.get(holder.getAdapterPosition());

        Picasso.get().load(Uri.parse(item.getUserImage())).into(holder.imageView);
        StringBuilder adSoyadString = new StringBuilder();
        if(item.getUserName().length() > 15){
            for(int i=0; i<15; i++){
                adSoyadString.append(item.getUserName().charAt(i));
            }
            adSoyadString.append("...");
        }else{
            adSoyadString.append(item.getUserName());
        }
        holder.adSoyadText.setText(adSoyadString.toString());
        holder.messageText.setText(item.getLastMessage());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClicked(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mesajlarItems.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView adSoyadText;
        TextView messageText;
        LinearLayout cardView;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mrow_image);
            adSoyadText = itemView.findViewById(R.id.mrow_title);
            messageText = itemView.findViewById(R.id.mrow_message);
            cardView = itemView.findViewById(R.id.mrow_card);
        }
    }

    public interface MAListener<T>{
        void onCardClicked(int position);
    }
}
