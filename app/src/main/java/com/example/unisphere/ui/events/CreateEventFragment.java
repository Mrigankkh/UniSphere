package com.example.unisphere.ui.events;

import static com.example.unisphere.service.Util.checkBlank;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateEventFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ARG_EVENT = "event";
    private String UNIVERSITY = "northeastern";
    private String userId = "ogs@northeastern.edu";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference eventDatabaseReference;

    private ImageView eventImageView;
    private Uri eventImageUri;

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
        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));
        eventDatabaseReference = firebaseDatabase.getReference().child(UNIVERSITY).child(getString(R.string.events));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        EditText editTextPlace = view.findViewById(R.id.editTextPlace);
        Button uploadImageBtn = view.findViewById(R.id.buttonUploadImage);
        eventImageView = view.findViewById(R.id.imageView_event);

        eventImageView.setOnClickListener(v -> onClickUploadImageLayout(v));
        uploadImageBtn.setOnClickListener(v -> onClickUploadImageLayout(v));

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

        Button createEventBtn = view.findViewById(R.id.creatEventBtn);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitle = editTextTitle.getText().toString();
                String eventDescription = editTextDescription.getText().toString();
                String eventPlace = editTextPlace.getText().toString();

                String dateFrom = dateFromTv.getText().toString();
                String timeFrom = timeFromTv.getText().toString();
                String eventStartDateTime = dateFrom + "T" + timeFrom;

                String dateTo = dateToTv.getText().toString();
                String timeTo = timeToTv.getText().toString();
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

                Event newEvent = new Event(userId, null, eventTitle,
                        eventDescription,
                        null,
                        eventStartDateTime,
                        eventEndDateTime,
                        eventPlace,
                        qsnLabel,
                        qsnBtnLabel,
                        radioLabel,
                        radioOptionsList,
                        radioBtnLabel, null);
                uploadImageAndAddToEvent(newEvent);
                returnToEventDetailFragment(newEvent);
            }
        });

        return view;
    }

    private void uploadImageAndAddToEvent(Event event) {
        String key = eventDatabaseReference.push().getKey();
        event.setEventId(key);
        if (this.eventImageUri == null) {
            // No image to upload
            createEventOnFirebase(event);
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("events/images/" + event.getEventTitle() + key + ".jpg");

        UploadTask uploadTask = imageRef.putFile(this.eventImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                event.setEventImage(imageUrl);
                createEventOnFirebase(event);
            });
        }).addOnFailureListener(e -> {
            System.out.println("Error uploading image to Firebase");
            e.printStackTrace();
        });
    }

    public void onClickUploadImageLayout(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            this.eventImageUri = uri;
            Picasso.get().load(this.eventImageUri).resize(400, 300).into(eventImageView);
        }
    }

    private void returnToEventDetailFragment(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EVENT, event);
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

    public void createEventOnFirebase(Event event) {
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

}
