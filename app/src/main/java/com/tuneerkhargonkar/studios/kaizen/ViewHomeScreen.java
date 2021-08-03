package com.tuneerkhargonkar.studios.kaizen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewHomeScreen extends AppCompatActivity implements PlaceInfoListener{


    public static int flag = 0;
    private boolean checkFunction = true;
    private Button btn_showAll;
    private ProgressBar progressbar2;
    private boolean doubleBackToExitPressedOnce = false;
    private RecyclerView recyclerView;
    private List<Kaizen> functionPassedList = new ArrayList<>();
    KaizenViewAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the
    // Firebase Realtime Database
    List<Kaizen> mKaizenEntries = new ArrayList<>();
    List<Kaizen> mKaizenEntriesComplete = new ArrayList<>();
    List<Kaizen> displayedList = new ArrayList<>();
    List<Kaizen> mKiazenMaintenence = new ArrayList<>();
    List<Kaizen> mKaizenRandD = new ArrayList<>();
    List<Kaizen> mKaizenME = new ArrayList<>();
    List<Kaizen> mKaizenHR = new ArrayList<>();
    ImageView backgroundImage;
    Context mContext;
    PlaceInfoListener infoListener;





    DatabaseReference completeDisplay;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference uRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("department");
    DatabaseReference deptRef = FirebaseDatabase.getInstance().getReference().child("Kaizen Final");

    String dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_homescreen);
        progressbar2 = findViewById(R.id.progressBar2);
        completeDisplay = FirebaseDatabase.getInstance().getReference().child("Kaizen Final");
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backgroundImage = findViewById(R.id.backgroundImage);
        backgroundImage.setVisibility(View.GONE);
        mContext = this;

        displayedList.clear();
        updateList(mKaizenEntries);





        Log.d("checkDept", "onCreate: "+dept);
        showMine();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(view,displayedList.get(position));

            }
        }));

        // Function calls
        BottomNavigationMenu();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void showAll() {
        Toast.makeText(getApplicationContext(),"show all",Toast.LENGTH_LONG);
        completeDisplay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressbar2.setVisibility(View.VISIBLE);
                for( DataSnapshot dept : snapshot.getChildren()){
                    for (DataSnapshot uid : dept.getChildren()) {
                        for (DataSnapshot ds : uid.getChildren()) {
                            Log.d("checkView", "onDataChange: "+ds.toString());
                            Kaizen note = ds.getValue(Kaizen.class);
                            mKaizenEntriesComplete.add(note);
                        }

                        if(mKaizenEntriesComplete.size()==0)
                        {
                            progressbar2.setVisibility(View.GONE);
                            backgroundImage.setVisibility(View.VISIBLE);
                            KaizenViewAdapter adapter = new KaizenViewAdapter(mContext, displayedList,infoListener);
                            adapter.notifyDataSetChanged();
                            Snackbar.make(findViewById(android.R.id.content), "No data available", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                        else
                        {
                            KaizenViewAdapter adapter = new KaizenViewAdapter(mContext, mKaizenEntriesComplete,infoListener);
                            recyclerView.setAdapter(adapter);
                            progressbar2.setVisibility(View.GONE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void updateList(List<Kaizen> list){
        displayedList = list;
    }



    private void showMine() {

        uRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                dept = dataSnapshot.getValue(String.class);
                Log.d("checkDept", "onCreate: 1 :"+dept);
                mbase = FirebaseDatabase.getInstance().getReference().child("Kaizen Final").child(dept).child(uid);

                mbase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressbar2.setVisibility(View.VISIBLE);
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Kaizen note = ds.getValue(Kaizen.class);
                            mKaizenEntries.add(note);
                        }
                        if(mKaizenEntries.size()==0)
                        {
                            progressbar2.setVisibility(View.GONE);
                            displayedList.clear();
                            backgroundImage.setVisibility(View.VISIBLE);
                            Snackbar.make(findViewById(android.R.id.content), "No data available", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                        else
                        {
                            KaizenViewAdapter adapter = new KaizenViewAdapter(mContext, mKaizenEntries,infoListener);
                            recyclerView.setAdapter(adapter);
                            progressbar2.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    private void showMaintenance() {

        progressbar2.setVisibility(View.VISIBLE);

        Log.d("checkMaintenanceCall", "showMaintenance: Function called");

        deptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot department : snapshot.getChildren())
                {
                    if(department.getKey().trim().equals("Maintenance"))
                    {
                        Log.d("checkMaintenanceCall", "showMaintenance: into department");
                        for(DataSnapshot uid: department.getChildren())
                        {
                            Log.d("checkMaintenanceCall", "showMaintenance: uid = " + uid.toString());
                            for (DataSnapshot kid : uid.getChildren()) {
                                Log.d("checkMaintenanceCall", "showMaintenance: KID = " + kid.getKey());
                                Kaizen note = kid.getValue(Kaizen.class);
                                mKiazenMaintenence.add(note);
                            }
                            if(mKiazenMaintenence.size()==0)
                            {
                                progressbar2.setVisibility(View.GONE);
                                displayedList.clear();
                                backgroundImage.setVisibility(View.VISIBLE);
                                Snackbar.make(findViewById(android.R.id.content), "No data available", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                            else
                            {
                                KaizenViewAdapter adapter = new KaizenViewAdapter(mContext, mKiazenMaintenence,infoListener);
                                recyclerView.setAdapter(adapter);
                                progressbar2.setVisibility(View.GONE);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showME() {

        deptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot department : snapshot.getChildren())
                {
                    if(department.getKey().equals("ME"))
                    {
                        for(DataSnapshot uid: department.getChildren())
                        {
                            for (DataSnapshot kid : uid.getChildren()) {
                                Kaizen note = kid.getValue(Kaizen.class);
                                mKaizenME.add(note);
                            }
                            if(mKaizenME.size()==0)
                            {
                                progressbar2.setVisibility(View.GONE);
                                displayedList.clear();
                                backgroundImage.setVisibility(View.VISIBLE);
                                Snackbar.make(findViewById(android.R.id.content), "No data available", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                            else
                            {
                                KaizenViewAdapter adapter = new KaizenViewAdapter(mContext, mKaizenME,infoListener);
                                recyclerView.setAdapter(adapter);
                                progressbar2.setVisibility(View.GONE);
                            }
                        }

                    }
                    else{
                        progressbar2.setVisibility(View.GONE);
                        displayedList.clear();
                        backgroundImage.setVisibility(View.VISIBLE);
                        Snackbar.make(findViewById(android.R.id.content), "No data available", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showHR() {

        progressbar2.setVisibility(View.VISIBLE);

        Log.d("checkMaintenanceCall", "showMaintenance: Function called");

        deptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot department : snapshot.getChildren())
                {
                    if(department.getKey().trim().equals("HR"))
                    {
                        Log.d("checkMaintenanceCall", "showMaintenance: into department");
                        for(DataSnapshot uid: department.getChildren())
                        {
                            Log.d("checkMaintenanceCall", "showMaintenance: uid = " + uid.toString());
                            for (DataSnapshot kid : uid.getChildren()) {
                                Log.d("checkMaintenanceCall", "showMaintenance: KID = " + kid.getKey());
                                Kaizen note = kid.getValue(Kaizen.class);
                                mKaizenHR.add(note);
                            }
                            if(mKaizenHR.size()==0)
                            {
                                progressbar2.setVisibility(View.GONE);
                                displayedList.clear();
                                backgroundImage.setVisibility(View.VISIBLE);
                                Snackbar.make(findViewById(android.R.id.content), "No data available", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                            else
                            {
                                KaizenViewAdapter adapter = new KaizenViewAdapter(mContext, mKaizenHR,infoListener);
                                recyclerView.setAdapter(adapter);
                                progressbar2.setVisibility(View.GONE);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showRandD() {

        progressbar2.setVisibility(View.VISIBLE);

        Log.d("checkMaintenanceCall", "showMaintenance: Function called");

        deptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot department : snapshot.getChildren())
                {
                    if(department.getKey().trim().equals("HR"))
                    {
                        Log.d("checkMaintenanceCall", "showMaintenance: into department");
                        for(DataSnapshot uid: department.getChildren())
                        {
                            Log.d("checkMaintenanceCall", "showMaintenance: uid = " + uid.toString());
                            for (DataSnapshot kid : uid.getChildren()) {
                                Log.d("checkMaintenanceCall", "showMaintenance: KID = " + kid.getKey());
                                Kaizen note = kid.getValue(Kaizen.class);
                                mKaizenRandD.add(note);
                            }
                            if(mKaizenRandD.size()==0)
                            {
                                progressbar2.setVisibility(View.GONE);
                                displayedList.clear();
                                backgroundImage.setVisibility(View.VISIBLE);
                                Snackbar.make(findViewById(android.R.id.content), "No data available", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                            else
                            {
                                KaizenViewAdapter adapter = new KaizenViewAdapter(mContext, mKaizenRandD,infoListener);
                                recyclerView.setAdapter(adapter);
                                progressbar2.setVisibility(View.GONE);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();

            }
        });
    }




    public void onPopupButtonClick(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.getMenu().getItem(0).setChecked(true);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.show_yours:
                        adapter = new KaizenViewAdapter(mContext, mKaizenEntries,infoListener);
                        recyclerView.setAdapter(adapter);
                        backgroundImage.setVisibility(View.GONE);
                        showMine();
                        displayedList.clear();
                        updateList(mKaizenEntries);
                        return false;

                    case R.id.show_all:
                        adapter = new KaizenViewAdapter(mContext, mKaizenEntriesComplete,infoListener);
                        recyclerView.setAdapter(adapter);
                        backgroundImage.setVisibility(View.GONE);
                        showAll();
                        displayedList.clear();
                        updateList(mKaizenEntriesComplete);
                        return false;

                    case R.id.show_maint:
                        adapter = new KaizenViewAdapter(mContext, mKiazenMaintenence,infoListener);
                        recyclerView.setAdapter(adapter);
                        backgroundImage.setVisibility(View.GONE);
                        showMaintenance();
                        displayedList.clear();
                        updateList(mKiazenMaintenence);
                        return false;

                    case R.id.show_RnD:
                        adapter = new KaizenViewAdapter(mContext, mKaizenRandD,infoListener);
                        recyclerView.setAdapter(adapter);
                        backgroundImage.setVisibility(View.GONE);
                        showRandD();
                        displayedList.clear();
                        updateList(mKaizenRandD);
                        return false;

                    case R.id.show_ME:
                        adapter = new KaizenViewAdapter(mContext, mKaizenME,infoListener);
                        recyclerView.setAdapter(adapter);
                        showME();
                        displayedList.clear();
                        updateList(mKaizenME);
                        return false;

                    case R.id.show_hr:
                        adapter = new KaizenViewAdapter(mContext, mKaizenHR,infoListener);
                        recyclerView.setAdapter(adapter);
                        showHR();
                        displayedList.clear();
                        updateList(mKaizenHR);
                        return false;
                }
                return false;

            }
        });
        popup.show();
    }

    private void BottomNavigationMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.kaizenCreate: {
                        startActivity(new Intent(getApplicationContext(), SendHomeScreen.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onPopupMenuClick(View view, int pos) {

    }
}