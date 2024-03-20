package com.example.unisphere.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.unisphere.R;
import com.example.unisphere.model.Event;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateEventFragment extends Fragment {

    private static final String ARG_EVENT = "event";
    private Event event;

    public static CreateEventFragment newInstance(Event event) {
        CreateEventFragment fragment = new CreateEventFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
//        ImageView eventImage = view.findViewById(R.id.imageView_event);
//        TextView eventTitle = view.findViewById(R.id.eventTitleTv);
//        TextView eventDescription = view.findViewById(R.id.textView_event_description);
//        TextView eventDateTv = view.findViewById(R.id.eventDateTv);
//        TextView eventPlaceTv = view.findViewById(R.id.eventPlaceTv);
//        EditText commentsET = view.findViewById(R.id.editText_comment);
//        Button commentsBtn = view.findViewById(R.id.button_post_comment);
//        RecyclerView commentsListRv = view.findViewById(R.id.recyclerViewComments);
//
//        String imageUrl = "https://fastly.picsum.photos/id/1050/200/300.jpg?hmac=mMZp1DAD5EpHCZh-YBwfvrg5w327V3DoJQ8CmRAKF70";
//        Picasso.get().load(imageUrl).into(eventImage);
//        eventTitle.setText(event.getEventTitle());
//        eventDescription.setText(event.getEventTitle());
//        eventDateTv.setText(Util.convertDateTime(event.getEventDate()));
//        eventPlaceTv.setText(event.getEventPlace());
//
//        RelativeLayout parentLayout = view.findViewById(R.id.formComponentsRv);

        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        EditText editTextPlace = view.findViewById(R.id.editTextPlace);
        EditText editTextImageLink = view.findViewById(R.id.editTextImageLink);
        Button uploadImageBtn = view.findViewById(R.id.buttonUploadImage);
        ImageView eventImageView = view.findViewById(R.id.imageView);

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = editTextImageLink.getText().toString();
                Picasso.get().load(imageUrl).into(eventImageView);
            }
        });

        DatePicker dateFromDP = view.findViewById(R.id.datePickerFrom);
        DatePicker dateToDP = view.findViewById(R.id.datePickerTo);
        TimePicker timeFromTP = view.findViewById(R.id.timePickerFrom);
        TimePicker timeToTP = view.findViewById(R.id.timePickerTo);

        CheckBox addRadioCheckBox = view.findViewById(R.id.addRadioCb);
        EditText radioLabelEditText = view.findViewById(R.id.radioLabelET);
        EditText radioOptionsEditText = view.findViewById(R.id.radioOptionsET);
        EditText radioBtnLabelEditText = view.findViewById(R.id.radioBtnLabelET);
        CheckBox addQsnCheckBox = view.findViewById(R.id.addETCB);
        EditText qsnEditText = view.findViewById(R.id.questionET);
        EditText qsnBtnEditText = view.findViewById(R.id.questionLabelET);

        addRadioCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioLabelEditText.setVisibility(View.VISIBLE);
                radioOptionsEditText.setVisibility(View.VISIBLE);
                radioBtnLabelEditText.setVisibility(View.VISIBLE);
            } else {
                radioLabelEditText.setVisibility(View.GONE);
                radioOptionsEditText.setVisibility(View.GONE);
                radioBtnLabelEditText.setVisibility(View.GONE);
            }
        });

        addQsnCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                qsnEditText.setVisibility(View.VISIBLE);
                qsnBtnEditText.setVisibility(View.VISIBLE);
            } else {
                qsnEditText.setVisibility(View.GONE);
                qsnBtnEditText.setVisibility(View.GONE);
            }
        });

        Button createEventBtn = view.findViewById(R.id.creatEventBtn);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitle = editTextTitle.getText().toString();
                String eventDescription = editTextDescription.getText().toString();
                String eventPlace = editTextPlace.getText().toString();
                String eventImageUrl = editTextImageLink.getText().toString();
                // "2024-03-04T12:33:58"
                String dateFrom = dateFromDP.getYear() + "-" + dateFromDP.getMonth() + "-" + dateFromDP.getDayOfMonth();
                String timeFrom = timeFromTP.getHour() + ":" + timeFromTP.getMinute() + ":00";
                String eventStartDateTime = dateFrom + "T" + timeFrom;

                String dateTo = dateToDP.getYear() + "-" + dateToDP.getMonth() + "-" + dateToDP.getDayOfMonth();
                String timeTo = timeToTP.getHour() + ":" + timeToTP.getMinute() + ":00";
                String eventEndDateTime = dateTo + "T" + timeTo;

                String radioLabel = null;
                String radioOptions = null;
                String radioBtnLabel = null;
                List<String> radioOptionsList = null;
                if (addRadioCheckBox.isChecked()) {
                    radioLabel = radioLabelEditText.getText().toString();
                    radioOptions = radioOptionsEditText.getText().toString();
                    radioBtnLabel = radioBtnLabelEditText.getText().toString();

                    if (checkBlank(radioLabel) || checkBlank(radioOptions) || checkBlank(radioBtnLabel)) {
                        Toast.makeText(requireContext(), "All fields need to be answered if options checkbox is checked!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] radioOptionsArray = radioOptions.trim().split(",");
                    if (!areOptionsValid(radioOptionsArray) || radioOptionsArray.length < 2) {
                        Toast.makeText(requireContext(), "Incorrect options provided! Options need to be comma-separated, atleast two options", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    radioOptionsList = new ArrayList<>(Arrays.asList(radioOptionsArray));
                }

                String qsnLabel = null;
                String qsnBtnLabel = null;

                if (addQsnCheckBox.isChecked()) {
                    qsnLabel = qsnEditText.getText().toString();
                    qsnBtnLabel = qsnBtnEditText.getText().toString();

                    if (checkBlank(qsnLabel) || checkBlank(qsnBtnLabel)) {
                        Toast.makeText(requireContext(), "All fields need to be answered if question checkbox is checked!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (checkBlank(eventTitle) || checkBlank(eventDescription) || checkBlank(eventPlace)) {
                    Toast.makeText(requireContext(), "Some inputs are missing!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Event newEvent = new Event(eventTitle,
                        eventDescription,
                        eventImageUrl,
                        eventStartDateTime,
                        eventEndDateTime,
                        eventPlace,
                        qsnLabel,
                        qsnBtnLabel,
                        radioLabel,
                        radioOptionsList,
                        radioBtnLabel);

                System.out.println(newEvent);

            }
        });

        return view;
    }

    private boolean areOptionsValid(String[] radioOptionsArray) {
        for (String option : radioOptionsArray) {
            if (checkBlank(option))
                return false;
        }
        return true;
    }

    boolean checkBlank(String value) {
        return value == null || value.isEmpty() || value.trim().isEmpty();
    }

}
