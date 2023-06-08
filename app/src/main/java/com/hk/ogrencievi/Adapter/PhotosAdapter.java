package com.hk.ogrencievi.Adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hk.ogrencievi.Model.StringAndURI;
import com.hk.ogrencievi.R;

import java.util.ArrayList;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PostHolder> {
    private ArrayList<Uri> uris;
    private final PhotosAdapterListener listener;
    private ArrayList<StringAndURI> images;

    public PhotosAdapter(ArrayList<Uri> uris, PhotosAdapterListener listener) {
        Log.e("getItemCount","Burası çalıştı");
        this.uris = uris;
        this.listener = listener;
    }

    public PhotosAdapter(PhotosAdapterListener listener, ArrayList<StringAndURI> images) {
        this.listener = listener;
        this.images = images;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.photos_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        if(uris != null){
            Uri uri = uris.get(holder.getAdapterPosition());
            holder.imageView.setImageURI(uri);
        }else if(images != null){
            if(!images.get(holder.getAdapterPosition()).getImage().isEmpty()){
                Glide.with(holder.imageView).load(Uri.parse(images.get(holder.getAdapterPosition()).getImage())).into(holder.imageView);
            }else{
                holder.imageView.setImageURI(images.get(holder.getAdapterPosition()).getUri());
            }
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(holder.getAdapterPosition());
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClicked(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(uris != null){
            Log.e("getItemCount","1");
            return uris.size();
        }else{
            return images.size();
        }
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photos_row_image);

        }
    }
    public interface PhotosAdapterListener<T>{
        void onItemClicked(int position);
        void onItemLongClicked(int position);
    }
}
