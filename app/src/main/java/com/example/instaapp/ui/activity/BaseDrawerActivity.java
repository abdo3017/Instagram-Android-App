package com.example.instaapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.instaapp.LOGIN_IN;
import com.example.instaapp.R;
import com.example.instaapp.SplachScreen;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.utils.CircleTransformation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.instaapp.Utils.USERS;

/**
 * Created by Abdo GHazi
 */
public class BaseDrawerActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    int avatarSize;

    private CircleImage ivMenuUserProfilePhoto;
    NavigationView vNavigation;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        avatarSize = 64;
        drawerLayout = findViewById(R.id.drawerLayout);
        dialog = new ProgressDialog(this);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);

        NavigationView navigationView = findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(this);


        bindViews();
        setupHeader();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    TextView drawer_user_name;

    private void setupHeader() {
        vNavigation = findViewById(R.id.vNavigation);
        final View headerView = vNavigation.getHeaderView(0);
        drawer_user_name = headerView.findViewById(R.id.drawer_user_name);
        ivMenuUserProfilePhoto = headerView.findViewById(R.id.ivMenuUserProfilePhoto);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(USERS).document(Profile.user.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                Profile.user = documentSnapshot.toObject(User.class);
                Glide.with(BaseDrawerActivity.this)
                        .load(Profile.user.getImage())
                        .into(ivMenuUserProfilePhoto);
                drawer_user_name.setText(Profile.user.getName());
                headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserProfileActivity.user = documentSnapshot.toObject(User.class);
                        UserProfileActivity.check = true;
                        onGlobalMenuHeaderClick(v);
                    }
                });

            }
        });

    }

    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }

    ProgressDialog dialog;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_feed:
                startActivity(new Intent(BaseDrawerActivity.this, MainActivity.class));
                break;
            case R.id.menu_edit:
                startActivity(new Intent(BaseDrawerActivity.this, EditProfileActivity.class));
                break;
            case R.id.menu_about:
                dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.gear_duo));
                dialog.setMessage("Logout.....");
                dialog.show();
                SharedPreferences preferences = getSharedPreferences("Userapp", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("userId").commit();
                Intent intent1 = new Intent(BaseDrawerActivity.this, LOGIN_IN.class);
                startActivity(intent1);
                finish();
                dialog.dismiss();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
