package com.tuneerkhargonkar.studios.kaizen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class UserRegister extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText emailTV, passwordTV, nameTV,empID_TV,Dept_name;
    private Button regBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private boolean mFlag = false;
    private TextView backtosignin;
    private FirebaseDatabase database;
    private DatabaseReference mref,kRef;
    private Spinner spinner_department;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        initializeUI();

        spinner_department = (Spinner) findViewById(R.id.department);
        spinner_department.setOnItemSelectedListener(this);
        List<String> category_pillar = new ArrayList<String>();
        category_pillar.add("Maintenance");
        category_pillar.add("R and D");
        category_pillar.add("ME");
        category_pillar.add("Quality");
        category_pillar.add("HR");;

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category_pillar);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_department.setAdapter(dataAdapter);

        backtosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Intent intent = new Intent(UserRegister.this,UserSignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerNewUser();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String dept, email, password, name, empID, points="0";
        name = nameTV.getText().toString().trim();
        email = emailTV.getText().toString().trim();
        password = passwordTV.getText().toString().trim();
        empID = empID_TV.getText().toString().trim();
        dept = spinner_department.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter yout name", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length()<6) {
            Toast.makeText(getApplicationContext(),"Password must be at least 6 characters long",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(empID)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("database", "onComplete: Calling push to database 1");



        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        Log.d("database", "onComplete: Calling push to database 2");
                        Log.d("database", "onComplete: "+Boolean.toString(isNewUser));

                        if (isNewUser) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Log.d("database", "onComplete: Calling push to database 3");
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);

                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                String UID = user.getUid();
                                                Log.d("database", "onComplete: Calling push to database 4");
                                                pushToDatabase(nameTV.getText().toString().trim(),emailTV.getText().toString().trim(),passwordTV.getText().toString().trim(),UID,empID_TV.getText().toString().trim(),0,points,dept);
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(name).build();
                                                user.updateProfile(profileUpdates);

                                                if (mFlag == false) {
                                                    Intent intent = new Intent(UserRegister.this, UserSignIn.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);

                                                }

                                                Log.d("check","user create completed");

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "This email is already registered!", BaseTransientBottomBar.LENGTH_LONG);
                            mFlag = true;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), UserSignIn.class));
        overridePendingTransition(0, 0);
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.email);
        passwordTV = findViewById(R.id.password);
        nameTV = findViewById(R.id.name);
        progressBar = findViewById(R.id.progressBar);
        regBtn = findViewById(R.id.register);
        backtosignin = findViewById(R.id.backToSignIn_page);
        empID_TV = findViewById(R.id.employeeID);
    }

    private void pushToDatabase(String name, String email, String password, String uid, String empID,int numberOfapprovals,String points,String department){
        User user = new User(name,email,password,uid,empID,0,department);
        Log.d("database", "pushToDatabase: Push to database");
        kRef = database.getReference().child("UsersPoints").child(uid);
        kRef.setValue(points);
        mref = database.getReference().child("Users").child(uid);
        mref.setValue(user);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}