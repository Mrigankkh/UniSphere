package com.example.unisphere.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.unisphere.R;
import com.example.unisphere.model.Post;

public class EditPostDialogFragment extends DialogFragment {

    private EditText editText;
    private Button buttonDelete;
    private Button buttonSaveChanges;

    private Post post;

    public EditPostDialogFragment(Post post) {
        this.post = post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        editText = view.findViewById(R.id.editText_post);
        buttonDelete = view.findViewById(R.id.button_delete);
        buttonSaveChanges = view.findViewById(R.id.button_save_changes);

        editText.setText(post.getDescription());

        buttonDelete.setOnClickListener(v -> {
            // TODO: Implement delete logic
            dismiss();
        });

        buttonSaveChanges.setOnClickListener(v -> {
            // TODO: Implement save changes logic
            dismiss();
        });

        return view;
    }
}
