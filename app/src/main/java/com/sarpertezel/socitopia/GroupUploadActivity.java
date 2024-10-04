package com.sarpertezel.socitopia;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sarpertezel.socitopia.adapter.FullNameSingleton;
import com.sarpertezel.socitopia.adapter.GroupIdSingleton;
import com.sarpertezel.socitopia.adapter.UserIdSingleton;
import com.sarpertezel.socitopia.adapter.profilePicUrlSingleton;
import com.sarpertezel.socitopia.databinding.ActivityGroupUploadBinding;
import com.sarpertezel.socitopia.databinding.ActivityGroupsFeedBinding;
import com.sarpertezel.socitopia.view.CreateGroup;
import com.sarpertezel.socitopia.view.FeedActivity;
import com.sarpertezel.socitopia.view.GroupsActivity;
import com.sarpertezel.socitopia.view.MainActivity;
import com.sarpertezel.socitopia.view.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class GroupUploadActivity extends AppCompatActivity {
    ActivityGroupUploadBinding binding;
    FirebaseStorage firebaseStorage;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap SelectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent = new Intent(GroupUploadActivity.this, FeedActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.create_group:
                        Intent intentToCreateGroup = new Intent(GroupUploadActivity.this, CreateGroup.class);
                        startActivity(intentToCreateGroup);
                        finish();
                        return true;
                    case R.id.menu_groups:
                        Intent intentToGroups = new Intent(GroupUploadActivity.this, GroupsActivity.class);
                        startActivity(intentToGroups);
                        finish();
                        return true;
                    case R.id.menu_profile:
                        Intent intentToProfile = new Intent(GroupUploadActivity.this, ProfileActivity.class);
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
    public void uploadGroupClicked(View view) {
        if (imageData != null) {
            UUID uuid = UUID.randomUUID();
            String imageName = "images/" + uuid + ".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference newReference = firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            String comment = binding.UploadGroupComment.getText().toString();

                            FirebaseUser user = auth.getCurrentUser();
                            String email = user.getEmail();

                            FullNameSingleton fullNameSingleton = FullNameSingleton.getInstance();
                            String fullName = fullNameSingleton.getFullName();

                            GroupIdSingleton groupIdSingleton = GroupIdSingleton.getInstance();
                            String groupId = groupIdSingleton.getGroupId();

                            profilePicUrlSingleton ProfilePicUrlSingleton = profilePicUrlSingleton.getInstance();
                            String profilepicUrl = ProfilePicUrlSingleton.getProfilePhotoUrl();

                            String postId = UUID.randomUUID().toString();
                            Intent intentUserId = getIntent();
                            UserIdSingleton userIdSingleton = UserIdSingleton.getInstance();
                            String userId = userIdSingleton.getUserId();
                            HashMap<String, Object> groupPostData = new HashMap<>();
                            groupPostData.put("groupId",groupId);
                            groupPostData.put("postId", postId);
                            groupPostData.put("userId", userId);
                            groupPostData.put("profilePicUrl", profilepicUrl);
                            groupPostData.put("fullName", fullName);
                            groupPostData.put("email", email);
                            groupPostData.put("downloadUrl", downloadUrl);
                            groupPostData.put("comment", comment);
                            groupPostData.put("date", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("GroupPost").document(postId).set(groupPostData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(GroupUploadActivity.this,GroupsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(GroupUploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GroupUploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
                }
            });
        }
    }
    public void selectImage(View view)
    {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Snackbar.make(view,"Permission needed",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }
            else
            {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else
        {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }
    }

    public void registerLauncher()
    {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK)
                {
                    Intent intentFromResult = result.getData();
                    if( intentFromResult != null)
                    {
                        imageData =intentFromResult.getData();
                        binding.UploadGroupImage.setImageURI(imageData);
                    /*try
                    {
                        if(Build.VERSION.SDK_INT >= 28) {
                            ImageDecoder.Source source = ImageDecoder.createSource(UploadActivity.this.getContentResolver(), imageData);
                            Bitmap selectedImage = ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(selectedImage);
                        }
                        else
                        {
                            selectImage() = MediaStore.Images.Media.getBitmap(UploadActivity.this,getContentResolver(),imageData);
                            binding.imageView.setImageBitmap(selectedImage);
                        }
                    }
                        catch (Exception e)
                        {

                        }*/

                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result)
                {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }
                else
                {
                    Toast.makeText(GroupUploadActivity.this,"Permission Needed",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}