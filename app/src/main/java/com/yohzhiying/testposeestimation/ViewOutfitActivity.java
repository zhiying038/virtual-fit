package com.yohzhiying.testposeestimation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ViewOutfitActivity extends AppCompatActivity {

    ImageView backIcon, outfitImage;
    TextView viewOutfitName, viewOutfitDescription, viewOutfitCategory;
    String _category, _name, _description, _url;
    Bitmap outfitBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_outfit);

        backIcon = findViewById(R.id.view_outfit_back);
        outfitImage = findViewById(R.id.view_outfit_image);
        viewOutfitCategory = findViewById(R.id.view_outfit_category);
        viewOutfitDescription = findViewById(R.id.view_outfit_description);
        viewOutfitName = findViewById(R.id.view_outfit_name);

        _category = getIntent().getStringExtra("itemCategory");
         _name = getIntent().getStringExtra("itemName");
        _description = getIntent().getStringExtra("itemDescription");
        _url = getIntent().getStringExtra("itemUrl");

        Picasso.get().load(_url).into(outfitImage);
        viewOutfitCategory.setText(_category);
        viewOutfitName.setText(_name);
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
}