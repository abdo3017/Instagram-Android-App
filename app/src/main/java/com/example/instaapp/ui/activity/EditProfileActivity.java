package com.example.instaapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instaapp.R;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackandphantom.circularimageview.CircleImage;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.instaapp.Utils.USERS;
/**
 * Created by Abdo GHazi
 */
public class EditProfileActivity extends AppCompatActivity {
    EditText Password, Name, ConfirmPassword, About, Gender;
    private CircleImage circleImageView;
    Button save;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");
        Gender = findViewById(R.id.gender_edit);
        About = findViewById(R.id.about_edit);
        Name = findViewById(R.id.name_edit);
        save=findViewById(R.id.save_edit);
        circleImageView = findViewById(R.id.img_edit);
        Gender.setText(Profile.user.getGender());
        Name.setText(Profile.user.getName());
        About.setText(Profile.user.getAbout());
        Glide.with(this).load(Profile.user.getImage()).into(circleImageView);
        imageString = Profile.user.getImage();
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }
        });
    }

    void change() {

        if (TextUtils.isEmpty(Name.getText())) {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Gender.getText())) {
            Toast.makeText(this, "Enter Your Gender", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(About.getText())) {
            Toast.makeText(this, "Enter info about you", Toast.LENGTH_SHORT).show();
        } else {
            dialog = new ProgressDialog(EditProfileActivity.this);
            dialog.show();
            dialog.setContentView(R.layout.item_photo_filter);
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            if(mImageUri!=null){
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference(USERS);
                final StorageReference fileReference = storageReference.child(Profile.user.getId()
                        + "." + getFileExtension(mImageUri, EditProfileActivity.this));
                fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Profile.user.setImage(uri.toString());
                            }
                        });
                    }
                });
            }
            Profile.user.setName(Name.getText().toString());
            Profile.user.setAbout(About.getText().toString());
            Profile.user.setGender(Gender.getText().toString());
            firebaseFirestore.collection(USERS).document(Profile.user.getId()).set(Profile.user);
            DocumentReference noteRef = firebaseFirestore.document("Users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
            noteRef.update( "username", Profile.user.getName());
            noteRef.update( "imageUrl", Profile.user.getImage());
            dialog.dismiss();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this,MainActivity.class));
        }
    }
    private static String getFileExtension(final Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    String imageString;

    private void uploadFile() {

        if (mImageUri != null) {
            imageString = mImageUri.toString();
        }
    }

    public Uri mImageUri=null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null) {
            mImageUri = data.getData();
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(mImageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                circleImageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            uploadFile();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

}
