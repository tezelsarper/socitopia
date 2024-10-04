package com.sarpertezel.socitopia.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sarpertezel.socitopia.R;
import com.sarpertezel.socitopia.adapter.PostAdapter;
import com.sarpertezel.socitopia.databinding.ActivityFeedBinding;
import com.sarpertezel.socitopia.databinding.ActivityProfileBinding;
import com.sarpertezel.socitopia.model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth auth ;

    CountDownTimer countDownTimer;
    private FirebaseFirestore  firebaseFirestore;

    private ActivityFeedBinding binding;

    PostAdapter postAdapter;

    private SharedPreferences sharedPreferences;


    ArrayList<Post> postArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        long lastTime = sharedPreferences.getLong("lastTime", 0);
        if(System.currentTimeMillis() - lastTime < 24 * 60 * 60 * 1000){
            Toast.makeText(FeedActivity.this, "24 hours have not passed yet.", Toast.LENGTH_LONG).show();
            Intent intentToGroups = new Intent(FeedActivity.this, GroupsActivity.class);
            startActivity(intentToGroups);
            finish();
        }

        postArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.create_group:
                        Intent intentToCreateGroup = new Intent(FeedActivity.this,CreateGroup.class);
                        startActivity(intentToCreateGroup);
                        finish();
                        return true;
                    case R.id.menu_add_post:
                        Intent intentToMain = new Intent(FeedActivity.this,UploadActivity.class);
                        startActivity(intentToMain);
                        finish();
                        return true;
                    case R.id.menu_groups:
                        Intent intentToGroups = new Intent(FeedActivity.this,GroupsActivity.class);
                        startActivity(intentToGroups);
                        finish();
                        return true;
                    case R.id.menu_profile:
                        Intent intentToProfile = new Intent(FeedActivity.this,ProfileActivity.class);
                        startActivity(intentToProfile);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
       countDownTimer  =  new  CountDownTimer(1800000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                AlertDialog.Builder alert = new AlertDialog.Builder(FeedActivity.this);
                alert.setTitle("A friendly reminder to step back and enjoy life's simple pleasures.");
                alert.setMessage("Are you want to go groups page?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("lastTime", System.currentTimeMillis());
                        editor.apply();

                        Intent intentLimit = new Intent(FeedActivity.this,GroupsActivity.class);
                        finish();
                        startActivity(intentLimit);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                    }
                });
                alert.show();
            }
        }.start();

    }
   @Override
    protected void onResume() {
        super.onResume();

        long lastTime = sharedPreferences.getLong("lastTime", 0);
        if(System.currentTimeMillis() - lastTime < 24 * 60 * 60 * 1000){
            Toast.makeText(FeedActivity.this, "24 hours have not passed yet.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }




    public  void getData()
    {
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(),Toast.LENGTH_LONG);
                }
                if(value != null)
                {
                    for(DocumentSnapshot snapshot : value.getDocuments())
                    {
                        Map<String ,Object> data = snapshot.getData();

                        String userId = (String) data.get("userId");
                        String profilePicUrl = (String) data.get("profilePicUrl");
                        String fullName = (String) data.get("fullName");
                        String email = (String) data.get("email");
                        String comment = (String) data.get("comment");
                        String downloadUrl = (String) data.get("downloadUrl");

                        Post post = new Post(userId,fullName,email,comment,downloadUrl,profilePicUrl);
                        postArrayList.add(post);


                    }

                    postAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.signout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

       if(item.getItemId() == R.id.signout)
        {

            auth.signOut();

            Intent intentToMain = new Intent(this, MainActivity.class);
            startActivity(intentToMain);
            finish();
        }
       return super.onOptionsItemSelected(item);

    }


}