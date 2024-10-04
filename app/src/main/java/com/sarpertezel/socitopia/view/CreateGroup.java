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
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sarpertezel.socitopia.R;
import com.sarpertezel.socitopia.adapter.GroupIdSingleton;
import com.sarpertezel.socitopia.databinding.ActivityCreateGroupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class CreateGroup extends AppCompatActivity {
    ActivityCreateGroupBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    FirebaseStorage firebaseStorage;

    FirebaseFirestore firebaseFirestore;
    StorageReference  storageReference;

    Uri imageData;

    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setCircularImage();

        registerLauncher();
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent = new Intent(CreateGroup.this, FeedActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.menu_add_post:
                        Intent intentToMain = new Intent(CreateGroup.this,UploadActivity.class);
                        startActivity(intentToMain);
                        finish();
                        return true;
                    case R.id.menu_groups:
                        Intent intentToGroups = new Intent(CreateGroup.this, GroupsActivity.class);
                        startActivity(intentToGroups);
                        finish();
                        return true;
                    case R.id.menu_profile:
                        Intent intentToProfile = new Intent(CreateGroup.this,ProfileActivity.class);
                        startActivity(intentToProfile);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void createClicked(View view)
    {
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
                            String groupProfilePicURL = uri.toString();

                            String groupName = binding.CreateGroupTextView.getText().toString();

                            String groupId = UUID.randomUUID().toString();

                            GroupIdSingleton.getInstance().setGroupId(groupId);


                            HashMap<String, Object> groupData = new HashMap<>();
                            groupData.put("groupName",groupName );
                            groupData.put("groupId", groupId);
                            groupData.put("groupProfilePicURL", groupProfilePicURL);

                            firebaseFirestore.collection("Groups")
                                    .document(groupId)
                                    .set(groupData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(CreateGroup.this, GroupsActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CreateGroup.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateGroup.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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


    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.CreateGroupImage.setImageURI(imageData);
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
                    Toast.makeText(CreateGroup.this, "Permission Needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void setCircularImage() {

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.BLACK);


        shape.setStroke(4, Color.BLACK);


        binding.CreateGroupImage.setBackground(shape);
        binding.CreateGroupImage.setClipToOutline(true);
        binding.CreateGroupImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        int imageSize = getResources().getDisplayMetrics().widthPixels / 2 ;
        ViewGroup.LayoutParams params = binding.CreateGroupImage.getLayoutParams();
        params.width = imageSize;
        params.height = imageSize;
        binding.CreateGroupImage.setLayoutParams(params);
    }
}
