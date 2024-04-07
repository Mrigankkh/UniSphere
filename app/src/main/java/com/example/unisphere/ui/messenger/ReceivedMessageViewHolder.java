package com.example.unisphere.ui.messenger;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;

public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
    private TextView messageBody;

    public ReceivedMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageBody = itemView.findViewById(R.id.text_message_body_received);
    }

    public void bind(ChatMessage message) {
        messageBody.setText(message.getMessage());
    }
}

