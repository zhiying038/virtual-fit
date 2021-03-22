package com.yohzhiying.testposeestimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.Calendar;

public class CameraActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    Calendar calendar;

    int time1,time2;

    private boolean checkedPermissions=false;
    private int PERMISSIONS_REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        checkPermission();
    }

    private void checkPermission(){
        if (!checkedPermissions && !allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
            return;
        } else {
            checkedPermissions = true;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container,Camera2BasicFragment.newInstance()).commit();
    }

    private String[] getRequiredPermissions() {
        return new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.CAMERA"};
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,Camera2BasicFragment.newInstance()).commit();
    }
}