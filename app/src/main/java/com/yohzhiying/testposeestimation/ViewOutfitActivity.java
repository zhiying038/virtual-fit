package com.yohzhiying.testposeestimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ViewOutfitActivity extends AppCompatActivity {

    ImageView backIcon, outfitImage;
    TextView viewOutfitDescription, viewOutfitCategory;
    String _category, _name, _description, _url;
    CollapsingToolbarLayout toolbarLayout;

    Bitmap outfitBitmap = null;
    FirebaseUser firebaseUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_outfit);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        backIcon = findViewById(R.id.view_outfit_back);
        outfitImage = findViewById(R.id.view_outfit_image);
        viewOutfitCategory = findViewById(R.id.view_outfit_category);
        toolbarLayout = findViewById(R.id.collapsing_bar);
        viewOutfitDescription = findViewById(R.id.view_outfit_description);

        _category = getIntent().getStringExtra("itemCategory");
         _name = getIntent().getStringExtra("itemName");
        _description = getIntent().getStringExtra("itemDescription");
        _url = getIntent().getStringExtra("itemUrl");

        Picasso.get().load(_url).into(outfitImage);
        viewOutfitCategory.setText(_category);
        toolbarLayout.setTitle(_name);
        viewOutfitDescription.setText(_description);
    }

    public void backHome(View view) {
        startActivity(new Intent(getApplicationContext(), UserDashboardActivity.class));
        finish();
    }

    public void onPressTry(View view) {
        Picasso.get().load(_url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                outfitBitmap = bitmap;
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        String itemCategory = _category;
        String itemName = _name;
        DrawView.currentOutfit = new Outfit(itemCategory, itemName, outfitBitmap);
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(intent);
    }

    public void onDeleteOutfit(View view) {
        String userPhone = firebaseUser.getPhoneNumber();
        Query dataQuery = FirebaseDatabase.getInstance().getReference().child("outfits").child(userPhone).orderByChild("outfitName").equalTo(_name);

        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    data.getRef().removeValue();
                }
                Toast.makeText(ViewOutfitActivity.this, "Sucessfully deleted outfit", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UserDashboardActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}