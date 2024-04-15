package com.example.unisphere.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.unisphere.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Notification extends Service {

    private DatabaseReference postsRef, chatsRef;
    private String loggedInUserEmail, uniUser;

    private Query userPostsQuery, userR;
    private ValueEventListener likesEventListener, messagesEventListener;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        loggedInUserEmail = sharedPreferences.getString("email", "");
        uniUser = sharedPreferences.getString("university", "");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!loggedInUserEmail.isEmpty()) {
            listenForLikes();
            listenForNewMessages();
        }
        return START_STICKY;
    }


    private void listenForLikes() {
        postsRef = FirebaseDatabase.getInstance().getReference().child(uniUser + "/posts");
        userPostsQuery = postsRef.orderByChild("userId").equalTo(loggedInUserEmail);

        likesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String postId = snapshot.getKey();
                    long currentTime = System.currentTimeMillis();
                    String userId = snapshot.child("userId").getValue(String.class);
                    String toggle = snapshot.child("latestLikeAction").getValue(String.class);
                    Long messageTimestamp = snapshot.child("latestLikeTimestamp").getValue(Long.class);
                    if (loggedInUserEmail.equals(userId) && messageTimestamp != null && (currentTime - messageTimestamp) < 30000) {
                        if ("liked".equals(toggle)) {
                            String lastUserEmail = "";
                            for (DataSnapshot userSnapshot : snapshot.child("likedByUserIds").getChildren()) {
                                lastUserEmail = userSnapshot.getValue(String.class);
                            }
                            if (!lastUserEmail.isEmpty()) {
                                int atIndex = lastUserEmail.indexOf('@');
                                if (atIndex != -1) {
                                    lastUserEmail = lastUserEmail.substring(0, atIndex);
                                }
                                showLikeNotification(postId, lastUserEmail);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationService", "Error fetching posts", databaseError.toException());
            }
        };
        userPostsQuery.addValueEventListener(likesEventListener);
    }


    private void listenForNewMessages() {
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        messagesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NotificationService", "Data fetched: " + dataSnapshot);
                long currentTime = System.currentTimeMillis();
                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    Log.d("NotificationService", "Session ID: " + sessionSnapshot.getKey());
                    for (DataSnapshot messageSnapshot : sessionSnapshot.getChildren()) {
                        String recipient = messageSnapshot.child("recipientEmail").getValue(String.class);
                        String sender = messageSnapshot.child("senderEmail").getValue(String.class);
                        Long messageTimestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                        String messageText = messageSnapshot.child("message").getValue(String.class);

                        Log.d("NotificationService", "Checking message from: " + sender + " to " + recipient);
                        if (loggedInUserEmail.equals(recipient) && messageTimestamp != null && (currentTime - messageTimestamp) < 60000) {
                            if (messageText != null && sender != null) {
                                int atIndex = sender.indexOf('@');
                                if (atIndex != -1) {
                                    sender = sender.substring(0, atIndex);
                                }
                                showNewMessageNotification(sessionSnapshot.getKey(), messageText, sender);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationService", "Error fetching messages", databaseError.toException());
            }
        };
        chatsRef.addValueEventListener(messagesEventListener);

    }


    private void showLikeNotification(String postId, String lastUser) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("like_notifications", "Like Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "like_notifications")
                .setContentTitle("New like on your post")
                .setContentText("Your post has been liked by " + lastUser)
                .setSmallIcon(R.drawable.ic_like_filled_foreground);

        int notificationId = postId.hashCode();
        notificationManager.notify(notificationId, builder.build());
    }

    private void showNewMessageNotification(String chatSessionId, String messageText, String sender) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("message_notifications1", "Message Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "message_notifications1")
                .setContentTitle("New Message from " + sender)
                .setContentText("Message: " + messageText)
                .setSmallIcon(R.drawable.ic_message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText));

        int notificationId = chatSessionId.hashCode();
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userPostsQuery != null && likesEventListener != null) {
            userPostsQuery.removeEventListener(likesEventListener);
        }
        if (chatsRef != null && messagesEventListener != null) {
            chatsRef.removeEventListener(messagesEventListener);
        }
    }
}
