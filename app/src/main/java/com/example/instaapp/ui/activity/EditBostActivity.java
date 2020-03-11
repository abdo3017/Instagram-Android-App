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
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.example.instaapp.ui.adapter.MyFeedAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static com.example.instaapp.Utils.POSTS;
/**
 * Created by Abdo GHazi
 */
public class EditBostActivity extends AppCompatActivity {
    ImageView imageView;
    EditText editText;
    ImageButton back_post;
    TextView post;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static int position;
    public static MyFeedAdapter myFeedAdapter;
    public static Post postitem;
    public String imageString, userId;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bost);
        imageView = findViewById(R.id.img_post_edited_post);
        editText = findViewById(R.id.txt_desc_edited_post);
        post = findViewById(R.id.share_post_edited_post);
        back_post = findViewById(R.id.back_post);

        System.out.println(postitem.getImage());
        Glide.with(this)
                .load(postitem.getImage())
                .into(imageView);
        editText.setText(postitem.getDisc());

        back_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(EditBostActivity.this);
                dialog.show();
                dialog.setContentView(R.layout.item_photo_filter);
                dialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );
                upLoadEditData(editText.getText().toString());
                UserProfileActivity.check = true;
                startActivity(new Intent(EditBostActivity.this, UserProfileActivity.class));
            }
        });
    }

    void upLoadEditData(final String post) {
        postitem.setDisc(post);
        db.collection(POSTS).document(postitem.getPostId()).set(postitem);
        myFeedAdapter.notifyItemChanged(position);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
