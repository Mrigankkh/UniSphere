package com.example.unisphere.ui.events;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Event;
import com.example.unisphere.service.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        eventDateTv.setText(Util.convertDateTime(event.getEventStartDate()) + "-" + Util.convertDateTime(event.getEventEndDate()));
        eventPlaceTv.setText(event.getEventPlace());

        RelativeLayout parentLayout = view.findViewById(R.id.formComponentsRv);

        createFormComponents(parentLayout);

        return view;
    }

    private void createFormComponents(RelativeLayout parentLayout) {
        int lastElementId = -1;
        if (event.getRadioLabel() != null && !event.getRadioLabel().isEmpty())
            lastElementId = createRadioComponent(parentLayout);

        if (event.getInputTextLabel() != null && !event.getInputTextLabel().isEmpty())
            createEditTextComponent(parentLayout, lastElementId);
    }

    private int createRadioComponent(RelativeLayout parentLayout) {
        TextView radioLabel = new TextView(requireContext());
        radioLabel.setText(event.getRadioLabel());
        radioLabel.setId(View.generateViewId());
        RelativeLayout.LayoutParams radioLabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        radioLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        radioLabel.setLayoutParams(radioLabelParams);
        // Create the radio group
        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setId(View.generateViewId());
        RelativeLayout.LayoutParams radioParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        radioParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        radioParams.addRule(RelativeLayout.BELOW, radioLabel.getId());
        radioGroup.setLayoutParams(radioParams);

        // Create radio buttons and add them to the radio group
        List<String> radioOptions = event.getRadioOptions();
        for (String radioOption : radioOptions) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(radioOption);
            radioButton.setId(View.generateViewId());
            radioGroup.addView(radioButton);
        }

        // Create the submit button
        Button radioSubmitButton = new Button(requireContext());
        radioSubmitButton.setText(event.getRadioButtonLabel());
        radioSubmitButton.setId(View.generateViewId());
        radioSubmitButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_500)));
        radioSubmitButton.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams radioButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        radioButtonParams.addRule(RelativeLayout.BELOW, radioGroup.getId());
        radioButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        radioSubmitButton.setLayoutParams(radioButtonParams);

        // Set click listener for submit button
        radioSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected radio button
                RadioButton selectedRadioButton = requireView().findViewById(radioGroup.getCheckedRadioButtonId());

                // Get input text
//                String inputText = radioGroup.getCheckedRadioButtonId().toString().trim();

                // Check if radio button and input field are selected/filled
//                if (selectedRadioButton != null && !inputText.isEmpty()) {
//                    // Display selected option and input text
//                    Toast.makeText(requireContext(), "Selected Option: " + selectedRadioButton.getText() + "\nInput Text: " + inputText, Toast.LENGTH_SHORT).show();
//                } else {
//                    // Display error message if any field is empty
//                    Toast.makeText(requireContext(), "Please select an option and enter text", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        parentLayout.addView(radioLabel);
        parentLayout.addView(radioGroup);
        parentLayout.addView(radioSubmitButton);
        return radioSubmitButton.getId();
    }

    private void createEditTextComponent(RelativeLayout parentLayout, int lastElementId) {

        TextView editLabel = new TextView(requireContext());
        editLabel.setText(event.getInputTextLabel());
        editLabel.setId(View.generateViewId());
        RelativeLayout.LayoutParams editLabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        editLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if (lastElementId != -1)
            editLabelParams.addRule(RelativeLayout.BELOW, lastElementId);
        editLabel.setLayoutParams(editLabelParams);

        // Create the input field
        EditText editText = new EditText(requireContext());
        editText.setId(View.generateViewId());
        RelativeLayout.LayoutParams editParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        editParams.addRule(RelativeLayout.BELOW, editLabel.getId());
        editText.setLayoutParams(editParams);

        // Create the submit button
        Button textSubmitButton = new Button(requireContext());
        textSubmitButton.setText(event.getInputTextButtonLabel());
        textSubmitButton.setId(View.generateViewId());
        textSubmitButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_500)));
        textSubmitButton.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams textButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textButtonParams.addRule(RelativeLayout.BELOW, editText.getId());
        textButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textSubmitButton.setLayoutParams(textButtonParams);

        // Set click listener for submit button
        textSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get input text
                String inputText = editText.getText().toString().trim();

                // Check if radio button and input field are selected/filled
                if (!inputText.isEmpty()) {
                    // Display selected option and input text
                    Toast.makeText(requireContext(), "Input Text: " + inputText, Toast.LENGTH_SHORT).show();
                } else {
                    // Display error message if any field is empty
                    Toast.makeText(requireContext(), "Please select an option and enter text", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add views to the parent layout

        parentLayout.addView(editLabel);
        parentLayout.addView(editText);
        parentLayout.addView(textSubmitButton);
    }
}
