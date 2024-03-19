package com.example.unisphere.ui.events;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Event;
import com.example.unisphere.service.Util;
import com.squareup.picasso.Picasso;

public class EventDetailsFragment extends Fragment {

    private static final String ARG_EVENT = "event";
    private Event event;

    public static EventDetailsFragment newInstance(Event event) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
            System.out.println(event);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ImageView eventImage = view.findViewById(R.id.imageView_event);
        TextView eventTitle = view.findViewById(R.id.eventTitleTv);
        TextView eventDescription = view.findViewById(R.id.textView_event_description);
        TextView eventDateTv = view.findViewById(R.id.eventDateTv);
        TextView eventPlaceTv = view.findViewById(R.id.eventPlaceTv);
        EditText commentsET = view.findViewById(R.id.editText_comment);
        Button commentsBtn = view.findViewById(R.id.button_post_comment);
        RecyclerView commentsListRv = view.findViewById(R.id.recyclerViewComments);

        String imageUrl = "https://fastly.picsum.photos/id/1050/200/300.jpg?hmac=mMZp1DAD5EpHCZh-YBwfvrg5w327V3DoJQ8CmRAKF70";
        Picasso.get().load(imageUrl).into(eventImage);
        eventTitle.setText(event.getEventTitle());
        eventDescription.setText(event.getEventTitle());
        eventDateTv.setText(Util.convertDateTime(event.getEventDate()));
        eventPlaceTv.setText(event.getEventPlace());

        RelativeLayout parentLayout = view.findViewById(R.id.formComponentsRv);

        // Create the radio group
        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setId(View.generateViewId());
        RelativeLayout.LayoutParams radioParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        radioParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        radioParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        radioGroup.setLayoutParams(radioParams);

        // Create radio buttons and add them to the radio group
        String[] radioOptions = {"Option 1", "Option 2"};
        for (int i = 0; i < radioOptions.length; i++) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(radioOptions[i]);
            radioButton.setId(View.generateViewId());
            radioGroup.addView(radioButton);
        }

        // Create the input field
        EditText editText = new EditText(requireContext());
        editText.setId(View.generateViewId());
        RelativeLayout.LayoutParams editParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        editParams.addRule(RelativeLayout.BELOW, radioGroup.getId());
        editText.setLayoutParams(editParams);

        // Create the submit button
        Button submitButton = new Button(requireContext());
        submitButton.setText("Submit");
        submitButton.setId(View.generateViewId());
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.addRule(RelativeLayout.BELOW, editText.getId());
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        submitButton.setLayoutParams(buttonParams);

        // Set click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected radio button
                RadioButton selectedRadioButton = requireView().findViewById(radioGroup.getCheckedRadioButtonId());

                // Get input text
                String inputText = editText.getText().toString().trim();

                // Check if radio button and input field are selected/filled
                if (selectedRadioButton != null && !inputText.isEmpty()) {
                    // Display selected option and input text
                    Toast.makeText(requireContext(), "Selected Option: " + selectedRadioButton.getText() + "\nInput Text: " + inputText, Toast.LENGTH_SHORT).show();
                } else {
                    // Display error message if any field is empty
                    Toast.makeText(requireContext(), "Please select an option and enter text", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add views to the parent layout
        parentLayout.addView(radioGroup);
        parentLayout.addView(editText);
        parentLayout.addView(submitButton);

        return view;
//        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

//        ImageView imageViewPost = view.findViewById(R.id.imageView_post);
//        TextView textViewDescription = view.findViewById(R.id.textView_post_description);
//        TextView textViewLikeCount = view.findViewById(R.id.like_count);
//        TextView textViewCommentCount = view.findViewById(R.id.comment_count);
//
//        if (post != null) {
//            Picasso.get().load(post.getImageUrl()).into(imageViewPost);
//            textViewDescription.setText(post.getDescription());
//            textViewLikeCount.setText(String.valueOf(post.getLikedByUserIds().size()));
//            textViewCommentCount.setText(String.valueOf(post.getComments().size()));
//
//            // Assuming you have a list of comments in your Post object
//            List<Comment> comments = post.getComments();
//
//// Initialize the RecyclerView
//            RecyclerView recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
//            recyclerViewComments.setLayoutManager(new LinearLayoutManager(requireContext()));
//            CommentAdapter commentAdapter = new CommentAdapter(comments);
//            recyclerViewComments.setAdapter(commentAdapter);
//
//        }

//        return view;
    }
}
