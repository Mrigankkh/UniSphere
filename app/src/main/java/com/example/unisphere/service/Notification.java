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

public class Notification extends Service {

    private DatabaseReference postsRef, chatsRef;
    private String loggedInUserEmail, uniUser;

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
        Query userPostsQuery = postsRef.orderByChild("userId").equalTo(loggedInUserEmail);

        userPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.child("userId").getValue(String.class);
                    if (loggedInUserEmail.equals(userId)) {
                        long likesCount = snapshot.child("likedByUserIds").getChildrenCount();
                        if (likesCount > 0) {
                            showLikeNotification(snapshot.getKey(), likesCount);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void listenForNewMessages() {
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot messageSnapshot : sessionSnapshot.getChildren()) {
                        String recipientEmail = messageSnapshot.child("recipientEmail").getValue(String.class);
                        if (loggedInUserEmail.equals(recipientEmail)) {
                            String messageText = messageSnapshot.child("message").getValue(String.class);
                            if (messageText != null) {
                                showNewMessageNotification(sessionSnapshot.getKey(), messageText);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setupMessageNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("message_notifications", "Message Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
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

    private void showNewMessageNotification(String chatSessionId, String messageText) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("message_notifications", "Message Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "message_notifications")
                .setContentTitle("New Message")
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
    }
}
