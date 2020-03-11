package com.example.instaapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instaapp.R;
import com.example.instaapp.ui.adapter.follower_adapter;
import com.example.instaapp.ui.adapter.following_adapter;

import java.util.List;
import java.util.Objects;
/**
 * Created by Abdo GHazi
 */
public class folowpage extends BaseDrawerActivity {
    public static List<String> mylist;
    public static int type;
    RecyclerView recyclerView;
    ImageButton back_post;
    RecyclerView.LayoutManager layoutManager;
    ProgressDialog dialog;
    SwipeRefreshLayout ref_follow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folowpage);
        back_post = findViewById(R.id.back_post);
        ref_follow = findViewById(R.id.ref_follow);
        ref_follow.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init_view();
            }
        });
        back_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init_view();
    }
    public void init_view() {

        recyclerView = findViewById(R.id.rec_following);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (type == 1) {
            setTitle("Following");
            following_adapter following_adapter = new following_adapter(folowpage.this, mylist, UserProfileActivity.user.getId());
            recyclerView.setAdapter(following_adapter);
            following_adapter.setOnclickitem(new com.example.instaapp.ui.adapter.following_adapter.onclick() {
                @Override
                public void onclick() {
                    UserProfileActivity.check = false;
                    startActivity(new Intent(folowpage.this, UserProfileActivity.class));
                    folowpage.this.finish();
                }
            });
        } else {
            setTitle("Followers");

            follower_adapter following_adapter = new follower_adapter(folowpage.this, mylist);
            recyclerView.setAdapter(following_adapter);
            following_adapter.setOnclickitem(new com.example.instaapp.ui.adapter.following_adapter.onclick() {
                @Override
                public void onclick() {
                    UserProfileActivity.check = false;
                    startActivity(new Intent(folowpage.this, UserProfileActivity.class));
                    finish();
                }
            });
        }
        ref_follow.setRefreshing(false);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
