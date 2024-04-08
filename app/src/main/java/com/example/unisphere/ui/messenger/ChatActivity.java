package com.example.unisphere.ui.messenger;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unisphere.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private List<ChatMessage> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");

        ImageView chatPartnerImage = findViewById(R.id.chat_partner_image);
        TextView chatPartnerName = findViewById(R.id.chat_partner_name);
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);

        String chatPartnerEmail = getIntent().getStringExtra("CHAT_PARTNER_EMAIL");
        String name = getIntent().getStringExtra("CHAT_PARTNER_NAME");
        String imageUrl = getIntent().getStringExtra("CHAT_PARTNER_IMAGE_URL");

        chatPartnerName.setText(name);
        if(imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(chatPartnerImage);
        }

        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList, currentUserEmail);
        messagesRecyclerView.setAdapter(messageAdapter);

        loadMessages(chatPartnerEmail, currentUserEmail);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatPartnerEmail);
            }
        });
    }

    private void loadMessages(String chatPartnerEmail, String currentUserEmail) {
        String chatSessionKey = generateChatSessionKey(chatPartnerEmail, currentUserEmail);
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("chats").child(chatSessionKey);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage message = snapshot.getValue(ChatMessage.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private String generateChatSessionKey(String emailOne, String emailTwo) {
        List<String> emails = Arrays.asList(emailOne, emailTwo);
        Collections.sort(emails);
        return emails.get(0).replace(".", ",") + "_" + emails.get(1).replace(".", ",");
    }

    private void sendMessage(String chatPartnerEmail) {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("chats");

            String chatSessionKey = generateChatSessionKey(currentUserEmail, chatPartnerEmail);
            DatabaseReference chatSessionRef = messageRef.child(chatSessionKey);

            ChatMessage newMessage = new ChatMessage(messageText, currentUserEmail, System.currentTimeMillis());

            chatSessionRef.push().setValue(newMessage).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    messageEditText.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
