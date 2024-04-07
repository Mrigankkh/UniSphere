package com.example.unisphere.adapter.UserPost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostViewHolder> {

    Context context;
    private List<String> userPostUriList;
    private UserPostAdapter.ClickListener clickListener;
    private RecyclerView recyclerView;

    public UserPostAdapter(Context context, List<String> userPostUriList,RecyclerView recyclerView, ClickListener clickListener) {
        this.context = context;
        this.userPostUriList = userPostUriList;
        this.clickListener = clickListener;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       System.out.println("in here");
        return new UserPostViewHolder(LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false), context, userPostUriList, clickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        Picasso.get().load(userPostUriList.get(position)).into(holder.userPostPreview);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface ClickListener {
        void onPostClick(int position);
    }

}
