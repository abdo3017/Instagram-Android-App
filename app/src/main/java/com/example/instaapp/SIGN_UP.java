package com.example.instaapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.activity.MainActivity;
import com.example.instaapp.ui.utils.Preferences_Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackandphantom.circularimageview.CircleImage;

import java.util.ArrayList;

import static com.example.instaapp.Utils.USERS;

/**
 * Created by Abdo GHazi
 */
public class SIGN_UP extends AppCompatActivity {


    FirebaseFirestore firestore;
    EditText Email, Password, Name,ConfirmPassword,About,Gender;

    FirebaseAuth auth;
    User current_user;
    Preferences_Utils preferencesUtils;
    FirebaseUser user;
    ProgressDialog progressDialog;
    private int STORAGE_PERMISSION_CODE = 1;
    UploadTask uploadTask;
    private StorageReference mStorageRef;
    private Uri ImageProfileUri = null;
    private CircleImage circleImageView;
    private  boolean ShowPassword  = true;
    private  boolean ShowPassword2  = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Email = findViewById(R.id.EMAIL);
        Gender = findViewById(R.id.GENDER);
        About = findViewById(R.id.ABOUT);
        Name = findViewById(R.id.NAME);
        ConfirmPassword = findViewById(R.id.CONFIRMPASSWORD);
        circleImageView = findViewById(R.id.IMAGE_SIGN_UP);
        mStorageRef = FirebaseStorage.getInstance().getReference("Users");
        Password = findViewById(R.id.PASSWORD);
        firestore = FirebaseFirestore.getInstance();
        preferencesUtils = new Preferences_Utils();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
    public void Sign_Up(View view) {
        String My_email = Email.getText().toString();
        String My_password = Password.getText().toString();
        String My_gender = Gender.getText().toString();
        String My_about = About.getText().toString();
        String My_Username = Name.getText().toString();
        if(TextUtils.isEmpty(Email.getText()))
        {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password.getText()))
        {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Name.getText()))
        {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ConfirmPassword.getText()))
        {
            Toast.makeText(this, "Enter Password Again To Confirm", Toast.LENGTH_SHORT).show();
        }
        else if(!Password.getText().toString().equals(ConfirmPassword.getText().toString()))
        {
            Toast.makeText(this, "Confirm Your Password Failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            if(ImageProfileUri==null)
            {
                current_user = new User(My_Username,My_email,My_email,My_password,My_gender,String.valueOf(ImageProfileUri),My_about,new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());

            }
            else
                current_user = new User(My_Username,My_email,My_email,My_password,My_gender,String.valueOf(ImageProfileUri),My_about,new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());

            auth.createUserWithEmailAndPassword(current_user.getEmail(), current_user.getPassword()).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = auth.getCurrentUser();
                                current_user.setId(user.getEmail());
                                firestore.collection(USERS).document(user.getEmail()).set(current_user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                savestring("userId",current_user.getEmail());
                                                progressDialog.dismiss();
                                                Intent intent =new Intent(SIGN_UP.this, LOGIN_IN.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {

                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SIGN_UP.this,e.getMessage() , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SIGN_UP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void Btn_GetImage(View view) {

        requestStoragePermission();

    }

    /*
*/
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SIGN_UP.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 2);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if(requestCode==2)
        {
            if(data!=null)
            {
               Uri ImageProfile = data.getData();
                Glide.with(getApplicationContext()).load(ImageProfile).into(circleImageView);
                uploadFile(ImageProfile);
            }
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(Uri ImageProfile) {
        if (ImageProfile != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(ImageProfile));


            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.dismiss();//dismiss dialog
                    circleImageView.setImageResource(R.drawable.user);
                    if(uploadTask.isComplete())
                    {

                        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SIGN_UP.this, "Delete Success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SIGN_UP.this, "Not Delete", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        uploadTask.cancel();
                        Toast.makeText(SIGN_UP.this, "Upload Canceled", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            progressDialog.show();



            uploadTask =   fileReference.putFile(ImageProfile);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageProfileUri = uri;
                        }
                    });
                    //Toast.makeText(MessageActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                }
            })      .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SIGN_UP.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            })  .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }


    }


    public void BACK_TO_SIGN_IN(View view) {
        Intent intent =  new Intent(SIGN_UP.this,LOGIN_IN.class);
        startActivity(intent);
        finish();
    }

    public void SHOW_PASSWORD(View view) {


        if(ShowPassword)
        {
            Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ShowPassword = false;
        }
        else {
            Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ShowPassword = true;
        }
    }

    public void SHOW_PASSWORD2(View view) {

        if(ShowPassword2)
        {
            ConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ShowPassword2 = false;
        }
        else {
            ConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ShowPassword2 = true;
        }
    }

    public void savestring(String key, String value) {
        SharedPreferences.Editor editor =
                getSharedPreferences("Userapp", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
}





