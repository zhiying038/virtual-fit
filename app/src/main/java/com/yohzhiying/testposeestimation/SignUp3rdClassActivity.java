package com.yohzhiying.testposeestimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

public class SignUp3rdClassActivity extends AppCompatActivity {

    ScrollView scrollView;
    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;
    RelativeLayout progressbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up3rd_class);

        mAuth = FirebaseAuth.getInstance();

        scrollView = findViewById(R.id.signup_3rd_screen_scroll_view);
        countryCodePicker = findViewById(R.id.country_code_picker);
        phoneNumber = findViewById(R.id.signup_phone_number);
        progressbar = findViewById(R.id.signup_progress_bar);
    }

    public void callVerifyOTPScreen(View view) {
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(this)) {
            showCustomDialog();
            return;
        }

        if (!validatePhoneNumber()) {
            return;
        }
        progressbar.setVisibility(View.VISIBLE);

        final String _fullName = getIntent().getStringExtra("fullName");
        final String _email = getIntent().getStringExtra("email");
        final String _username = getIntent().getStringExtra("username");
        final String _password = getIntent().getStringExtra("password");
        final String _date = getIntent().getStringExtra("date");
        final String _gender = getIntent().getStringExtra("gender");;

        String _getUserEnteredPhoneNumber = phoneNumber.getEditText().getText().toString().trim();
        if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
            _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
        }
        final String _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUserEnteredPhoneNumber;

        Sessions sessionManager = new Sessions(getApplicationContext(), Sessions.SESSION_USERSESSION);
        sessionManager.createLoginSession(_fullName, _username, _email, _phoneNo, _password, _date, _gender);

        mAuth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(SignUp3rdClassActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                DatabaseReference reference = rootNode.getReference("users");

                if (task.isSuccessful()) {
                    Log.d("Signup", "createUserWithEmail:success");
                    UserHelper addNewUser = new UserHelper(_fullName, _username, _email, _phoneNo, _password, _date, _gender);

                    String userUid = task.getResult().getUser().getUid();

                    reference.child(userUid).setValue(addNewUser);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    Log.d("Signup", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUp3rdClassActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
                progressbar.setVisibility(View.GONE);
            }
        });
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), AuthStartUpActivity.class));
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,20}\\z";
        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkspaces)) {
            phoneNumber.setError("No White spaces are allowed!");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    public void callSignUp2nd(View view) {
        startActivity(new Intent(getApplicationContext(), SignUp2ndClassActivity.class));
    }
}