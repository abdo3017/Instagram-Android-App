package com.example.instaapp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaapp.R;
import com.example.instaapp.Utils;
import com.example.instaapp.models.Comment;
import com.example.instaapp.models.Post;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.adapter.CommentsAdapter;
import com.example.instaapp.ui.adapter.FeedAdapter;
import com.example.instaapp.ui.adapter.MyFeedAdapter;
import com.example.instaapp.ui.view.SendCommentButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.example.instaapp.Utils.POSTS;

/**
 * Created by Abdo GHazi
 */
public class CommentsActivity extends BaseDrawerActivity implements SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    public static Post post = new Post();
    public static int position;
    public static  boolean check;
    LinearLayout contentRoot;
    RecyclerView rvComments;
    LinearLayout llAddComment;
    EditText etComment;
    SendCommentButton btnSendComment;
    List<Post> posts = new ArrayList<>();
    private CommentsAdapter commentsAdapter = new CommentsAdapter();
    private int drawingStartLocation;
    ProgressDialog dialog;
    public static FeedAdapter feedAdapter;
    public static MyFeedAdapter myFeedAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        rvComments = findViewById(R.id.rvComments);
        btnSendComment = findViewById(R.id.btnSendComment);
        contentRoot = findViewById(R.id.contentRoot);
        etComment = findViewById(R.id.etComment);
        llAddComment = findViewById(R.id.llAddComment);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        setupComments();
        setupSendCommentButton();

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void setupComments() {
        dialog=new ProgressDialog(this);
        dialog.show();
        dialog.setContentView(R.layout.item_photo_filter);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);
        commentsAdapter = new CommentsAdapter(this, post.getComments());
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
        dialog.dismiss();
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public void onBackPressed() {
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
        finish();
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            commentsAdapter.setDelayEnterAnimation(false);
            if (!post.getComments().isEmpty()) {
                rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());
            } else
                rvComments.smoothScrollBy(0, 0);
            Comment comment = new Comment(Profile.user.getId(), etComment.getText().toString(), post.getUserId());
            commentsAdapter.addItem();
            post.getComments().add(comment);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(POSTS).document(post.getPostId()).set(post);
            if(check)
            feedAdapter.notifyItemChanged(position);
            else myFeedAdapter.notifyItemChanged(position);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
            etComment.setText(null);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }


}
