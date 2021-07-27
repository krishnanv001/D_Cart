package com.techdev.dcart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.techdev.dcart.Activity.FrontPageActivity;
import com.techdev.dcart.Activity.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private String TAG = LoginActivity.class.getSimpleName();

    AppCompatEditText editTextName,editTextPassword;
    Button button;

    //  https://londonappdeveloper.com/how-to-use-git-hub-with-android-studio/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button  =   findViewById(R.id.btn_submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent page_ = new Intent(LoginActivity.this, FrontPageActivity.class);
//                Intent page_ = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(page_);

            }
        });

    }


}