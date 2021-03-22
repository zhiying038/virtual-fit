package com.yohzhiying.testposeestimation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ForgetPasswordSuccessMessageActivity extends AppCompatActivity {

    ImageView screenIcon;
    TextView title, description;
    Button backToLoginBtn;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // To Remove Status Bar
        setContentView(R.layout.activity_forget_password_success_message);

        screenIcon = findViewById(R.id.success_message_icon);
        title = findViewById(R.id.success_message_title);
        description = findViewById(R.id.success_message_description);
        backToLoginBtn = findViewById(R.id.success_message_btn);

        animation = AnimationUtils.loadAnimation(this,R.anim.slide_animation);
        screenIcon.setAnimation(animation);
        title.setAnimation(animation);
        description.setAnimation(animation);
        backToLoginBtn.setAnimation(animation);
    }

    public void backToLogin(View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}