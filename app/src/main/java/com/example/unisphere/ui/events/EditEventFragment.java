package com.example.unisphere.ui.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.unisphere.R;
import com.example.unisphere.model.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EditEventFragment extends Fragment {

    private static final String ARG_EVENT = "event";
    private Event event;
    private String UNIVERSITY = "northeastern";
    private String userId = "ogs@northeastern.edu";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference eventDatabaseReference;

    public static EditEventFragment newInstance(Event event) {
        EditEventFragment fragment = new EditEventFragment();
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
        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));
        eventDatabaseReference = firebaseDatabase.getReference().child(UNIVERSITY).child(getString(R.string.events));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);
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
                if (!checkBlank(imageUrl))
                    Picasso.get().load(imageUrl).into(eventImageView);
            }
        });

        EditText dateFromTv = view.findViewById(R.id.fromDateTv);
        EditText dateToTv = view.findViewById(R.id.toDateTv);
        EditText timeFromTv = view.findViewById(R.id.fromTimeTv);
        EditText timeToTv = view.findViewById(R.id.toTimeTv);

        DatePickerDialog.OnDateSetListener fromDateListener = (v, year, monthOfYear, dayOfMonth) -> {
            String selectedDate = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
            dateFromTv.setText(selectedDate);
        };
        TimePickerDialog.OnTimeSetListener fromTimeListener = (v, hourOfDay, minute) -> {
            String selectedTime = hourOfDay + ":" + String.format("%02d", minute) + ":00";
            timeFromTv.setText(selectedTime);
        };

        DatePickerDialog.OnDateSetListener toDateListener = (v, year, monthOfYear, dayOfMonth) -> {
            String selectedDate = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
            dateToTv.setText(selectedDate);
        };
        TimePickerDialog.OnTimeSetListener toTimeListener = (v, hourOfDay, minute) -> {
            String selectedTime = hourOfDay + ":" + String.format("%02d", minute) + ":00";
            timeToTv.setText(selectedTime);
        };

        dateFromTv.setOnClickListener(v -> showDatePicker(fromDateListener));

        timeFromTv.setOnClickListener(v -> showTimePicker(fromTimeListener));

        dateToTv.setOnClickListener(v -> showDatePicker(toDateListener));

        timeToTv.setOnClickListener(v -> showTimePicker(toTimeListener));

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

        if (event != null) {
            editTextTitle.setText(event.getEventTitle());
            editTextDescription.setText(event.getEventDescription());
            editTextPlace.setText(event.getEventPlace());
            editTextImageLink.setText(event.getEventImage());
            //Picasso.get().load(event.getOrganizerImage()).into(eventImageView);
            String[] fromDT = event.getEventStartDate().split("T");
            dateFromTv.setText(fromDT[0]);
            timeFromTv.setText(fromDT[1].substring(0, fromDT[1].length() - 3));
            String[] toDT = event.getEventEndDate().split("T");
            dateToTv.setText(toDT[0]);
            timeToTv.setText(toDT[1].substring(0, toDT[1].length() - 3));
            qsnEditText.setText(event.getInputTextLabel());
            qsnBtnEditText.setText(event.getInputTextButtonLabel());
            if (!checkBlank(event.getInputTextLabel())) {
                addQsnCheckBox.setChecked(true);
            }
            radioLabelEditText.setText(event.getRadioLabel());
            radioOptionsEditText.setText(String.join(",", event.getRadioOptions()));
            radioBtnLabelEditText.setText(event.getRadioButtonLabel());
            if (!checkBlank(event.getRadioLabel())) {
                addRadioCheckBox.setChecked(true);
            }
        }

        Button editEventBtn = view.findViewById(R.id.editEventBtn);
        editEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitle = editTextTitle.getText().toString();
                String eventDescription = editTextDescription.getText().toString();
                String eventPlace = editTextPlace.getText().toString();
                String eventImageUrl = editTextImageLink.getText().toString();
                // "2024-03-04T12:33:58"
                String dateFrom = dateFromTv.getText().toString();
                String timeFrom = timeFromTv.getText().toString();
                String eventStartDateTime = dateFrom + "T" + timeFrom + ":00";

                String dateTo = dateToTv.getText().toString();
                String timeTo = timeToTv.getText().toString();
                String eventEndDateTime = dateTo + "T" + timeTo + ":00";

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

                event.setEventTitle(eventTitle);
                event.setEventDescription(eventDescription);
                event.setEventImage(eventImageUrl);
                event.setEventStartDate(eventStartDateTime);
                event.setEventEndDate(eventEndDateTime);
                event.setEventPlace(eventPlace);
                event.setInputTextLabel(qsnLabel);
                event.setInputTextButtonLabel(qsnBtnLabel);
                event.setRadioLabel(radioLabel);
                event.setRadioOptions(radioOptionsList);
                event.setRadioButtonLabel(radioBtnLabel);
                updateEventOnFirebase(event);
                Toast.makeText(requireContext(), "Event is successfully updated", Toast.LENGTH_SHORT).show();
                returnToEventDetailFragment(event);
            }
        });

        return view;
    }

    private void returnToEventDetailFragment(Event updatedEvent) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EVENT, updatedEvent);
        Navigation.findNavController(requireView()).navigate(R.id.eventDetailsFragment, bundle);
    }

    private void showDatePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                dateSetListener,
                year,
                month,
                dayOfMonth
        );
        datePickerDialog.show();
    }

    private void showTimePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                timeSetListener,
                hourOfDay,
                minute,
                DateFormat.is24HourFormat(getContext())
        );
        timePickerDialog.show();
    }

    private boolean areOptionsValid(String[] radioOptionsArray) {
        for (String option : radioOptionsArray) {
            if (checkBlank(option))
                return false;
        }
        return true;
    }

    public void updateEventOnFirebase(Event event) {
        String key = event.getEventId();
        eventDatabaseReference.child(key).setValue(event)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Event added to Firebase");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error adding message to Firebase");
                    e.printStackTrace();
                });
    }

    boolean checkBlank(String value) {
        return value == null || value.isEmpty() || value.trim().isEmpty();
    }

}

