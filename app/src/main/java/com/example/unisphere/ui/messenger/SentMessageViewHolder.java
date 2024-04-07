package com.example.unisphere.ui.messenger;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;

public class SentMessageViewHolder extends RecyclerView.ViewHolder {
    private TextView messageBody;

    public SentMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageBody = itemView.findViewById(R.id.text_message_body_sent);
    }

    public void bind(ChatMessage message) {
        messageBody.setText(message.getMessage());
    }
}

