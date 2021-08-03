package com.tuneerkhargonkar.studios.kaizen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SendHomeScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;

    Spinner spinner_pillar, spinner_category;
    EditText edittext_startDate, editText_endDate, approvedBy, checkedBy, madeBy, helpedBY, kaizenNumber;
    Calendar myCalendar;
    Button button, bt_addPeople, submit, refresh;
    Context context;
    int NumberofLines = 3;
    ImageView beforePic, afterPic;
    Uri beforeUri, afterUri;
    FirebaseStorage storage;
    StorageReference sReference;
    BottomNavigationView bottomNavigationView;
    String madeByEmpID;
    String beforeImageURL, afterImageURL, uid;
    String temp_URL_String, s_dept;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef, kRef;
    Boolean already_available;

    // Defining views
    EditText enter_machine_name, enter_idea_of_kaizen, enter_analysis, enter_results, enter_counter_measures, enter_problem;
    EditText et_person1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_homescreen);
        setTheme(R.style.splashScreenTheme);
        edittext_startDate = (EditText) findViewById(R.id.edit_text_startDate);
        editText_endDate = (EditText) findViewById(R.id.edit_text_endDate);
        myCalendar = Calendar.getInstance();
        button = findViewById(R.id.bt_addPeople);
        context = this;
        et_person1 = findViewById(R.id.editTextPerson1);
        bt_addPeople = findViewById(R.id.bt_addPeople);
        submit = findViewById(R.id.bt_submit);
        refresh = findViewById(R.id.bt_refresh);
        beforePic = findViewById(R.id.iv_before);
        afterPic = findViewById(R.id.iv_after);
        storage = FirebaseStorage.getInstance();
        sReference = storage.getReference();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        approvedBy = findViewById(R.id.ApprovedBY);
        checkedBy = findViewById(R.id.CheckedBy);
        helpedBY = findViewById(R.id.helpedBy);
        kaizenNumber = findViewById(R.id.kaizen_number);
        ImageView profilePic = findViewById(R.id.profilePhoto);
        String UID = FirebaseAuth.getInstance().getUid();
        already_available = false;


        DatabaseReference tref = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        Log.d("checkDEPT", "onDataChange: before onDataChange");
        tref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                s_dept = snapshot.child("department").getValue(String.class);
                kRef = FirebaseDatabase.getInstance().getReference().child("Kaizen").child(s_dept).child(UID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfileActivity();
            }
        });

        MultiSelectionSpinner mySpinner;


        ArrayList<Item> items = new ArrayList<>();
        Item item1 = new Item("P", true);
        items.add(item1);
        Item item2 = new Item("Q", true);
        items.add(item2);
        Item item3 = new Item("C", true);
        items.add(item3);
        Item item4 = new Item("H", true);
        items.add(item4);
        Item item5 = new Item("D", true);
        items.add(item5);
        Item item6 = new Item("M", true);
        items.add(item6);

        mySpinner = (MultiSelectionSpinner) findViewById(R.id.spinner_category);
        mySpinner.setItem(items);

        // To get the selected Item list
        ArrayList<Item> selectedItems = mySpinner.getSelectedItems();

        // Function calls
        BottomNavigationMenu();

        //REFRENCES TO EDIT TEXTS
        enter_machine_name = findViewById(R.id.enter_machine_name);
        enter_idea_of_kaizen = findViewById(R.id.enter_idea_of_kaizen);
        enter_problem = findViewById(R.id.enter_problem);
        enter_analysis = findViewById(R.id.enter_analysis);
        enter_results = findViewById(R.id.enter_results);
        enter_counter_measures = findViewById(R.id.enter_counter_measures);


        refresh = findViewById(R.id.bt_refresh);
        refresh.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshForm();
            }
        }));


        beforePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentBefore();
            }
        });

        afterPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentAfter();
            }
        });
        bt_addPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Line();

            }
        });


        DatabaseReference mGetEmpId = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("empID");
        mGetEmpId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                madeByEmpID = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                Log.d("checkRepID", "onClick: entered onclick");
                Log.d("checkRepID", s_dept);
                Log.d("checkRepID", UID);
                Log.d("kaizencheck", "check 1");


                Log.d("checkRepID", Boolean.toString(already_available));

                if (kRef != null) {
                    if (kaizenNumber.getText().toString().trim().length() != 0) {
                        if (mySpinner.getSelectedItems().size() != 0) {
                            Log.d("checkRepID", "entered first if");
                            kRef.orderByChild("kaizenid").equalTo(kaizenNumber.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    Log.d("checkRepID", "entered dataSnapshot");
                                    if (snapshot.exists()) {
                                        Toast.makeText(getApplicationContext(), "Kaizen ID already exists", Toast.LENGTH_SHORT).show();
                                    }

                                    if (!snapshot.exists()) {
                                        Log.d("checkRepID", "entered main if");
                                        if (beforeUri != null && afterUri != null) {
                                            String st = edittext_startDate.getText().toString();
                                            String ed = editText_endDate.getText().toString();
                                            String pillar = spinner_pillar.getSelectedItem().toString();
                                            List<String> category = new ArrayList<>();
                                            for (int i = 0; i < mySpinner.getSelectedItems().size(); i++) {
                                                String category_name_temp;
                                                if (mySpinner.getSelectedItems().get(i).getValue() == true) {
                                                    category_name_temp = mySpinner.getSelectedItems().get(i).getName();
                                                    category.add(category_name_temp);
                                                }
                                            }
                                            String machine_name = enter_machine_name.getText().toString();
                                            String idea_of_kaizen = enter_idea_of_kaizen.getText().toString();
                                            String problem = enter_problem.getText().toString();
                                            String analysis = enter_analysis.getText().toString();
                                            String counter_measures = enter_counter_measures.getText().toString();
                                            String results = enter_results.getText().toString();
                                            String beforeUriPath = beforeUri.getPath();
                                            String afterUriPath = afterUri.getPath();
                                            String kID = kaizenNumber.getText().toString().trim();
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                                            List<String> name_1 = getNamesOfTeamMembers();
                                            Kaizen k1 = sendDataToFirebase(st, ed, pillar, category, machine_name, idea_of_kaizen, problem,
                                                    analysis, counter_measures, results, beforeUriPath, afterUriPath, name_1, uid, kID,
                                                    madeByEmpID, checkedBy.getText().toString(), approvedBy.getText().toString(), helpedBY.getText().toString(),currentDate);
                                            Log.d("URI", "onClick: BeforeURI:" + beforeUri.toString());
                                            Log.d("URI", "onClick: BeforeURIpath:" + beforeUri.getPath());

                                            if (checkedBy.getText().toString().length() != 0) {
                                                if (k1.isMADEBY_boolean() == true) {
                                                    sendForChecking(checkedBy.getText().toString().trim(), k1, uid);
                                                }
                                            }

                                            if (helpedBY.getText().toString().length() != 0) {
                                                sendForAssistance(helpedBY.getText().toString().trim(), k1, uid);
                                            }

                                            Toast.makeText(getApplicationContext(), "Submitted successfully!", Toast.LENGTH_SHORT).show();
                                            Log.d("kaizencheck", "submitted");
                                            refreshForm();
                                        } else {
                                            String st = edittext_startDate.getText().toString();
                                            String ed = editText_endDate.getText().toString();
                                            String pillar = spinner_pillar.getSelectedItem().toString();
                                            List<String> category = new ArrayList<>();
                                            for (int i = 0; i < mySpinner.getSelectedItems().size(); i++) {
                                                String category_name_temp;
                                                if (mySpinner.getSelectedItems().get(i).getValue() == true) {
                                                    category_name_temp = mySpinner.getSelectedItems().get(i).getName();
                                                    category.add(category_name_temp);
                                                }
                                            }

                                            String machine_name = enter_machine_name.getText().toString();
                                            String idea_of_kaizen = enter_idea_of_kaizen.getText().toString();
                                            String problem = enter_problem.getText().toString();
                                            String analysis = enter_analysis.getText().toString();
                                            String counter_measures = enter_counter_measures.getText().toString();
                                            String results = enter_results.getText().toString();
                                            String kID = kaizenNumber.getText().toString().trim();
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                                            List<String> name_1 = getNamesOfTeamMembers();
                                            Kaizen k1 = sendDataToFirebase(st, ed, pillar, category, machine_name, idea_of_kaizen, problem,
                                                    analysis, counter_measures, results, null, null, name_1, uid, kID,
                                                    madeByEmpID, checkedBy.getText().toString(), approvedBy.getText().toString(), helpedBY.getText().toString(),currentDate);
                                            Log.d("check", Boolean.toString(k1.isMADEBY_boolean()));

                                            if (checkedBy.getText().toString().length() != 0) {
                                                if (k1.isMADEBY_boolean() == true) {
                                                    sendForChecking(checkedBy.getText().toString().trim(), k1, uid);
                                                }
                                            }

                                            if (helpedBY.getText().toString().length() != 0) {
                                                sendForAssistance(helpedBY.getText().toString().trim(), k1, uid);
                                            }
                                            Toast.makeText(getApplicationContext(), "Submitted successfully!", Toast.LENGTH_SHORT).show();
                                            Log.d("kaizencheck", "submitted");
                                            refreshForm();
                                        }
                                        hideKeyboard();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Please select category",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Enter temporary kaizen ID", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        spinner_pillar = (Spinner) findViewById(R.id.spinner_select_pillar);
        spinner_pillar.setOnItemSelectedListener(this);
        List<String> category_pillar = new ArrayList<String>();
        category_pillar.add("KK");
        category_pillar.add("JH");
        category_pillar.add("PM");
        category_pillar.add("ET");
        category_pillar.add("SHE");
        category_pillar.add("QM");
        category_pillar.add("EM");
        category_pillar.add("OTPM");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category_pillar);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_pillar.setAdapter(dataAdapter);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel_startDate();
            }

        };

        edittext_startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SendHomeScreen.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        DatePickerDialog.OnDateSetListener date_end = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel_endDate();
            }

        };

        editText_endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SendHomeScreen.this, date_end, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        Glide.with(context)
                .load(R.drawable.kngine)
                .transform(new CircleTransform(context))
                .into(profilePic);

    }

    private void updateLabel_startDate() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext_startDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel_endDate() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText_endDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void Add_Line() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout_enter_person_name);
        // add edittext
        EditText et = new EditText(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, convertPixelsToDp(80, this), 0, 0);
        et.setLayoutParams(p);
        et.setHint("Name");
        et.setId(NumberofLines + 1);
        et.requestFocus();
        ll.addView(et);
        NumberofLines++;
    }

    public void getContentBefore() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void getContentAfter() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            beforeUri = data.getData();
            beforePic.setImageURI(beforeUri);
            uploadPicture(beforeUri, 1);
            Log.d("testURL", "onActivityResult: Before URL is " + beforeImageURL);
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            afterUri = data.getData();
            afterPic.setImageURI(afterUri);
            uploadPicture(afterUri, 2);
            Log.d("testURL", "onActivityResult: After URL is " + afterImageURL);
        }
    }

    private void uploadPicture(Uri uri, int id) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        //final String uuid = UUID.randomUUID().toString();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference riversRef = sReference.child("images/" + uid + "." + System.currentTimeMillis() + "." + getFileExtension(uri));

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
                                if (id == 1) {
                                    beforeImageURL = temp_URL_String;
                                    Log.d("checkURL", "onSuccess: beforeURL:" + temp_URL_String);
                                }
                                if (id == 2) {
                                    afterImageURL = temp_URL_String;
                                    Log.d("checkURL", "onSuccess: afterURL:" + temp_URL_String);
                                }
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

    public Kaizen sendDataToFirebase(String st, String ed, String piller, List<String> category, String machine_name, String idea_of_kaizen,
                                     String problem, String analysis, String counter_measures, String results, String beforeUriPath,
                                     String afterUriPath, List<String> name, String UID, String kID, String madeby1, String checkedby, String approvedby, String helpedBy, String currentDate) {
        Kaizen k;
        if (beforeUriPath != null && afterUriPath != null) {
            k = new Kaizen(st, ed, piller, category, machine_name, idea_of_kaizen, problem, analysis, counter_measures, results,
                    beforeImageURL, afterImageURL, name, UID, kID, madeby1, checkedby, approvedby, helpedBy, s_dept,currentDate);
            k.setMADEBY_boolean(true);
            k.setAPPROVEDBY_boolean(false);
            k.setAPPROVEDBY_boolean(false);

        } else {
            k = new Kaizen(st, ed, piller, category, machine_name, idea_of_kaizen, problem, analysis, counter_measures, results,
                    name, UID, kID, madeby1, checkedby, approvedby, helpedBy, s_dept,currentDate);
            k.setMADEBY_boolean(true);
            k.setAPPROVEDBY_boolean(false);
            k.setAPPROVEDBY_boolean(false);
        }

        mRef = database.getReference().child("Kaizen").child(s_dept).child(UID);
        mRef.child(kID).setValue(k);
        Log.d("checkDEPT", "onDataChange: " + s_dept);


        return k;
    }

    public List<String> getNamesOfTeamMembers() {
        List<String> nameList = new ArrayList<String>();
        LinearLayout layout = findViewById(R.id.linearLayout_enter_person_name);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof EditText) {
                String name = ((EditText) v).getText().toString().trim();
                nameList.add(name);
            }
        }
        return nameList;
    }


    private void BottomNavigationMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.kaizenView: {
                        startActivity(new Intent(getApplicationContext(), ViewHomeScreen.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                }
                return false;
            }
        });
    }


    public static int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float intdp = px / (metrics.densityDpi / 160f);
        int dp = (int) intdp;
        return dp;
    }


    private void refreshForm() {
        enter_analysis.setText("");
        enter_counter_measures.setText("");
        enter_idea_of_kaizen.setText("");
        enter_machine_name.setText("");
        enter_problem.setText("");
        enter_results.setText("");
        editText_endDate.setText("");
        edittext_startDate.setText("");
        beforePic.setImageResource(R.drawable.googleg_standard_color_18);
        afterPic.setImageResource(R.drawable.googleg_disabled_color_18);
        kaizenNumber.setText("");
        checkedBy.setText("");
        approvedBy.setText("");
        helpedBY.setText("");
    }

    private void callProfileActivity() {
        Intent intent = new Intent(SendHomeScreen.this, ProfileView.class);
        startActivity(intent);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void sendForChecking(String empID, Kaizen k, String uid_currentSender) {
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference fRef = FirebaseDatabase.getInstance().getReference().child("Checking");
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
                        } else {
                            Log.d("check", "Failed to send to receiver");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendForAssistance(String empID, Kaizen k, String uid_currentSender) {
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference fRef = FirebaseDatabase.getInstance().getReference().child("Assistance");
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
                        } else {
                            Log.d("check", "Failed to send to receiver");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void getAlreadyAvailable() {
        boolean[] checkAlready = new boolean[1];
        String s1 = kaizenNumber.getText().toString();
        if (kRef != null) {
            kRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    Log.d("checkRepID", "onSuccess: entered onSuccess");
                    for (DataSnapshot temp_kaizenid : dataSnapshot.getChildren()) {
                        Log.d("checkRepID", "temp id key = " + temp_kaizenid.child("kaizenid").getValue(String.class) + " compare with = " + s1);
                        //Log.d("checkRepID", "onDataChange: Compare result = " + Boolean.toString(Integer.parseInt(temp_kaizenid.child("kaizenid").getValue(String.class)) == Integer.parseInt(kaizenNumber.getText().toString().trim())));
                        if (Integer.parseInt(temp_kaizenid.child("kaizenid").getValue(String.class)) == Integer.parseInt(kaizenNumber.getText().toString().trim())) {
                            Log.d("checkRepID", "set to true");
                            checkAlready[0] = true;
                            break;
                        } else {
                            Log.d("checkRepID", "onDataChange: not changed");
                        }
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Log.d("checkRepID", "Return value : " + Boolean.toString(checkAlready[0]));

                }
            });
        }
    }


}