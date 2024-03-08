package com.example.unisphere.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.PostAdapter;
import com.example.unisphere.model.Comment;
import com.example.unisphere.model.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment {

    List<Post> postList;

    RecyclerView recyclerView;
    PostAdapter postAdapter;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = homeView.findViewById(R.id.recyclerViewPostsHome);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        postAdapter = new PostAdapter(requireContext(), postList, homeView.findViewById(android.R.id.content));
        recyclerView.setAdapter(postAdapter);

        return homeView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postList = new ArrayList<>();
        // TODO FETCH FROM API LATER
        postList.add(Post.builder().description("Lorem Ipsum")
                .comments(Arrays.asList(Comment.builder().text("nice post").userId("tst1@g.com").build(), Comment.builder().text("great location").userId("tst2@g.com").build()))
                .likes(34L)
                .userId("test@northeastern.edu")
                .imageUrl("https://fastly.picsum.photos/id/1050/200/300.jpg?hmac=mMZp1DAD5EpHCZh-YBwfvrg5w327V3DoJQ8CmRAKF70").build());
        postList.add(Post.builder().description("Another post")
                .comments(Arrays.asList(Comment.builder().text("Awesome!").userId("user1@example.com").build(), Comment.builder().text("Love it!").userId("user2@example.com").build()))
                .likes(20L)
                .userId("test@northeastern.edu")
                .imageUrl("https://fastly.picsum.photos/id/237/200/300.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U").build());

        postList.add(Post.builder().description("Yet another post")
                .comments(Arrays.asList(Comment.builder().text("Great job!").userId("user3@example.com").build(), Comment.builder().text("Nice work!").userId("user4@example.com").build()))
                .likes(15L)
                .userId("test@northeastern.edu")
                .imageUrl("https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI").build());


    }
}