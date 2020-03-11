package com.example.instaapp.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.instaapp.R;
import com.example.instaapp.genericFirebase;
import com.example.instaapp.models.Post;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.adapter.FeedAdapter;
import com.example.instaapp.ui.adapter.MyFeedAdapter;
import com.example.instaapp.ui.adapter.UserProfileAdapter;
import com.example.instaapp.ui.utils.CircleTransformation;
import com.example.instaapp.ui.view.RevealBackgroundView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.example.instaapp.Utils.POSTS;

/**
 * Created by Abdo GHazi
 */
public class UserProfileActivity extends BaseDrawerActivity implements View.OnClickListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    public static User user;
    public static boolean check = false;
    LinearLayout layout_followers, layout_following;
    RevealBackgroundView vRevealBackground;
    RecyclerView profile_rv_posts;
    TextView profile_followers;
    TextView profile_following;
    public static TextView posts;
    TextView profile_name;
    TextView profile_email;
    TextView profile_about;
    ImageView profile_img;
    ImageButton back_home;
    Button follow_status_profile;
    View vUserDetails;
    View vUserStats;
    View vUserProfileRoot;
    SwipeRefreshLayout ref_profile;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dialog = new ProgressDialog(this);
        dialog.show();
        dialog.setContentView(R.layout.item_photo_filter);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        layout_followers = findViewById(R.id.layout_followers);
        ref_profile = findViewById(R.id.ref_profile);
        layout_following = findViewById(R.id.layout_following);
        vRevealBackground = findViewById(R.id.vRevealBackground);
        profile_rv_posts = findViewById(R.id.profile_rv_posts);
        profile_followers = findViewById(R.id.profile_followers);
        follow_status_profile = findViewById(R.id.follow_status_profile);

        posts = findViewById(R.id.posts);
        profile_following = findViewById(R.id.profile_following);
        profile_img = findViewById(R.id.profile_img);
        vUserDetails = findViewById(R.id.vUserDetails);
        vUserStats = findViewById(R.id.vUserStats);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        profile_about = findViewById(R.id.profile_about);

        Glide.with(this).load(user.getImage()).into(profile_img);
        profile_following.setText(user.getFollowing().size() + "");
        profile_followers.setText(user.getFollowers().size() + "");
        posts.setText(user.getPosts().size() + "");
        profile_name.setText(user.getName());
        profile_email.setText(user.getEmail());
        profile_about.setText(user.getAbout());
        setupUserProfileGrid();
        init();
        profile_followers.setOnClickListener(this);
        profile_following.setOnClickListener(this);
        layout_followers.setOnClickListener(this);
        layout_following.setOnClickListener(this);
        follow_status_profile.setOnClickListener(this);
        ref_profile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
    }

    private void setupUserProfileGrid() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(UserProfileActivity.this);
        profile_rv_posts.setLayoutManager(layoutManager);
    }

    void init() {
        profile_rv_posts.setVisibility(View.VISIBLE);
        vUserProfileRoot.setVisibility(View.VISIBLE);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(POSTS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                dialog.dismiss();
                List<Post> postList = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Post post = snapshot.toObject(Post.class);
                    for (String postId : user.getPosts()) {
                        if (postId.equals(post.getPostId())) {
                            postList.add(post);
                        }
                    }
                }
                if (!check) {
                    follow_status_profile.setVisibility(View.VISIBLE);
                    if (checkFollowing(user, Profile.user)) {
                        follow_status_profile.setText("following");
                    } else follow_status_profile.setText("follow");

                    CommentsActivity.check = true;
                    FeedAdapter post_adapter = new FeedAdapter(postList, UserProfileActivity.this);
                    CommentsActivity.feedAdapter = post_adapter;
                    profile_rv_posts.setAdapter(post_adapter);

                } else {
                    follow_status_profile.setVisibility(View.GONE);
                    CommentsActivity.check = false;
                    MyFeedAdapter post_adapter = new MyFeedAdapter(postList, UserProfileActivity.this);
                    CommentsActivity.myFeedAdapter = post_adapter;
                    EditBostActivity.myFeedAdapter = post_adapter;
                    profile_rv_posts.setAdapter(post_adapter);
                }
                ref_profile.setRefreshing(false);

                animateUserProfileHeader();
            }
        });

    }


    private void animateUserProfileHeader() {
        vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
        profile_rv_posts.setTranslationY(-profile_rv_posts.getHeight());
        vUserDetails.setTranslationY(-vUserDetails.getHeight());
        vUserStats.setAlpha(0);
        vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        profile_rv_posts.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }

    void followers() {
        System.out.println(user.getId()+"**");
        folowpage.mylist = new ArrayList<>();
        folowpage.type = 2;
        folowpage.mylist = new ArrayList<>(user.getFollowers());
    }

    void following() {
        System.out.println(user.getId()+"**");

        folowpage.mylist = new ArrayList<>();
        folowpage.type = 1;
        folowpage.mylist = new ArrayList<>(user.getFollowing());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_followers:
            case R.id.profile_followers:
                followers();
                startActivity(new Intent(UserProfileActivity.this, folowpage.class));
                break;
            case R.id.profile_following:
            case R.id.layout_following:
                following();
                startActivity(new Intent(UserProfileActivity.this, folowpage.class));
                break;
            case R.id.follow_status_profile:
                if (follow_status_profile.getText().equals("following")) {
                    genericFirebase.unfollowing_relation(user, Profile.user, this);
                    follow_status_profile.setText("follow");
                    profile_followers.setText((Integer.valueOf(profile_followers.getText().toString())-1)+"");
                } else {
                    genericFirebase.following_relation(user, Profile.user, this);
                    follow_status_profile.setText("following");
                    profile_followers.setText((Integer.valueOf(profile_followers.getText().toString())+1)+"");
                }
        }
    }

    boolean checkFollowing(User following, User follower) {
        for (String name : follower.getFollowing())
            if (name.equals(following.getId()))
                return true;
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
