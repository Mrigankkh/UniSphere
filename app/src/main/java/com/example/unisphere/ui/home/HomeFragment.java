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
        postList.add(Post.builder().description("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book")
                .comments(Arrays.asList(Comment.builder().text("nice post").userId("tst1@g.com").build(), Comment.builder().text("great location").userId("tst2@g.com").build()))
                .likedByUserIds(new ArrayList<>(Arrays.asList("tst1","tst1","tst1","tst1","tst1")))
                .userId("test@northeastern.edu")
                .imageUrl("https://fastly.picsum.photos/id/1050/200/300.jpg?hmac=mMZp1DAD5EpHCZh-YBwfvrg5w327V3DoJQ8CmRAKF70").build());
        postList.add(Post.builder().description("It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.")
                .comments(Arrays.asList(Comment.builder().text("Awesome!").userId("user1@example.com").build(), Comment.builder().text("Love it!").userId("user2@example.com").build()))
                .likedByUserIds(new ArrayList<>(Arrays.asList("tst1","tst1","tst1","tst1","tst1","tst1","tst1","tst1","tst1")))

                .userId("test@northeastern.edu")
                .imageUrl("https://fastly.picsum.photos/id/237/200/300.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U").build());

        postList.add(Post.builder().description("Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage,")
                .comments(Arrays.asList(Comment.builder().text("Great job!").userId("user3@example.com").build(), Comment.builder().text("Nice work!").userId("user4@example.com").build()))
                .likedByUserIds(new ArrayList<>(Arrays.asList("tst1","tst1","tst1","tst1","tst1","tst1","tst1")))
                .userId("test@northeastern.edu")
                .imageUrl("https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI").build());


    }
}