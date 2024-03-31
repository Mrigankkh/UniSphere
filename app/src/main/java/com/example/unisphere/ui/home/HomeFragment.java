package com.example.unisphere.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_PICTURES;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.unisphere.R;
import com.example.unisphere.adapter.PostAdapter;
import com.example.unisphere.model.Post;
import com.example.unisphere.model.User;
import com.example.unisphere.service.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class HomeFragment extends Fragment {

    private static final String ARG_POST = "post";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private static final int PICK_IMAGE_REQUEST = 1;
    List<Post> postList;
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    ImageCapture imageCapture;
    private FirebaseDatabase firebaseDatabase;

    Uri photoUriData;

    View popupView;

    // TODO: change this to get from profile
    String universityKey;

    String userId;

    private DatabaseReference postDatabaseReference;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPreferences preferences;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Camera permission is required to use the camera.", Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = homeView.findViewById(R.id.recyclerViewPostsHome);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        postAdapter = new PostAdapter(requireContext(), postList, homeView.findViewById(android.R.id.content), this::onPostClick);
        recyclerView.setAdapter(postAdapter);

        FloatingActionButton fab = homeView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddPostPopup());

        swipeRefreshLayout = homeView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Your refresh code here, for example, fetching new data from Firebase
            retrievePostsFromFirebase();
            swipeRefreshLayout.setRefreshing(false);
        });



        return homeView;
    }

    private void showAddPostPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        this.popupView = getLayoutInflater().inflate(R.layout.dialog_new_post, null);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Set background color for dialog window
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.setOnDismissListener(dialogInterface -> {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
            this.popupView = null;
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(requireContext()));
        });


        PreviewView previewView = popupView.findViewById(R.id.previewViewCamera);
        EditText editTextDescription = popupView.findViewById(R.id.editTextCaption);
        Button buttonPost = popupView.findViewById(R.id.buttonPost);
        Button buttonUpload = popupView.findViewById(R.id.buttonUpload);
        Button buttonClick = popupView.findViewById(R.id.buttonClick);
        Button buttonClickPhoto = popupView.findViewById(R.id.buttonClickTst);
        buttonClickPhoto.setVisibility(View.GONE);


        buttonUpload.setOnClickListener(view -> {
            // Perform action for uploading picture
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            buttonClick.setVisibility(View.GONE);
            buttonUpload.setVisibility(View.GONE);

            startActivityForResult(intent, PICK_IMAGE_REQUEST);


        });

        buttonClick.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                requestPermissions(
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            } else {

                // Permission is granted, open camera
                buttonClickPhoto.setVisibility(View.VISIBLE);
                buttonClick.setVisibility(View.GONE);
                buttonUpload.setVisibility(View.GONE);
                openCamera(previewView);
            }
        });


        buttonClickPhoto.setOnClickListener(view -> {
            File photoFile = createImageFile();
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    // Image capture successful
                    Toast.makeText(requireContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
                    buttonClick.setVisibility(View.GONE);
                    buttonClickPhoto.setVisibility(View.GONE);
                    previewView.setVisibility(View.GONE);
                    buttonUpload.setVisibility(View.GONE);
                    ImageView imageViewPreview = popupView.findViewById(R.id.imageViewPreview);
                    imageViewPreview.setImageURI(Uri.fromFile(photoFile));
                    photoUriData = Uri.fromFile(photoFile);
                    imageViewPreview.setVisibility(View.VISIBLE);

                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    // Image capture failed
                    Toast.makeText(requireContext(), "Error saving image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Post button click listener
        buttonPost.setOnClickListener(view -> {
            String description = editTextDescription.getText().toString().trim();


            if (photoUriData == null) {
                Toast.makeText(requireContext(), "Please select or take a picture before posting", Toast.LENGTH_SHORT).show();
                return;
            }

            Post post = new Post();
            post.description=description;
            post.userId = userId;
            createPostOnFirebase(post);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            this.photoUriData = uri;
            ImageView imageViewPreview = popupView.findViewById(R.id.imageViewPreview);
            imageViewPreview.setImageURI(uri);
            imageViewPreview.setVisibility(View.VISIBLE);

        }
    }


    private void openCamera(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                if (!cameraProvider.hasCamera(cameraSelector)) {
                    Log.e("Camera", "Camera not available");
                    return;
                }
                bindPreview(cameraProvider, previewView);
                setupImageCapture(cameraProvider, cameraSelector);


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (CameraInfoUnavailableException e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the directory where the image will be saved
        File storageDir = requireContext().getExternalFilesDir(DIRECTORY_PICTURES);

        // Create the File object
        File imageFile = null;
        try {
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Return the File object
        return imageFile;
    }


    private void setupImageCapture(ProcessCameraProvider cameraProvider, CameraSelector cameraSelector) {
        ImageCapture.Builder builder = new ImageCapture.Builder();
        this.imageCapture = builder.build();
        cameraProvider.bindToLifecycle((LifecycleOwner) requireContext(), cameraSelector, imageCapture);
    }


    private void bindPreview(ProcessCameraProvider cameraProvider, PreviewView previewView) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner) requireContext(), cameraSelector, preview);
    }

    private void onPostClick(int position) {
        Post post = postList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_POST, post);
        Navigation.findNavController(requireView()).navigate(R.id.postDetailsFragment, bundle);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        User userDataPreferences = Util.getUserDataFromSharedPreferences(preferences);
        this.userId = userDataPreferences.getEmailID();
        this.universityKey = userDataPreferences.getUniversity();

        super.onCreate(savedInstanceState);
        postList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));

        postDatabaseReference = firebaseDatabase.getReference().child(universityKey).child(getString(R.string.posts));
        retrievePostsFromFirebase();

    }


    public void createPostOnFirebase(Post post) {
        String key = postDatabaseReference.push().getKey();

        // TODO check if photoFileClicked is null
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + key + ".jpg");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), photoUriData);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    photoUriData = null;
                    post.setImageUrl(imageUrl);
                    post.setKeyFirebase(key);

                    // Update the local list of posts
                    postList.add(0, post);

                    // Notify the adapter that a new item has been inserted at position 0
                    postAdapter.notifyItemInserted(0);

                    postDatabaseReference.child(key).setValue(post)
                            .addOnSuccessListener(aVoid -> {
                                System.out.println("Message added to Firebase");
                                Toast.makeText(requireContext(), "Message Sent ", Toast.LENGTH_SHORT).show();
                                retrievePostsFromFirebase();

                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Error adding message to Firebase");
                                e.printStackTrace();
                            });
                });
            }).addOnFailureListener(e -> {
                System.out.println("Error uploading image to Firebase");
                e.printStackTrace();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retrievePostsFromFirebase() {
        postDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    posts.add(post);
                }

                // Assuming postList is a member variable of your class
                postList.clear();
                postList.addAll(posts);

                // Notify the adapter of the data change TODO if this can be improved
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}