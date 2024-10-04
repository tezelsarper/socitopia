package com.sarpertezel.socitopia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sarpertezel.socitopia.adapter.GroupPostAdapter;
import com.sarpertezel.socitopia.databinding.ActivityCreateGroupBinding;
import com.sarpertezel.socitopia.databinding.ActivityGroupsFeedBinding;
import com.sarpertezel.socitopia.model.GroupPost;
import com.sarpertezel.socitopia.view.CreateGroup;
import com.sarpertezel.socitopia.view.FeedActivity;
import com.sarpertezel.socitopia.view.GroupsActivity;
import com.sarpertezel.socitopia.view.MainActivity;
import com.sarpertezel.socitopia.view.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GroupsFeedActivity extends AppCompatActivity {
    ActivityGroupsFeedBinding binding;
    FirebaseAuth auth;

    private FirebaseFirestore  firebaseFirestore;

    GroupPostAdapter groupPostAdapter;

    ArrayList<GroupPost> groupPostArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupsFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        groupPostArrayList = new ArrayList<>();


        auth = FirebaseAuth.getInstance();


        firebaseFirestore = FirebaseFirestore.getInstance();


        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            String groupId = intent.getStringExtra("groupId");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("GroupPost").whereEqualTo("groupId", groupId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(GroupsFeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG);
                    }
                    if (value != null) {
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();

                            String groupId = (String) data.get("groupId");
                            String userId = (String) data.get("userId");
                            String profilePicUrl = (String) data.get("profilePicUrl");
                            String fullName = (String) data.get("fullName");
                            String email = (String) data.get("email");
                            String comment = (String) data.get("comment");
                            String downloadUrl = (String) data.get("downloadUrl");

                            GroupPost groupPost = new GroupPost(groupId, userId, fullName, email, comment, downloadUrl, profilePicUrl);
                            groupPostArrayList.add(groupPost);


                        }

                        groupPostAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        binding.groupRecyclerView.setLayoutManager(new LinearLayoutManager(GroupsFeedActivity.this));
        groupPostAdapter = new GroupPostAdapter(groupPostArrayList);
        binding.groupRecyclerView.setAdapter(groupPostAdapter);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent = new Intent(GroupsFeedActivity.this, FeedActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.create_group:
                        Intent intentToCreateGroup = new Intent(GroupsFeedActivity.this,CreateGroup.class);
                        startActivity(intentToCreateGroup);
                        finish();
                        return true;
                    case R.id.menu_add_post:
                        Intent intentToMain = new Intent(GroupsFeedActivity.this,GroupUploadActivity.class);
                        startActivity(intentToMain);
                        finish();
                        return true;
                    case R.id.menu_groups:
                        Intent intentToGroups = new Intent(GroupsFeedActivity.this, GroupsActivity.class);
                        startActivity(intentToGroups);
                        finish();
                        return true;
                    case R.id.menu_profile:
                        Intent intentToProfile = new Intent(GroupsFeedActivity.this,ProfileActivity.class);
                        startActivity(intentToProfile);
                        finish();
                        return true;
                    default:
                        return false;
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