package com.yohzhiying.testposeestimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ViewOutfitActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView menuIcon, outfitImage;
    TextView viewOutfitName, viewOutfitDescription, viewOutfitCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_outfit);

        menuIcon = findViewById(R.id.view_outfit_menu);
        outfitImage = findViewById(R.id.view_outfit_image);
        viewOutfitCategory = findViewById(R.id.view_outfit_category);
        viewOutfitDescription = findViewById(R.id.view_outfit_description);
        viewOutfitName = findViewById(R.id.view_outfit_name);

        final String _category = getIntent().getStringExtra("itemCategory");
        final String _name = getIntent().getStringExtra("itemName");
        final String _description = getIntent().getStringExtra("itemDescription");
        final String _url = getIntent().getStringExtra("itemUrl");

        Picasso.get().load(_url).into(outfitImage);
        viewOutfitCategory.setText(_category);
        viewOutfitName.setText(_name);
        viewOutfitDescription.setText(_description);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ViewOutfitActivity.this, "You've signed out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AuthStartUpActivity.class));
                break;
            case R.id.nav_rate_us:
                startActivity(new Intent(getApplicationContext(), RateUsActivity.class));
                finish();
                break;
            case R.id.nav_add_outfit:
                startActivity(new Intent(getApplicationContext(), AddOutfitActivity.class));
                break;
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), UserDashboardActivity.class));
                break;
        }

        return true;
    }
}