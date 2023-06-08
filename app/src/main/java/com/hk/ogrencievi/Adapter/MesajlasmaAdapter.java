package com.hk.ogrencievi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hk.ogrencievi.Model.Message;
import com.hk.ogrencievi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MesajlasmaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Message> messages;

    public MesajlasmaAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1:
                View view = inflater.inflate(R.layout.anlik_giden_message_row, parent, false);
                return new PostHolder1(view);
            case 2:
                View view2 = inflater.inflate(R.layout.anlik_gelen_message_row, parent, false);
                return new PostHolder2(view2);

        }
        return null;


    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position) != null) {
            if (messages.get(position).getSenderEmail()
                    != null) {
                if(messages.get(position).getSenderEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    return 1;
            }else{
                    return 2;
                }


        } else {
            return 2;
        }
    }
        return 1;

}


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(holder.getAdapterPosition());
        switch (holder.getItemViewType()) {
            case 1:
                if (m != null) {
                    PostHolder1 postHolder1 = (PostHolder1) holder;
                    postHolder1.messageText.setText(m.getText());
                }
                break;

            case 2:
                if (m != null) {
                    PostHolder2 postHolder2 = (PostHolder2) holder;
                    postHolder2.messageText.setText(m.getText());
                }
                break;

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

static class PostHolder1 extends RecyclerView.ViewHolder {
    TextView messageText;

    public PostHolder1(@NonNull View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.anlikgidenmr_text);
    }

}

static class PostHolder2 extends RecyclerView.ViewHolder {
    TextView messageText;

    public PostHolder2(@NonNull View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.anlikgelenmr_text);
    }

}
}
