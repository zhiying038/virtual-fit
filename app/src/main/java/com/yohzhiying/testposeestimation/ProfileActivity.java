package com.yohzhiying.testposeestimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    ImageView menuIcon;
    LinearLayout contentView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView nameLabel, usernameLabel;
    TextInputLayout fullName, username, phoneNumber, email;
    String _name, _username, _email, _phoneNo;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userPhone = user.getPhoneNumber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        fullName = findViewById(R.id.full_name_profile);
        username = findViewById(R.id.username_profile);
        phoneNumber = findViewById(R.id.phone_number_profile);
        email = findViewById(R.id.email_profile);
        nameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username);

        navigationDrawer();
        getUserData();
    }

    private void getUserData() {
        Query checkUser = reference.orderByChild("phoneNo").equalTo(userPhone);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    _name = Objects.requireNonNull(data.child("fullName").getValue()).toString();
                    _username = Objects.requireNonNull(data.child("username").getValue()).toString();
                    _email = Objects.requireNonNull(data.child("email").getValue()).toString();
                    _phoneNo = Objects.requireNonNull(data.child("phoneNo").getValue()).toString();

                    nameLabel.setText(_name);
                    usernameLabel.setText(_username);

                    Objects.requireNonNull(fullName.getEditText()).setText(_name);
                    Objects.requireNonNull(username.getEditText()).setText(_username);
                    Objects.requireNonNull(email.getEditText()).setText(_email);
                    Objects.requireNonNull(phoneNumber.getEditText()).setText(_phoneNo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void update(View view) {
        if (isNameChanged() || isEmailChanged() || isUsernameChanged()) {
            Toast.makeText(this, "Data has been updated!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No changes in data", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNameChanged() {
        if (!_name.equals(fullName.getEditText().getText().toString())) {
            reference.child(userPhone).child("fullName").setValue(fullName.getEditText().getText().toString());
            nameLabel.setText(fullName.getEditText().getText().toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmailChanged() {
        if (!_email.equals(email.getEditText().getText().toString())) {
            reference.child(userPhone).child("email").setValue(email.getEditText().getText().toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean isUsernameChanged() {
        if (!_username.equals(username.getEditText().getText().toString())) {
            reference.child(userPhone).child("username").setValue(username.getEditText().getText().toString());
            usernameLabel.setText(username.getEditText().getText().toString());
            return true;
        } else {
            return false;
        }
    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
        drawerLayout.setDrawerElevation(0);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), UserDashboardActivity.class));
                break;
            case R.id.nav_rate_us:
                startActivity(new Intent(getApplicationContext(), RateUsActivity.class));
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "You've signed out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AuthStartUpActivity.class));
                break;
            case R.id.nav_add_outfit:
                startActivity(new Intent(getApplicationContext(), AddOutfitActivity.class));
                break;
        }
        return true;
    }
}