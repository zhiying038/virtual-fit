package com.yohzhiying.testposeestimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddOutfitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int SELECT_OUTFIT = 100;
    private static final String STORAGE_BUCKET_PATH = "gs://testoutfit-23e9c.appspot.com";

    private Bitmap selectedBitmap = null;
    private int[] backgroundColor = new int[]{255,255,255};

    Slider sensitivityBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Button addOutfitBtn, chooseOutfitBtn;
    ImageView outfitArea, menuIcon;
    TextView sensitive;
    Spinner spinner;
    TextInputLayout outfitNameField, outfitDescriptionField;
    String categorySelected, outfitUrl, userPhone;
    Uri outfitUri, selectedOutfit;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_outfit);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl(STORAGE_BUCKET_PATH);
        user = FirebaseAuth.getInstance().getCurrentUser();

        sensitivityBar = findViewById(R.id.sensitivity_bar);
        addOutfitBtn = findViewById(R.id.outfit_btn);
        outfitArea = findViewById(R.id.outfit_view);
        chooseOutfitBtn = findViewById(R.id.choose_outfit_button);
        spinner = findViewById(R.id.spinner);
        outfitNameField = findViewById(R.id.outfit_name);
        menuIcon = findViewById(R.id.menu_icon);
        drawerLayout = findViewById(R.id.outfit_drawer);
        navigationView = findViewById(R.id.navigation_view);
        outfitDescriptionField = findViewById(R.id.outfit_description);
        sensitive = findViewById(R.id.sensitive_text);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.outfit_categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        sensitivityBar.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ProcessImage image = new ProcessImage();
                Bitmap processedBitmap = image.getOutfitArea(selectedBitmap, (int) value, backgroundColor);
                outfitArea.setImageBitmap(processedBitmap);
            }
        });

        addOutfitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOutfit();
            }
        });

        navigationDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_OUTFIT && resultCode == RESULT_OK && data != null) {
            selectedOutfit = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedOutfit);

                selectedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                outfitArea.setImageBitmap(bitmap);
                outfitArea.setVisibility(View.VISIBLE);
                addOutfitBtn.setVisibility(View.VISIBLE);
                sensitivityBar.setVisibility(View.VISIBLE);
                sensitive.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        categorySelected = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
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

    public void selectOutfit(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Outfit"), SELECT_OUTFIT);
    }

    public void addOutfit() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Add Outfit");
        progressDialog.show();

        userPhone = user.getPhoneNumber();

        if (!validateFields()) {
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) outfitArea.getDrawable()).getBitmap();
        final String outfitName = Objects.requireNonNull(outfitNameField.getEditText()).getText().toString().trim();
        final String outfitDescription = Objects.requireNonNull(outfitDescriptionField.getEditText()).getText().toString().trim();
        final String outfitCategory = spinner.getSelectedItem().toString();
        outfitUri = selectedOutfit;

        if (outfitUri != null && bitmap != null) {
            final StorageReference imageRef = storageReference.child("images/" + userPhone + "/" + System.currentTimeMillis() + getFileExtension(outfitUri));

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] imageByte = stream.toByteArray();

            final UploadTask uploadTask = imageRef.putBytes(imageByte);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                assert downloadUri != null;
                                outfitUrl = downloadUri.toString();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("outfits");

                                Outfit addOutfitHelper = new Outfit(outfitCategory, outfitName, outfitUrl, outfitDescription);
                                reference.child(userPhone).push().setValue(addOutfitHelper);

                                progressDialog.dismiss();
                                startActivity(new Intent(AddOutfitActivity.this, UserDashboardActivity.class));
                            }
                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_add_outfit);
        drawerLayout.setDrawerElevation(0);

        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.nav_rate_us:
                startActivity(new Intent(getApplicationContext(), RateUsActivity.class));
                break;
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), UserDashboardActivity.class));
                break;
        }

        return true;
    }

    private boolean validateFields() {
        String _outfitName = Objects.requireNonNull(outfitNameField.getEditText()).getText().toString().trim();
        String _outfitDescription = outfitDescriptionField.getEditText().getText().toString().trim();

        if (_outfitName.isEmpty()) {
            outfitNameField.setError("Name cannot be empty!");
            return false;
        } else if (_outfitDescription.isEmpty()) {
            outfitDescriptionField.setError("Description cannot be empty!");
            return false;
        } else {
            outfitNameField.setError(null);
            outfitNameField.setErrorEnabled(false);
            outfitDescriptionField.setError(null);
            outfitDescriptionField.setErrorEnabled(false);
            return true;
        }
    }
}