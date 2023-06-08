package com.hk.ogrencievi.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.PostHolder> {
    private final ArrayList<Product> products;
    private final FAListener listener;

    public FavoritesAdapter(ArrayList<Product> products, FAListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoritesAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.favorite_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.PostHolder holder, int position) {
        Product p = products.get(holder.getAdapterPosition());

        Picasso.get().load(Uri.parse(p.getImage())).into(holder.image);

        holder.titleText.setText(p.getTitle());
        holder.priceText.setText(p.getFiyat()+" TL");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClicked(holder.getAdapterPosition());
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onCardLongClicked(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView titleText,priceText;
        CardView cardView;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.frow_image);
            titleText = itemView.findViewById(R.id.frow_title);
            priceText = itemView.findViewById(R.id.frow_price);
            cardView = itemView.findViewById(R.id.frow_card);
        }
    }
    public interface FAListener<T>{
        void onCardClicked(int position);
        void onCardLongClicked(int position);
    }
}
