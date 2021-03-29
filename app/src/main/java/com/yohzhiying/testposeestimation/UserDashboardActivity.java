package com.yohzhiying.testposeestimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView menuIcon;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView outfitRecyclerView;
    SearchView searchView;

    OutfitAdapter outfitAdapter;
    List<Outfit> outfits;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d("UserDashbaord", firebaseUser.toString());
            userUid = firebaseUser.getUid();
        }


        reference = FirebaseDatabase.getInstance().getReference().child("outfits").child(userUid);

        menuIcon = findViewById(R.id.menu_icon);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        outfitRecyclerView = findViewById(R.id.recycler_view_outfits);
        searchView = findViewById(R.id.search_outfit);

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (!s.isEmpty()) {
                        searchOutfit(s);
                    } else {
                        outfitRecycler();
                    }

                    return true;
                }
            });
        }

        outfitRecycler();
        navigationDrawer();
    }

    private void outfitRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        outfitRecyclerView.setHasFixedSize(true);
        outfitRecyclerView.setLayoutManager(linearLayoutManager);

        outfits = new ArrayList<>();
        outfitAdapter = new OutfitAdapter(this, outfits);
        outfitRecyclerView.setAdapter(outfitAdapter);

        getOutfitByPhone();
    }

    private void getOutfitByPhone() {
        if (reference != null) {
            reference.orderByChild("outfitName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Outfit outfitHelper = dataSnapshot.getValue(Outfit.class);
                            outfits.add(outfitHelper);
                        }
                        outfitAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void searchOutfit(String str) {
        ArrayList<Outfit> myList = new ArrayList<>();
        for (Outfit object : myList) {
            if (object.getOutfitName().toLowerCase().contains(str.toLowerCase())) {
                myList.add(object);
            }
        }

        OutfitAdapter filterOutfitAdapter = new OutfitAdapter(this, myList);
        outfitRecyclerView.setAdapter(filterOutfitAdapter);
    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
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
            case R.id.nav_rate_us:
                startActivity(new Intent(getApplicationContext(), RateUsActivity.class));
                finish();
                break;
            case R.id.nav_add_outfit:
                startActivity(new Intent(getApplicationContext(), AddOutfitActivity.class));
                break;
        }

        return true;
    }
}