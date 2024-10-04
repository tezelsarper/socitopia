package com.sarpertezel.socitopia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sarpertezel.socitopia.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();


        SharedPreferences sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        long lastTime = sharedPreferences.getLong("lastTime", 0);

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            if(System.currentTimeMillis() - lastTime >= 24 * 60 * 60 * 1000){
                Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, GroupsActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    public void loginClicked (View view)
    {
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();


        if(email.equals("") || password.equals(""))
        {
            Toast.makeText(this,"enter email and/or password",Toast.LENGTH_LONG).show();
        }
        else
        {
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent = new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void signUpClicked (View view)
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();


    }
}