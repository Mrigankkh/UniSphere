package com.example.unisphere.ui.events;

import static com.example.unisphere.service.Util.checkBlank;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.CommentAdapter;
import com.example.unisphere.model.Comment;
import com.example.unisphere.model.Event;
import com.example.unisphere.service.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventDetailsFragment extends Fragment {

    private static final String ARG_EVENT = "event";
    private Event event;
    private String UNIVERSITY = "northeastern";
    private String userId = "ogs@northeastern.edu";
    private CommentAdapter commentAdapter;
    private TextView commentsET;
    private EditText pollResultsLinkET;
    private EditText qsnResultsLinkET;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ImageView eventImage = view.findViewById(R.id.imageView_event);
        TextView eventTitle = view.findViewById(R.id.eventTitleTv);
        TextView eventDescription = view.findViewById(R.id.textView_event_description);
        TextView eventDateTv = view.findViewById(R.id.eventDateTv);
        TextView eventPlaceTv = view.findViewById(R.id.eventPlaceTv);
        commentsET = view.findViewById(R.id.editText_comment);
        Button commentsBtn = view.findViewById(R.id.button_post_comment);
        commentsBtn.setOnClickListener((viewButton) -> {
            addComment(event, commentsET.getText().toString());
        });
        Button editEventBtn = view.findViewById(R.id.editEventBtn);
        editEventBtn.setOnClickListener(this::gotoEditEvent);

        Button downloadPollResultBtn = view.findViewById(R.id.downloadPollResultBtn);
        downloadPollResultBtn.setOnClickListener(v -> getPollResultsFile());
        LinearLayout pollResultsLink = view.findViewById(R.id.pollResultsLink);
        pollResultsLinkET = view.findViewById(R.id.pollResultsLinkET);
        Button pollResultsLinkBtn = view.findViewById(R.id.pollResultsLinkBtn);
        pollResultsLinkBtn.setOnClickListener(v -> copyTextToClipboard(pollResultsLinkET));

        Button downloadQsnResultBtn = view.findViewById(R.id.downloadQsnResultBtn);
        downloadQsnResultBtn.setOnClickListener(v -> getQsnResultsFile());
        LinearLayout qsnResultsLink = view.findViewById(R.id.qsnResultsLink);
        qsnResultsLinkET = view.findViewById(R.id.qsnResultsLinkET);
        Button qsnResultsLinkBtn = view.findViewById(R.id.qsnResultsLinkBtn);
        qsnResultsLinkBtn.setOnClickListener(v -> copyTextToClipboard(qsnResultsLinkET));

        // Add condition to check userType == Organizer of this event
        if (event != null) {
            editEventBtn.setVisibility(View.VISIBLE);
            downloadPollResultBtn.setVisibility(View.VISIBLE);
            pollResultsLink.setVisibility(View.VISIBLE);
            downloadQsnResultBtn.setVisibility(View.VISIBLE);
            qsnResultsLink.setVisibility(View.VISIBLE);
        }

        RecyclerView commentsListRv = view.findViewById(R.id.recyclerViewComments);
        List<Comment> comments = event.getComments();
        commentsListRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentAdapter = new CommentAdapter(comments);
        commentsListRv.setAdapter(commentAdapter);

        if (event.getEventImage() == null || event.getEventImage().isEmpty()) {
            Picasso.get().load(R.drawable.no_image_available).resize(400, 300).into(eventImage);
        } else {
            Picasso.get().load(event.getEventImage()).resize(400, 300).into(eventImage);
        }

        eventTitle.setText(event.getEventTitle());
        eventDescription.setText(event.getEventDescription());
        eventDateTv.setText(Util.convertDateTime(event.getEventStartDate()) + " to " + Util.convertDateTime(event.getEventEndDate()));
        eventPlaceTv.setText(event.getEventPlace());

        RelativeLayout parentLayout = view.findViewById(R.id.formComponentsRv);
        createFormComponents(parentLayout);
        return view;
    }

    private void getQsnResultsFile() {
        String questionResults = event.getQuestionResults();
        if (checkBlank(questionResults)) {
            Toast.makeText(requireContext(), "There are no responses yet!", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference csvRef = storageRef.child("events/qsn_results/QuestionResults-" + event.getEventTitle() + ".csv");
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/csv")
                    .build();

            UploadTask uploadTask = csvRef.putBytes(questionResults.getBytes(), metadata);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                csvRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    event.setQuestionCsvLink(downloadUrl);
                    qsnResultsLinkET.setText(downloadUrl);
                    Toast.makeText(requireContext(), "Question results are ready in a csv file", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error getting download URL!", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error uploading CSV file!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void getPollResultsFile() {
        String pollResults = event.getPollResults();
        if (checkBlank(pollResults)) {
            Toast.makeText(requireContext(), "There are no responses yet!", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference csvRef = storageRef.child("events/poll_results/PollResults-" + event.getEventTitle() + ".csv");
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/csv")
                    .build();

            UploadTask uploadTask = csvRef.putBytes(pollResults.getBytes(), metadata);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                csvRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    event.setPollCsvLink(downloadUrl);
                    pollResultsLinkET.setText(downloadUrl);
                    Toast.makeText(requireContext(), "Poll results are ready in a csv file", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error getting download URL!", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error uploading CSV file!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void gotoEditEvent(View v) {
        if (event != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ARG_EVENT, event);
            Navigation.findNavController(requireView()).navigate(R.id.editEventFragment, bundle);
        }
    }

    private void createFormComponents(RelativeLayout parentLayout) {
        int lastElementId = -1;
        if (event.getRadioLabel() != null && !event.getRadioLabel().isEmpty()) {
            lastElementId = createRadioComponent(parentLayout);
        }

        if (event.getInputTextLabel() != null && !event.getInputTextLabel().isEmpty()) {
            createEditTextComponent(parentLayout, lastElementId);
        }
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
                RadioButton selectedRadioButton = requireView().findViewById(radioGroup.getCheckedRadioButtonId());
                String inputText = selectedRadioButton.getText().toString();

                if (selectedRadioButton != null && !inputText.isEmpty()) {
                    String selectedOptionText = selectedRadioButton.getText().toString();
                    saveRadioResponse(event, selectedOptionText);
                } else {
                    // Display error message if any field is empty
                    Toast.makeText(requireContext(), "Please select an option and enter text", Toast.LENGTH_SHORT).show();
                }
            }
        });
        parentLayout.addView(radioLabel);
        parentLayout.addView(radioGroup);
        parentLayout.addView(radioSubmitButton);
        return radioSubmitButton.getId();
    }

    private void saveRadioResponse(Event event, String selectedOptionText) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(UNIVERSITY).child("events").child(event.getEventId());
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pollResults = event.getPollResults();
                // TODO update userId to current user's id
                if (pollResults == null) {
                    pollResults = "";
                }
                pollResults += "\n" + event.getRadioLabel() + "," + selectedOptionText + "," + userId;
                pollResultsLinkET.setText("");
                event.setPollResults(pollResults);
                eventRef.child("pollResults").setValue(pollResults)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Your choice was recorded successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to record your choice", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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

        textSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString().trim();
                if (!checkBlank(inputText)) {
                    saveQuestionResponse(event, inputText);
                } else {
                    Toast.makeText(requireContext(), "Please enter your answer to submit!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add views to the parent layout

        parentLayout.addView(editLabel);
        parentLayout.addView(editText);
        parentLayout.addView(textSubmitButton);
    }

    private void saveQuestionResponse(Event event, String answerText) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(UNIVERSITY).child("events").child(event.getEventId());
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String questionResults = event.getQuestionResults();
                if (questionResults == null) {
                    questionResults = "";
                }
                // TODO update userId to current user's id
                questionResults += "\n" + event.getInputTextLabel() + "," + answerText + "," + userId;
                qsnResultsLinkET.setText("");
                event.setQuestionResults(questionResults);
                eventRef.child("questionResults").setValue(questionResults)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Your answer was recorded successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to record your answer", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addComment(Event event, String commentText) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(UNIVERSITY).child("events").child(event.getEventId());

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> comments = event.getComments();
                Comment newComment = new Comment(userId, commentText);
                comments.add(newComment);
                commentsET.setText("");
                commentAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Comment posted successfully", Toast.LENGTH_SHORT).show();

                eventRef.child("comments").setValue(comments)
                        .addOnSuccessListener(aVoid -> {
                            event.setComments(comments);
                            commentAdapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Comment posted successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to post comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("EventDetailsFragment", "Failed to post comment", e);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void copyTextToClipboard(EditText editText) {
        String textToCopy = editText.getText().toString();
        System.out.println(textToCopy);
        ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null && !checkBlank(textToCopy)) {
            ClipData clipData = ClipData.newPlainText("text", textToCopy);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(requireContext(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No responses! Try Download again!", Toast.LENGTH_SHORT).show();
        }
    }
}
