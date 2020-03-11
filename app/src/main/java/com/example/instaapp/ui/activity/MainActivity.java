package com.example.instaapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instaapp.R;
import com.example.instaapp.genericFirebase;
import com.example.instaapp.models.Post;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.adapter.FeedAdapter;

import com.example.instaapp.ui.utils.Preferences_Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jackandphantom.circularimageview.CircleImage;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.OnClick;

import static com.example.instaapp.Utils.POSTS;
import static com.example.instaapp.Utils.USERS;
/**
 * Created by Abdo GHazi
 */
public class MainActivity extends BaseDrawerActivity {

    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";
    public static List<Post> postList;
    private Uri mImageUri;
    FirebaseFirestore firebaseFirestore;
    RecyclerView rvFeed;
    FloatingActionButton fabCreate;
    CoordinatorLayout clContent;
    ImageButton search_user,chatapp;
    SwipeRefreshLayout refreshLayout;
    ProgressDialog dialog;
    private FeedAdapter feedAdapter;
    User user;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseFirestore = FirebaseFirestore.getInstance();
        rvFeed = findViewById(R.id.rvFeed);
        fabCreate = findViewById(R.id.btnCreate);
        clContent = findViewById(R.id.content);
        search_user = findViewById(R.id.search_user);
        refreshLayout = findViewById(R.id.ref_feeds);
        chatapp = findViewById(R.id.chatapp);
        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePhotoClick();
            }
        });
        chatapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=getPackageManager().getLaunchIntentForPackage("com.example.mychat");
                startActivity(i);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupFeed();
            }
        });
        setupFeed();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupFeed() {
        dialog=new ProgressDialog(this);
        dialog.show();
        dialog.setContentView(R.layout.item_photo_filter);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        firebaseFirestore.collection(USERS).document(Profile.user.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user=documentSnapshot.toObject(User.class);
                Profile.user =new User(user) ;
                firebaseFirestore.collection(POSTS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        postList = new ArrayList<>();
                        for (String follow : Profile.user.getFollowing()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Post post = documentSnapshot.toObject(Post.class);
                                if (Objects.equals(post.getUserId(), follow)) {
                                    postList.add(post);
                                }
                            }
                        }
                        Collections.sort(postList);
                        feedAdapter = new FeedAdapter(postList, MainActivity.this);
                        refreshLayout.setRefreshing(false);

                        CommentsActivity.feedAdapter=feedAdapter;
                        rvFeed.setAdapter(feedAdapter);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void onTakePhotoClick() {
        openFileChooser();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void uploadFile() {

        if (mImageUri != null) {
            Intent intent = new Intent(MainActivity.this, BostData.class);
            intent.putExtra("imagePath", mImageUri.toString());
            startActivity(intent);

        } else {
             Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null) {
            mImageUri = data.getData();
            uploadFile();
        }
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

}