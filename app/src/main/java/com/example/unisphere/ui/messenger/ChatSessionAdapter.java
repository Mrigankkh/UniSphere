package com.example.unisphere.ui.messenger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;

import java.util.List;

public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.ChatSessionViewHolder> {

    private List<String> chatPartners;

    public ChatSessionAdapter(List<String> chatPartners) {
        this.chatPartners = chatPartners;
    }

    @NonNull
    @Override
    public ChatSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_session, parent, false);
        return new ChatSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatSessionViewHolder holder, int position) {
        String chatPartner = chatPartners.get(position);
        holder.bind(chatPartner);
    }

    @Override
    public int getItemCount() {
        return chatPartners.size();
    }

    public void updateData(List<String> newChatPartners) {
        chatPartners.clear();
        chatPartners.addAll(newChatPartners);
        notifyDataSetChanged();
    }

    static class ChatSessionViewHolder extends RecyclerView.ViewHolder {

        private TextView chatPartnerTextView;

        public ChatSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            chatPartnerTextView = itemView.findViewById(R.id.chat_partner_email_text_view);
        }

        public void bind(String chatPartner) {
            chatPartnerTextView.setText(chatPartner);
        }
    }
}
