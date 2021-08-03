package com.tuneerkhargonkar.studios.kaizen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileView extends AppCompatActivity {

    ImageView profilepic, backPressed, approval_list, checking_list,get_assignments;
    TextView name, email, eID, heading, points;
    RecyclerView recyclerview;
    Button bt_logout;
    Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    DatabaseReference databaseReference;
    StorageReference storageReference = firebaseStorage.getReference();
    List<Kaizen> kaizen_approvedList = new ArrayList<>();
    List<Kaizen> kaizen_CheckingList = new ArrayList<>();
    List<Kaizen> displayList = new ArrayList<>();
    List<Kaizen> kaizen_helperList = new ArrayList<>();
    KaizenApprovalViewAdapter adapter;
    String s;
    DatabaseReference changeApproval = FirebaseDatabase.getInstance().getReference().child("Approvals");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        ConstraintLayout cLayout = findViewById(R.id.constrain_layout);
        context = getApplicationContext();

        get_assignments=findViewById(R.id.get_assignments);
        get_assignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHelperList();
            }
        });

        adapter = new KaizenApprovalViewAdapter(getApplicationContext(),displayList);
        heading = findViewById(R.id.headingOfRecyclerview);
        checking_list = findViewById(R.id.checkedList);

        checking_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayList != kaizen_CheckingList)
                    loadCheckedList();

            }
        });

        approval_list = findViewById(R.id.approved_List);

        approval_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayList != kaizen_approvedList)
                    loadApprovedList();

            }
        });

        changeApproval.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadApprovedList();
                Log.d("recyclerview", "onDataChange: listener activated");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerview = findViewById(R.id.recyclerView_profile_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference pRef = FirebaseDatabase.getInstance().getReference().child("UsersPoints").child(uid);
        pRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                points.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = firebaseDatabase.getReference().child("Users").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);
                name.setText(userProfile.getName());
                email.setText(userProfile.getEmail());
                eID.setText(userProfile.getEmpID());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileView.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        InitializingUI();

        if(displayList==kaizen_approvedList)
        {
            adapter = new KaizenApprovalViewAdapter(context,kaizen_approvedList);
            recyclerview.setAdapter(adapter);
        }

        if(displayList==kaizen_CheckingList)
        {
            adapter = new KaizenApprovalViewAdapter(context,kaizen_CheckingList);
            recyclerview.setAdapter(adapter);
        }


        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                if (displayList == kaizen_approvedList) {
                    s = "1";
                    adapter = new KaizenApprovalViewAdapter(getApplicationContext(),kaizen_approvedList);
                    PopUpClassProfileView popUpClassProfileView = new PopUpClassProfileView();
                    popUpClassProfileView.showPopupWindow(view, displayList.get(position), adapter, s, recyclerview);
                }
                if (displayList == kaizen_CheckingList) {
                    s = "2";
                    adapter = new KaizenApprovalViewAdapter(getApplicationContext(),kaizen_CheckingList);
                    PopUpClassProfileView popUpClassProfileView = new PopUpClassProfileView();
                    popUpClassProfileView.showPopupWindow(view, displayList.get(position), adapter, s, recyclerview);
                }

                if (displayList == kaizen_helperList) {
                    s = "3";
                    adapter = new KaizenApprovalViewAdapter(getApplicationContext(),kaizen_helperList);
                    PopUpClassProfileView popUpClassProfileView = new PopUpClassProfileView();
                    popUpClassProfileView.showPopupWindow(view, displayList.get(position), adapter, s, recyclerview);
                }
            }
        }));

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signout();
            }
        });

        Glide.with(context)
                .load(R.drawable.kngine)
                .transform(new CircleTransform(context))
                .into(profilepic);

        backPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(ProfileView.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(ProfileView.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                displayList.remove(position);
                adapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);

    }


    private void Signout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileView.this, UserSignIn.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void InitializingUI() {
        profilepic = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        eID = findViewById(R.id.profile_employee_id);
        bt_logout = findViewById(R.id.button_logout);
        backPressed = findViewById(R.id.back_pressed);
        points = findViewById(R.id.tv_points);

    }

    public void loadApprovedList() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference approvedRef = FirebaseDatabase.getInstance().getReference().child("Approvals").child(uid);
        approvedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot randomKey : snapshot.getChildren()) {
                    for (DataSnapshot ds : randomKey.getChildren()) {
                        Kaizen recieved_kaizen = ds.getValue(Kaizen.class);
                        kaizen_approvedList.add(recieved_kaizen);
                        Log.d("recyclerview", "onDataChange: kaizen added");
                    }
                }
                displayList.clear();
                displayList = kaizen_approvedList;

                Log.d("check", Integer.toString(kaizen_approvedList.size()));

                if (kaizen_approvedList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                }
                adapter = new KaizenApprovalViewAdapter(getApplicationContext(), kaizen_approvedList);
                recyclerview.setAdapter(adapter);
                heading.setText("To be approved list");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadCheckedList() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference approvedRef = FirebaseDatabase.getInstance().getReference().child("Checking").child(uid);

        approvedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kaizen_CheckingList.clear();
                for (DataSnapshot randomKey : snapshot.getChildren()) {
                    for (DataSnapshot ds : randomKey.getChildren()) {
                        Kaizen recieved_kaizen = ds.getValue(Kaizen.class);
                        Log.d("profileView", "hello there");
                        Log.d("profileView", Boolean.toString(recieved_kaizen.isMADEBY_boolean()));
                        Log.d("profileView", "hello there");
                        Log.d("profileView", Boolean.toString(recieved_kaizen.isCHECKEDBY_boolean()));
                        Log.d("profileView", "hello there");
                        Log.d("profileView", Boolean.toString(recieved_kaizen.isAPPROVEDBY_boolean()));
                        String sender_uid = ds.child("uid").getValue(String.class);
                        kaizen_CheckingList.add(recieved_kaizen);
                    }
                }
                displayList.clear();
                displayList = kaizen_CheckingList;

                Log.d("check", Integer.toString(kaizen_CheckingList.size()));

                if (kaizen_CheckingList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                }
                adapter = new KaizenApprovalViewAdapter(getApplicationContext(), displayList);
                recyclerview.setAdapter(adapter);
                heading.setText("To be checked list");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadHelperList() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference approvedRef = FirebaseDatabase.getInstance().getReference().child("Assistance").child(uid);

        approvedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kaizen_helperList.clear();
                for (DataSnapshot randomKey : snapshot.getChildren()) {
                    for (DataSnapshot ds : randomKey.getChildren()) {
                        Kaizen recieved_kaizen = ds.getValue(Kaizen.class);
                        String sender_uid = ds.child("uid").getValue(String.class);
                        kaizen_helperList.add(recieved_kaizen);
                    }
                }
                displayList.clear();
                displayList = kaizen_helperList;

                if (kaizen_helperList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                }
                adapter = new KaizenApprovalViewAdapter(getApplicationContext(), displayList);
                recyclerview.setAdapter(adapter);
                heading.setText("Asked for assistance");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}