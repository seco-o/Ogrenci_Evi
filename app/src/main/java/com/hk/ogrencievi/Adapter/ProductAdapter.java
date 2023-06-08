package com.hk.ogrencievi.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.PostHolder> {
    private final ArrayList<Product> products;
    private final PAListener listener;

    public ProductAdapter(ArrayList<Product> products, PAListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.urun_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.PostHolder holder, int position) {
        Product p = products.get(holder.getAdapterPosition());

        Picasso.get().load(Uri.parse(p.getImage())).into(holder.imageView);
        holder.titleText.setText(p.getTitle());
        holder.fiyatText.setText(p.getFiyat()+ " TL");

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCarcClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText,fiyatText;
        ConstraintLayout constraintLayout;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.urunrow_image);
            titleText = itemView.findViewById(R.id.urunrow_title);
            fiyatText = itemView.findViewById(R.id.urunrow_fiyat);
            constraintLayout = itemView.findViewById(R.id.urun_row_const);
        }
    }

    public interface PAListener<T>{
        void onCarcClicked(int position);
    }
}
