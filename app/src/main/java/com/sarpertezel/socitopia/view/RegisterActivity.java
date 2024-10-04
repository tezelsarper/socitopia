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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sarpertezel.socitopia.adapter.UserIdSingleton;
import com.sarpertezel.socitopia.adapter.FullNameSingleton;
import com.sarpertezel.socitopia.R;
import com.sarpertezel.socitopia.databinding.ActivityRegisterBinding;
import com.sarpertezel.socitopia.model.UserRegister;
import com.sarpertezel.socitopia.adapter.profilePicUrlSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    UserRegister userRegister;
    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    Uri imageData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
        registerLauncher();
        setCircularImage();
    }
    public void setCircularImage() {

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.BLACK);


        shape.setStroke(4, Color.BLACK);


        binding.RegisterImageView.setBackground(shape);
        binding.RegisterImageView.setClipToOutline(true);
        binding.RegisterImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        int imageSize = getResources().getDisplayMetrics().widthPixels / 2 ;
        ViewGroup.LayoutParams params = binding.RegisterImageView.getLayoutParams();
        params.width = imageSize;
        params.height = imageSize;
        binding.RegisterImageView.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.login_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intentToLogin = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intentToLogin);
        return super.onOptionsItemSelected(item);
    }

    public void signUpClicked(View view) {
        String email = binding.RegisterEmailTextView.getText().toString();
        String password = binding.RegisterPasswordTextView.getText().toString();
        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "Missing email and/or password", Toast.LENGTH_LONG).show();
            return;
        }

        String fullName = binding.RegisterFullNameTextView.getText().toString();
        String userName = binding.RegisterUserNameTextView.getText().toString();

        userRegister = new UserRegister(email, password, fullName, userName);

        registerUser(email, password, fullName, userName);
    }

    public void registerUser(String email, String password, final String fullName, final String userName) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userId = UUID.randomUUID().toString();
                        FullNameSingleton.getInstance().setFullName(fullName);
                        UserIdSingleton.getInstance().setUserId(userId);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", fullName);
                        user.put("userName", userName);
                        user.put("email", email);
                        user.put("userId",userId);

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
                                                    profilePicUrlSingleton.getInstance().setProfilePhotoUrl(downloadUrl);


                                                    user.put("profilePicUrl", downloadUrl);

                                                    db.collection("users")
                                                            .document(authResult.getUser().getUid())
                                                            .set(user)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent intentToLogin = new Intent(RegisterActivity.this, MainActivity.class);
                                                                    startActivity(intentToLogin);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            db.collection("users")
                                    .document(authResult.getUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intentToLogin = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intentToLogin);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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
                        binding.RegisterImageView.setImageURI(imageData);
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
                    Toast.makeText(RegisterActivity.this,"Permission Needed",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}
