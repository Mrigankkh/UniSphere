package com.example.unisphere.ui.messenger;

import static android.app.PendingIntent.getActivity;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unisphere.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
        String chatPartnerEmail = chatPartners.get(position);
        holder.bind(chatPartnerEmail);
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
        private TextView userNameTextView;
        private ImageView userProfTextView;
        private TextView chatPartnerEmailTextView;

        public ChatSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            chatPartnerEmailTextView = itemView.findViewById(R.id.chat_partner_email_text_view);
            userProfTextView = itemView.findViewById(R.id.user_profile_image_view);
        }

        public void bind(String chatPartnerEmail) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Northeastern University/users");
            userRef.orderByChild("emailID").equalTo(chatPartnerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String name = userSnapshot.child("name").getValue(String.class);
                            String profileImagePath = userSnapshot.child("profilePicture").getValue(String.class);

                            userNameTextView.setText(name);
                            chatPartnerEmailTextView.setText(chatPartnerEmail);

                            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                                if(profileImagePath.startsWith("http")) {
                                    Picasso.get().load(profileImagePath).placeholder(R.drawable.ic_launcher_background).into(userProfTextView);
                                } else {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(profileImagePath);
                                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        Picasso.get().load(uri.toString()).placeholder(R.drawable.ic_launcher_background).into(userProfTextView);
                                    }).addOnFailureListener(e -> {
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    userNameTextView.setText("User not found");
                    chatPartnerEmailTextView.setText(chatPartnerEmail);
                }
            });
        }
    }
}