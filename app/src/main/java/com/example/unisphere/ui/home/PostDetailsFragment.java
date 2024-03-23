package com.example.unisphere.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.CommentAdapter;
import com.example.unisphere.model.Comment;
import com.example.unisphere.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostDetailsFragment extends Fragment {

    private static final String ARG_POST = "post";

    private Post post;

    private  TextView textViewCommentBox;

    private CommentAdapter commentAdapter;


    // TODO: Fetch current user ID from SharedPreferences

    String currentUserId = "test@northeastern.edu";
    String university="northeastern";


    public static PostDetailsFragment newInstance(Post post) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        ImageView imageViewPost = view.findViewById(R.id.imageView_post);
        TextView textViewDescription = view.findViewById(R.id.textView_post_description);
        TextView textViewLikeCount = view.findViewById(R.id.like_count);
        TextView textViewCommentCount = view.findViewById(R.id.comment_count);
        textViewCommentBox  = view.findViewById(R.id.editText_comment);
        Button buttonPostComment= view.findViewById(R.id.button_post_comment);
        buttonPostComment.setOnClickListener((viewButton) -> {
            postComment(post,textViewCommentBox.getText().toString());
        });



        if (post != null) {
            Picasso.get().load(post.getImageUrl()).into(imageViewPost);
            textViewDescription.setText(post.getDescription());
            textViewLikeCount.setText(String.valueOf(post.getLikedByUserIds().size()));
            textViewCommentCount.setText(String.valueOf(post.getComments().size()));

            List<Comment> comments = post.getComments();

            RecyclerView recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
            recyclerViewComments.setLayoutManager(new LinearLayoutManager(requireContext()));
            commentAdapter = new CommentAdapter(comments);
            recyclerViewComments.setAdapter(commentAdapter);

        }


        return view;
    }


    private void postComment(Post post,String commentText) {


        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child(university).child("posts").child(post.getKeyFirebase());

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> comments = post.getComments();
                Comment newComment = new Comment(currentUserId,commentText);
                comments.add(newComment);
                textViewCommentBox.setText("");
                commentAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Comment posted successfully", Toast.LENGTH_SHORT).show();

                postRef.child("comments").setValue(comments)
                        .addOnSuccessListener(aVoid -> {
                            post.setComments(comments);
                            commentAdapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Comment posted successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to post comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("PostDetailsFragment", "Failed to post comment", e);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }



}
