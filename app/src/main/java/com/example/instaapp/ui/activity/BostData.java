package com.example.instaapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instaapp.R;
import com.example.instaapp.genericFirebase;
import com.example.instaapp.models.Post;
import com.example.instaapp.models.Profile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by Abdo GHazi
 */
public class BostData extends AppCompatActivity {
ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bost_data);
        imageView = findViewById(R.id.img_post);
        editText = findViewById(R.id.txt_desc_post);
        post = findViewById(R.id.share_post);
        back_post = findViewById(R.id.back_post);


        Intent intent = new Intent();
        Bundle bundle = getIntent().getExtras();
        imageString = bundle.getString("imagePath");
        System.out.println(imageString);
        mImageUri = Uri.parse(imageString);
        Glide.with(BostData.this)
                .load(imageString)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=new ProgressDialog(BostData.this);
                dialog.show();
                dialog.setContentView(R.layout.item_photo_filter);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                upLoadData(editText.getText().toString());
                dialog.dismiss();
                Toast.makeText(BostData.this, "shared ", Toast.LENGTH_LONG).show();
                startActivity(new Intent(BostData.this, MainActivity.class));

            }
        });
        back_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
finish();            }
        });
    }

    public Uri mImageUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;
    ImageView imageView;
    EditText editText;
    ImageButton back_post;
    TextView post;
    public String imageString;

    void upLoadData(String disc) {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
       genericFirebase.upLoadPost(mImageUri, new Post( Profile.user.getId(), null, imageString,disc,c), Profile.user.getId(), BostData.this);

    }


    private void uploadFile() {

        if (mImageUri != null) {
        imageString=mImageUri.toString();
        }
    }

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
                imageView.setImageBitmap(selectedImage);
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
    @Override
    public void onBackPressed() {
       finish();
    }
}
