package com.example.instaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.activity.MainActivity;
import com.example.instaapp.ui.utils.Constants;
import com.example.instaapp.ui.utils.Preferences_Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import static com.example.instaapp.Utils.USERS;
import static com.example.instaapp.genericFirebase.firebaseFirestore;
/**
 * Created by Abdo GHazi
 */
public class SplachScreen extends AppCompatActivity {
    private Preferences_Utils Prefer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);
//        SharedPreferences preferences = getSharedPreferences("Userapp", 0);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.remove("userId");
//        editor.commit();

        final String userId = getstring("userId");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userId != null) {
                    FirebaseFirestore firestore= FirebaseFirestore.getInstance();
                    firestore.collection(USERS).document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Profile.user=documentSnapshot.toObject(User.class);
                            Intent intent1 = new Intent(SplachScreen.this, MainActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    });
                } else {
                    Intent intent1 = new Intent(SplachScreen.this, LOGIN_IN.class);
                    startActivity(intent1);
                    finish();
                }

            }
        }, 2000);


    }

    public String getstring(String key) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("Userapp", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}
