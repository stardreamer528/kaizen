package com.tuneerkhargonkar.studios.kaizen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.connection.ConnectionTokenProvider;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PopUpClass extends AppCompatActivity {
    Context context = PopUpClass.this;
    Uri alterAfterUri;
    String uid, temp_URL_String,alterAfterURL;
    StorageReference sReference;
    Activity activity;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 234;
    //PopupWindow display method


    public void showPopupWindow(View view, Kaizen kaizen) {

        String kaizenId = kaizen.getKaizenid();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Kaizen Final").child(uid).child(kaizenId);

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_window, null);



        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = 1200;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(true);

        activity = this;

        //Initialize the elements of our window, install the handler

        TextView machineName = popupView.findViewById(R.id.machine_name_display);
        if (kaizen.getMachine_name().length() != 0)
            machineName.setText(kaizen.getMachine_name());
        else {
            machineName.setText("Analysis");
            machineName.setTypeface(machineName.getTypeface(), Typeface.ITALIC);
        }

        TextView idea = popupView.findViewById(R.id.idea_of_kaizen_display);
        if (kaizen.getIdea_of_kaizen().length() != 0)
            idea.setText(kaizen.getIdea_of_kaizen());
        else {
            idea.setText("Idea");
            idea.setTypeface(idea.getTypeface(), Typeface.ITALIC);
        }

        TextView problem = popupView.findViewById(R.id.problem_display);
        if (kaizen.getProblem().length() != 0)
            problem.setText(kaizen.getProblem());
        else {
            problem.setText("Problem");
            problem.setTypeface(problem.getTypeface(), Typeface.ITALIC);
        }

        TextView pillar = popupView.findViewById(R.id.pillar);
        if (kaizen.getPillar().length() != 0)
            pillar.setText(kaizen.getPillar());
        else {
            pillar.setText("Pillar");
            pillar.setTypeface(pillar.getTypeface(), Typeface.ITALIC);
        }

        TextView category = popupView.findViewById(R.id.category_display);
        if (kaizen.getCategory().toString().length() != 0)
            category.setText(kaizen.getCategory().toString());
        else {
            category.setText("Category");
            category.setTypeface(category.getTypeface(), Typeface.ITALIC);
        }

        TextView result = popupView.findViewById(R.id.result_display);
        if (kaizen.getResults().length() != 0)
            result.setText(kaizen.getResults());
        else {
            result.setText("Results");
            result.setTypeface(result.getTypeface(), Typeface.ITALIC);
        }


        TextView teamMembers = popupView.findViewById(R.id.team_members_display);
        List<String> teamMemberList = kaizen.getName();
        if (kaizen.getName().size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (String details : teamMemberList) {
                builder.append(details + "\n");
            }
            teamMembers.setText(builder.toString());
        } else {
            teamMembers.setText("Team members:");
            teamMembers.setTypeface(teamMembers.getTypeface(), Typeface.ITALIC);
        }

        if (kaizen.getBeforeUriPath() != null && kaizen.getBeforeUriPath().length() != 0) {
            ImageView before = popupView.findViewById(R.id.image_before);
            //String link = "https://firebasestorage.googleapis.com/v0/b/kaizen-89784.appspot.com/o/images%2FEyadrxb0JZbl3EBHEcYYLYOb6bG3.1627749846416.jpg?alt=media&token=e5733fd6-85ca-491e-b0cf-b5d3edd061aa";
            Log.d("TestURIS", "showPopupWindow: "+kaizen.getBeforeUriPath());
            Glide.with(popupView.getContext()).load(kaizen.getBeforeUriPath()).into(before);

        }

        if (kaizen.getAfterUriPath() != null && kaizen.getAfterUriPath().length() != 0) {
            ImageView after = popupView.findViewById(R.id.image_after);
            //String link = "https://firebasestorage.googleapis.com/v0/b/kaizen-89784.appspot.com/o/images%2FEyadrxb0JZbl3EBHEcYYLYOb6bG3.1627749823758.jpg?alt=media&token=926abd9d-3b5d-4df5-b22e-b587e1fc6f52";
            Log.d("TestURIS", "showPopupWindow: "+kaizen.getAfterUriPath());
            Glide.with(popupView.getContext()).load(kaizen.getAfterUriPath()).into(after);
        }

        Button buttonEdit = popupView.findViewById(R.id.messageButton);

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
}