package com.hk.ogrencievi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hk.ogrencievi.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OkullarAdapter extends RecyclerView.Adapter<OkullarAdapter.PostHolder> {
    private final ArrayList<String> names;
    private final OkullarAdapterListener listener;

    public OkullarAdapter(ArrayList<String> names, OkullarAdapterListener listener) {
        this.names = names;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OkullarAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.okul_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OkullarAdapter.PostHolder holder, int position) {
        String okulName = names.get(holder.getAdapterPosition());
        holder.okulAdiText.setText(okulName);
        holder.okulAdiText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.theSchoolSelected(names.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        TextView okulAdiText;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            okulAdiText = itemView.findViewById(R.id.okul_row_name);
        }
    }

    public interface OkullarAdapterListener<T>{
        void theSchoolSelected(String schoolName);
    }
}
