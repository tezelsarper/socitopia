package com.sarpertezel.socitopia.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sarpertezel.socitopia.R;
import com.sarpertezel.socitopia.adapter.GroupsAdapter;
import com.sarpertezel.socitopia.databinding.ActivityGroupsBinding;
import com.sarpertezel.socitopia.model.Groups;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GroupsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewGroup;
    private GroupsAdapter groupsAdapter;

    FirebaseFirestore firebaseFirestore;

    ArrayList<Groups> groupsArrayList;
    ActivityGroupsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        groupsArrayList = new ArrayList<>();


        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        binding.recyclerViewGroup.setLayoutManager(new LinearLayoutManager(GroupsActivity.this));
        groupsAdapter = new GroupsAdapter(groupsArrayList);
        binding.recyclerViewGroup.setAdapter(groupsAdapter);




        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent = new Intent(GroupsActivity.this,FeedActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.create_group:
                        Intent intentToCreateGroup = new Intent(GroupsActivity.this,CreateGroup.class);
                        startActivity(intentToCreateGroup);
                        finish();
                        return true;
                    case R.id.menu_add_post:
                        Intent intentToMain = new Intent(GroupsActivity.this, UploadActivity.class);
                        startActivity(intentToMain);
                        finish();
                        return true;
                    case R.id.menu_profile:
                        Intent intentToProfile = new Intent(GroupsActivity.this,ProfileActivity.class);
                        startActivity(intentToProfile);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });

    }
    public  void getData() {

        firebaseFirestore.collection("Groups").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(GroupsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG);
                }
                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        String groupId = (String) data.get("groupId");
                        String groupProfilePicURL = (String) data.get("groupProfilePicURL");
                        String groupName = (String) data.get("groupName");


                        Groups groups = new Groups(groupId, groupName, groupProfilePicURL);
                        groupsArrayList.add(groups);


                    }

                    groupsAdapter.notifyDataSetChanged();
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
        Intent intentToHome = new Intent(GroupsActivity.this,MainActivity.class);
        startActivity(intentToHome);
        return super.onOptionsItemSelected(item);
    }
}