package com.tuneerkhargonkar.studios.kaizen;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

public class PopUpClassProfileView {

    TextView machineName;
    final Handler handler = new Handler();
    FirebaseStorage storageRef;
    String finalKaizenID;
    Context context;
    ProfileView profileView = new ProfileView();
    List<Kaizen> temp_approval_list = new ArrayList<>();
    List<Kaizen> temp_checking_list = new ArrayList<>();

    //PopupWindow display method


    public void showPopupWindow(View view, Kaizen kaizen, KaizenApprovalViewAdapter adapter, String s, RecyclerView recyclerView) {

        storageRef = FirebaseStorage.getInstance();
        ViewHomeScreen viewHomeScreen = new ViewHomeScreen();

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_window_profile_view, null);


        DatabaseReference kfIDDatabaseReference = FirebaseDatabase.getInstance().getReference().child("KaizenID");
        kfIDDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                finalKaizenID = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = 1200;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        Button changeCondition = popupView.findViewById(R.id.changeCondition);
        if (s == "1")
            changeCondition.setText("Approve");

        if (s == "2")
            changeCondition.setText("Checked");

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
            Log.d("TestURIS", "showPopupWindow: " + kaizen.getBeforeUriPath());
            Glide.with(popupView.getContext()).load(kaizen.getBeforeUriPath()).into(before);

        }

        if (kaizen.getAfterUriPath() != null && kaizen.getAfterUriPath().length() != 0) {
            ImageView after = popupView.findViewById(R.id.image_after);
            //String link = "https://firebasestorage.googleapis.com/v0/b/kaizen-89784.appspot.com/o/images%2FEyadrxb0JZbl3EBHEcYYLYOb6bG3.1627749823758.jpg?alt=media&token=926abd9d-3b5d-4df5-b22e-b587e1fc6f52";
            Log.d("TestURIS", "showPopupWindow: " + kaizen.getAfterUriPath());
            Glide.with(popupView.getContext()).load(kaizen.getAfterUriPath()).into(after);
        }

        loadCondition(popupView, kaizen);


        Button exit = popupView.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
//                        final Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.notifyDataSetChanged();
//                            }
//                        }, 1000);

            }
        });

        Button buttonEdit = popupView.findViewById(R.id.changeCondition);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (s.trim().equals("2")) {
                    kaizen.setCHECKEDBY_boolean(true);
                    Log.d("popUp", "the changed value is:" + Boolean.toString(kaizen.isCHECKEDBY_boolean()));

                    String kaizenId = kaizen.getKaizenid();

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Kaizen").child(kaizen.getDepartment()).child(uid).child(kaizenId);
                    mDatabaseReference.setValue(kaizen);

                    String made_by_empid, helped_by_empid;

                    made_by_empid = kaizen.getMadeby_string();
                    helped_by_empid = kaizen.getHelpedby_string();

                    Log.d("recyclerview", "onDataChange: 1 = " + adapter.getItemCount());

                    DatabaseReference tDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Checking");
                    tDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot receivingUID : snapshot.getChildren()) {
                                for (DataSnapshot randomKey : receivingUID.getChildren()) {
                                    for (DataSnapshot senderUID : randomKey.getChildren()) {
                                        if (senderUID.child("kaizenid").getValue(String.class).equals(kaizenId)) {
                                            DatabaseReference ts1 = randomKey.getRef();
                                            Log.d("recyclerview", "onDataChange: " + ts1.toString());
                                            ts1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    sendForApproval(senderUID.child("approvedby_string").getValue(String.class), kaizen, kaizen.getUid());
                                                    Log.d("recyclerview", "onDataChange: 2 = " + adapter.getItemCount());
                                                    DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Checking").child(receivingUID.getKey());
                                                    tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot randomids : snapshot.getChildren())
                                                            {
                                                                for(DataSnapshot kaizens : randomids.getChildren())
                                                                {
                                                                    Kaizen test = kaizens.getValue(Kaizen.class);
                                                                    temp_checking_list.add(test);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                }
                                            });
                                            Log.d("recyclerview", "onDataChange: 3 = " + adapter.getItemCount());
                                            break;
                                        }
                                    }
                                }
                            }
                            loadCondition(popupView, kaizen);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                if (s.trim().equals("1")) {
                    kaizen.setAPPROVEDBY_boolean(true);
                    String kaizenId = kaizen.getKaizenid();

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Kaizen").child(kaizen.getDepartment()).child(uid).child(kaizenId);
                    mDatabaseReference.setValue(kaizen);

                    DatabaseReference kfIDDatabaseReference = FirebaseDatabase.getInstance().getReference().child("KaizenID");

                    DatabaseReference kfDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Kaizen Final").child((kaizen.getDepartment())).child(uid).child(finalKaizenID);

                    DatabaseReference uDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    DatabaseReference pDatabaseReference = FirebaseDatabase.getInstance().getReference().child("UsersPoints");

                    DatabaseReference tDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Approvals");
                    tDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot receivingUID : snapshot.getChildren()) {
                                for (DataSnapshot randomKey : receivingUID.getChildren()) {
                                    for (DataSnapshot senderUID : randomKey.getChildren()) {
                                        if (senderUID.child("kaizenid").getValue(String.class).equals(kaizenId))
                                        {


                                            if (kaizen.getHelpedby_string().length() == 0) {
                                                uDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot uid : snapshot.getChildren()) {
                                                            if (uid.child("empID").getValue(String.class).equals(kaizen.madeby_string)) {

                                                                String k4 = uid.child("uid").getValue(String.class);

                                                                pDatabaseReference.child(k4).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                        String s8 = snapshot.getValue(String.class);
                                                                        s8 = Integer.toString(Integer.parseInt(s8)+100);
                                                                        pDatabaseReference.child(k4).setValue(s8);

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                            } else {
                                                                Log.d("checkPoints", "onDataChange: FAIL");
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                            Log.d("checkApproval", "onDataChange: ");
                                            DatabaseReference ts1 = senderUID.getRef();
                                            ts1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    sendForApproval(senderUID.child("approvedby_string").getValue(String.class), kaizen, kaizen.getUid());
                                                    Log.d("recyclerview", "onDataChange: 2 = " + adapter.getItemCount());
                                                    DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Approval").child(receivingUID.getKey());
                                                    tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot randomids : snapshot.getChildren())
                                                            {
                                                                for(DataSnapshot kaizens : randomids.getChildren())
                                                                {
                                                                    Kaizen test = kaizens.getValue(Kaizen.class);
                                                                    temp_approval_list.add(test);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    KaizenApprovalViewAdapter adapter_temp = new KaizenApprovalViewAdapter(popupView.getContext(),temp_approval_list);
                                                    recyclerView.setAdapter(adapter_temp);
                                                }
                                            });
                                            kaizen.setKaizenid(finalKaizenID);
                                            kfDatabaseReference.setValue(kaizen);
                                            finalKaizenID = Integer.toString(Integer.parseInt(finalKaizenID) + 1);
                                            mDatabaseReference.removeValue();
                                            kfIDDatabaseReference.setValue(finalKaizenID);
                                            adapter.notifyDataSetChanged();
                                            recyclerView.setAdapter(adapter);
                                        }
                                        break;
                                    }
                                }
                            }

                            loadCondition(popupView, kaizen);
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }


    private void sendForApproval(String empID, Kaizen k, String uid_currentSender) {
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference fRef = FirebaseDatabase.getInstance().getReference().child("Approvals");
        mref.addValueEventListener(new ValueEventListener() {
            String uid_Reciever;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("empID").getValue().equals(empID)) {
                        User user_reciever = ds.getValue(User.class);
                        uid_Reciever = user_reciever.getUid();
                        if (uid_Reciever != null) {
                            HashMap<String, Kaizen> temp2 = new HashMap<>();
                            temp2.put(uid_currentSender, k);
                            fRef.child(uid_Reciever).push().setValue(temp2);
                            for (Map.Entry<String, Kaizen> entry : temp2.entrySet())
                                System.out.println("Key = " + entry.getKey() +
                                        ", Value = " + entry.getValue());
                        } else {
                            Log.d("check", "user_reciever not recieved");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void loadCondition(View v, Kaizen kaizen) {

        ImageView madeBy, checkedBy, approvedBy;
        madeBy = v.findViewById(R.id.madeByIndicator_Approved);
        checkedBy = v.findViewById(R.id.CheckdByIndicator_Approved);
        approvedBy = v.findViewById(R.id.ApprovedByIndicator_Approved);

        boolean madeBy_test_boolean = kaizen.isMADEBY_boolean();
        boolean approvedBy_test_boolean = kaizen.isAPPROVEDBY_boolean();
        boolean checkedBy_test_boolean = kaizen.isCHECKEDBY_boolean();


        if (madeBy_test_boolean == true) {
            madeBy.setImageResource(R.drawable.res_circle_green);
        }

        if (approvedBy_test_boolean == true) {
            approvedBy.setImageResource(R.drawable.res_circle_green);
        }

        if (checkedBy_test_boolean == true) {
            checkedBy.setImageResource(R.drawable.res_circle_green);
        }
    }


}