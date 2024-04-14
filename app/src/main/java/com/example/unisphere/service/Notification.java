package com.example.unisphere.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.unisphere.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Notification extends Service {

    private DatabaseReference postsRef, chatsRef;
    private String loggedInUserEmail, uniUser;

    private Query userPostsQuery, userR;
    private ValueEventListener likesEventListener,messagesEventListener;

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

    private HashMap<String, Long> previousLikesCounts = new HashMap<>();

    private void listenForLikes() {
        postsRef = FirebaseDatabase.getInstance().getReference().child(uniUser + "/posts");
        userPostsQuery = postsRef.orderByChild("userId").equalTo(loggedInUserEmail);

        likesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String postId = snapshot.getKey();
                    String userId = snapshot.child("userId").getValue(String.class);
                    if (loggedInUserEmail.equals(userId)) {
                        long currentLikesCount = snapshot.child("likedByUserIds").getChildrenCount();
                        Long lastLikesCount = previousLikesCounts.get(postId);

                        if (lastLikesCount == null || currentLikesCount > lastLikesCount) {
                            showLikeNotification(postId, currentLikesCount);
                            previousLikesCounts.put(postId, currentLikesCount);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        userPostsQuery.addValueEventListener(likesEventListener);
    }


    private void listenForNewMessages() {
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        userR = chatsRef.orderByChild("recipientEmail").equalTo(loggedInUserEmail);
        messagesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long currentTime = System.currentTimeMillis();
                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot messageSnapshot : sessionSnapshot.getChildren()) {
                        Long messageTimestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                        if (messageTimestamp != null && (currentTime - messageTimestamp) < 60000) {
                            String messageText = messageSnapshot.child("message").getValue(String.class);
                            String sender = messageSnapshot.child("senderEmail").getValue(String.class);
                            if (messageText != null) {
                                showNewMessageNotification(sessionSnapshot.getKey(), messageText, sender);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        userR.addValueEventListener(messagesEventListener);
    }



    private void showLikeNotification(String postId, long likesCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("like_notifications", "Like Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "like_notifications")
                .setContentTitle("New like on your post")
                .setContentText("Your post has been liked " + likesCount + " times!")
                .setSmallIcon(R.drawable.ic_like_filled_foreground);

        int notificationId = postId.hashCode();
        notificationManager.notify(notificationId, builder.build());
    }

    private void showNewMessageNotification(String chatSessionId, String messageText, String sender) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("message_notifications", "Message Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "message_notifications")
                .setContentTitle("New Message from"+sender)
                .setContentText(messageText)
                .setSmallIcon(R.drawable.ic_home)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText));

        int notificationId = chatSessionId.hashCode();
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userPostsQuery != null) {
            userPostsQuery.removeEventListener(likesEventListener);
        }
        if (chatsRef != null) {
            chatsRef.removeEventListener(messagesEventListener);
        }
    }
}
