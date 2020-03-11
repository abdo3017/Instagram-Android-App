package com.example.instaapp.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instaapp.R;
import com.example.instaapp.models.Comment;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.activity.CommentsActivity;
import com.example.instaapp.ui.activity.UserProfileActivity;
import com.example.instaapp.ui.utils.RoundedTransformation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.instaapp.Utils.USERS;

/**
 * Created by Abdo GHazi
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;
    List<Comment> comments = new ArrayList<>();
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public CommentsAdapter() {
    }

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
        itemsCount = comments.size();
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (!comments.isEmpty()) {
            runEnterAnimation(viewHolder.itemView, position);
            final CommentViewHolder holder = (CommentViewHolder) viewHolder;
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection(USERS).document(comments.get(position).getCommenterId())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserProfileActivity.user = documentSnapshot.toObject(User.class);
                    Glide.with(context).load(documentSnapshot.toObject(User.class).getImage()).into(holder.ivUserAvatar);

                    holder.tvComment.setText(comments.get(position).getDisc());
                    holder.tvCommenter.setText(UserProfileActivity.user.getName());
                }
            });
            holder.tvCommenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Profile.user.getId().equals(comments.get(position).getCommenterId()))
                        UserProfileActivity.check = true;
                    else UserProfileActivity.check = false;
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    Activity activity = (Activity) context;
                    activity.startActivity(intent);
                }
            });
            holder.ivUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Profile.user.getId().equals(comments.get(position).getCommenterId()))
                        UserProfileActivity.check = true;
                    else UserProfileActivity.check = false;
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    Activity activity = (Activity) context;
                    activity.startActivity(intent);
                }
            });
        }

    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        if (comments == null) return 0;
        return comments.size();
    }

    public void addItem() {
        notifyItemInserted(comments.size());
        itemsCount++;
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvComment, tvCommenter;

        public CommentViewHolder(View view) {
            super(view);
            ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
            tvComment = view.findViewById(R.id.tvComment);
            tvCommenter = view.findViewById(R.id.tvCommenter);
        }
    }
}
