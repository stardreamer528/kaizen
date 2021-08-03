package com.tuneerkhargonkar.studios.kaizen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileReader;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class editkaizen extends AppCompatActivity {

    ImageView before,after,back;
    TextView idea,problem;
    Uri afterURI;
    String temp_URL_String,afterImageURL,UID_Kaizen,KID_Kaizen,DEPT_kaizen;
    Button send;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(editkaizen.this,ViewHomeScreen.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        editkaizen.this.startActivity(intent);
        overridePendingTransition(0,10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editkaizen);
        InitializeUI();

        Intent intent = getIntent();

        Kaizen kaizen = (Kaizen) intent.getSerializableExtra("sampleObject");
        UID_Kaizen = kaizen.getUid();
        KID_Kaizen = kaizen.getKaizenid();
        DEPT_kaizen = kaizen.getDepartment();


        Glide.with(this).load(kaizen.getAfterUriPath()).into(after);
        Glide.with(this).load(kaizen.getBeforeUriPath()).into(before);
        idea.setText(kaizen.getIdea_of_kaizen());
        problem.setText(kaizen.getProblem());



        after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentAfter();
            }
        });


        DatabaseReference aRef = FirebaseDatabase.getInstance().getReference().child("Kaizen Final").child(DEPT_kaizen).child(UID_Kaizen).child(KID_Kaizen);

        aRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kaizen temp_kaizen = new Kaizen();
                temp_kaizen = snapshot.getValue(Kaizen.class);
                String s = temp_kaizen.getKaizenid();
                Log.d("test", "onDataChange: "+s);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




    }

    public void getContentAfter() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            afterURI = data.getData();
            after.setImageURI(afterURI);
            afterImageURL = uploadPicture(afterURI);
            Log.d("test", "onActivityResult: "+"pushed to Fb");
        }
    }

    private String uploadPicture(Uri uri) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        //final String uuid = UUID.randomUUID().toString();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("images/"+ uid + "." + System.currentTimeMillis() + "." + getFileExtension(uri));

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Get a URL to the uploaded content
                                pd.dismiss();
                                temp_URL_String = uri.toString();
                                Log.d("checkURL", "onSuccess: beforeURL:"+temp_URL_String);
                                Log.d("checkURL", "onSuccess: beforeURL:"+uri.toString());
                                DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Kaizen Final").child(DEPT_kaizen).child(UID_Kaizen).child(KID_Kaizen).child("afterUriPath");
                                tempRef.setValue(temp_URL_String);
                                Snackbar.make(findViewById(android.R.id.content), "Image uploaded successfully", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress : " + (int) progressPercentage + "%");
            }
        });
        return temp_URL_String;
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void InitializeUI(){
        before = findViewById(R.id.im_before);
        after = findViewById(R.id.im_after);
        idea = findViewById(R.id.ek_idea);
        problem = findViewById(R.id.ek_problem);
        send = findViewById(R.id.sendEdit);
        back = findViewById(R.id.back);
    }

}