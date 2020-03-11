package com.example.instaapp.ui.activity;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.instaapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abdo GHazi
 */
public class BaseActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView ivLogo;

    private MenuItem inboxMenuItem;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar =findViewById(R.id.toolbar);
        bindViews();
    }

    protected void bindViews() {
        setupToolbar();
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
