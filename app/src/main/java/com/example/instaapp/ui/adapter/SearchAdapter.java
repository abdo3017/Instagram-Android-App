package com.example.instaapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instaapp.R;
import com.example.instaapp.genericFirebase;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.activity.UserProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jackandphantom.circularimageview.CircleImage;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Abdo GHazi
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    Context context;

    public SearchAdapter(List<User> users, Context context) {
        this.users = new ArrayList<>();
        this.allusers = new ArrayList<>(users);
        this.context = context;
    }

    public int size = 0;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<User> users = new ArrayList<>();
    List<User> allusers = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    User item;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        item = users.get(position);
        UserProfileActivity.user = item;
        holder.sr_name.setText(item.getName());
        holder.sr_email.setText(item.getAbout());
        Glide.with(context).load(item.getImage()).into(holder.sr_img);
        if (checkFollowing(item, Profile.user, context)) {
            holder.follow_status_search.setText("following");
        } else
            holder.follow_status_search.setText("follow");

        holder.sr_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Profile.user.getId().equals(item.getId()))
                    UserProfileActivity.check = true;
                else UserProfileActivity.check = false;
                Intent intent = new Intent(context, UserProfileActivity.class);
                Activity activity = (Activity) context;
                activity.startActivity(intent);
            }
        });

        holder.sr_img.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Profile.user.getId().equals(users.get(position).getId()))
                            UserProfileActivity.check = true;
                        else UserProfileActivity.check = false;
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        Activity activity = (Activity) context;
                        activity.startActivity(intent);
                    }
                });
        holder.follow_status_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.follow_status_search.getText().equals("following")) {
                    genericFirebase.unfollowing_relation(item, Profile.user, context);
                    holder.follow_status_search.setText("follow");
                    notifyDataSetChanged();
                }
                else{
                    genericFirebase.following_relation(item, Profile.user, context);
                    holder.follow_status_search.setText("following");
                    notifyDataSetChanged();
                }
            }
        });

    }

    boolean checkFollowing(User following, User follower, final Context context) {
        for (String name : follower.getFollowing())
            if (name.equals(following.getId()))
                return true;
        return false;
    }


    @Override
    public int getItemCount() {
        if (users == null || users.isEmpty())
            return 0;
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return usersfilter;
    }

    private Filter usersfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint != null && constraint.length() != 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : allusers) {
                    if (item.getName().toLowerCase().contains(filterPattern) && !Profile.user.getId().equals(item.getId())) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sr_name, sr_email;
        ConstraintLayout sr_user;
        CircleImage sr_img;
        Button follow_status_search;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            follow_status_search = itemView.findViewById(R.id.follow_status_search);
            sr_name = itemView.findViewById(R.id.sr_username);
            sr_email = itemView.findViewById(R.id.sr_email);
            sr_user = itemView.findViewById(R.id.sr_user);
            sr_img = itemView.findViewById(R.id.sr_img);

        }
    }
}



