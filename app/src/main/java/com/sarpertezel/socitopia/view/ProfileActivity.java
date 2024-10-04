package com.sarpertezel.socitopia.view;

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
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import com.sarpertezel.socitopia.adapter.profilePicUrlSingleton;
import com.sarpertezel.socitopia.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.sarpertezel.socitopia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String userId1;

    private String postId;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    Uri imageData;

    Bitmap selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        registerLauncher();

        binding.profileImageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        binding.profileImageView.setClipToOutline(true);
        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("userId");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String fullName = documentSnapshot.getString("fullName");
                        String userName = documentSnapshot.getString("userName");
                        String profilePicUrl = documentSnapshot.getString("profilePicUrl");
                        updateUI(fullName, userName, profilePicUrl);
                    } else {
                        /*Toast.makeText(ProfileActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                        updateUI("Unknown User", "", "");*/
                    }
                }
            });
        }




        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId1 = auth.getCurrentUser().getUid();


        firebaseFirestore.collection("users").document(userId1).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            String userName = documentSnapshot.getString("userName");
                            String profilePicUrl = documentSnapshot.getString("profilePicUrl");
                            updateUI(fullName, userName, profilePicUrl);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent = new Intent(ProfileActivity.this, FeedActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.create_group:
                        Intent intentToCreateGroup = new Intent(ProfileActivity.this,CreateGroup.class);
                        startActivity(intentToCreateGroup);
                        finish();
                        return true;
                    case R.id.menu_add_post:
                        Intent intentToMain = new Intent(ProfileActivity.this, UploadActivity.class);
                        startActivity(intentToMain);
                        finish();
                        return true;
                    case R.id.menu_groups:
                        Intent intentToGroups = new Intent(ProfileActivity.this, GroupsActivity.class);
                        startActivity(intentToGroups);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void updateUI(String fullName, String userName, String profilePicUrl) {
        binding.fullNameTextView.setText(fullName);
        Picasso picasso = Picasso.get();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            picasso.load(profilePicUrl).into(binding.profileImageView);
        } else {
            picasso.load(R.drawable.profilepic).into(binding.profileImageView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.signout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signout) {
            auth.signOut();
            Intent intentToMain = new Intent(this, MainActivity.class);
            startActivity(intentToMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectImage(View view)
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Snackbar.make(view,"Permission needed",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
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


    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.profileImageView.setImageURI(imageData);
                        uploadImageToFirebase();
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                } else {
                    Toast.makeText(ProfileActivity.this, "Permission Needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadImageToFirebase() {
        if (imageData != null) {
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();


            StorageReference imageRef = storageReference.child("profile_images").child(userId1);


            imageRef.putFile(imageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUri) {
                                            String downloadUrl = downloadUri.toString();
                                            profilePicUrlSingleton.getInstance().setProfilePhotoUrl(downloadUrl);

                                            String profilePicUrl = downloadUri.toString();
                                            saveProfilePicUrl(profilePicUrl);
                                            //saveProfilePicUrl2(profilePicUrl);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(ProfileActivity.this, "Failed to get download URL.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(ProfileActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveProfilePicUrl(String profilePicUrl) {
        firebaseFirestore.collection("users")
                .document(userId1)
                .update("profilePicUrl", profilePicUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Profile picture updated in Users collection.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile picture in Users collection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }





}





