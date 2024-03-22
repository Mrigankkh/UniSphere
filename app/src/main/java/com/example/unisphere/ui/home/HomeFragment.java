package com.example.unisphere.ui.home;

import static android.os.Environment.*;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.PostAdapter;
import com.example.unisphere.model.Comment;
import com.example.unisphere.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;


public class HomeFragment extends Fragment {

    List<Post> postList;

    RecyclerView recyclerView;
    PostAdapter postAdapter;

    private static final String ARG_POST = "post";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageCapture imageCapture;


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


        return homeView;
    }

    private void showAddPostPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View popupView = getLayoutInflater().inflate(R.layout.dialog_new_post, null);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Set background color for dialog window
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.setOnDismissListener(dialogInterface -> {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

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

            dialog.dismiss();
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

            // Perform post action
            // TODO: IMPLEMENT SUBMITTING FORM
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            // Use the selected image URI
            // For example, you can display the image in an ImageView
            // imageView.setImageURI(uri);
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

    private File createImageFile()  {
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
        Navigation.findNavController(requireView()).navigate(R.id.postDetailsFragment,bundle);
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