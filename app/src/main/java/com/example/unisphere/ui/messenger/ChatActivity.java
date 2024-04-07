package com.example.unisphere.ui.messenger;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private String chatPartnerEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageView chatPartnerImage = findViewById(R.id.chat_partner_image);
        TextView chatPartnerName = findViewById(R.id.chat_partner_name);

        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);

        chatPartnerEmail = getIntent().getStringExtra("CHAT_PARTNER_EMAIL");
        String name = getIntent().getStringExtra("CHAT_PARTNER_NAME");
        String imageUrl = getIntent().getStringExtra("CHAT_PARTNER_IMAGE_URL");

        chatPartnerName.setText(name);
        if(imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_background).into(chatPartnerImage);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }


    private void sendMessage() {
        // Logic to send message to Firebase
    }
}

