package com.example.unisphere.ui.messenger;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.unisphere.R;
import com.example.unisphere.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends Fragment {

    private User recipient;
    private DatabaseReference chatsReference;
    private String loggedInUserEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipient = (User) getArguments().getSerializable("recipient");
        }
        chatsReference = FirebaseDatabase.getInstance().getReference("chats");

        SharedPreferences prefs = getActivity().getSharedPreferences("USER_DATA", getContext().MODE_PRIVATE);
        loggedInUserEmail = prefs.getString("email", null);
        if (loggedInUserEmail == null) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView recipientNameTextView = view.findViewById(R.id.chat_recipient_name);
        EditText messageEditText = view.findViewById(R.id.chat_message_edit_text);
        Button sendButton = view.findViewById(R.id.chat_send_button);

        recipientNameTextView.setText(recipient != null ? recipient.getName() : "Unknown");

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(loggedInUserEmail, recipient.getEmailID(), message);
                messageEditText.setText("");
            }
        });
    }

    private void sendMessage(String senderEmail, String recipientEmail, String message) {
        String messageId = chatsReference.push().getKey();
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("senderEmail", senderEmail);
        chatMessage.put("recipientEmail", recipientEmail);
        chatMessage.put("message", message);
        chatMessage.put("timestamp", ServerValue.TIMESTAMP);

        if (senderEmail != null && recipientEmail != null && messageId != null) {
            String chatSessionKey = senderEmail.replace(".", ",") + "_" + recipientEmail.replace(".", ",");
            chatsReference.child(chatSessionKey).child(messageId).setValue(chatMessage);
        }
    }
}