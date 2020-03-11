package com.example.instaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.ui.activity.MainActivity;
import com.example.instaapp.ui.utils.Preferences_Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.instaapp.Utils.USERS;
/**
 * Created by Abdo GHazi
 */
public class LOGIN_IN extends AppCompatActivity {


    EditText Email;
    EditText Password;
    FirebaseAuth auth;
    FirebaseUser user;
    User currentuser;
    Preferences_Utils preferencesUtils;
    FirebaseFirestore firestore;
    DocumentReference reference;
    EditText message;
    ProgressDialog progressDialog;
    private boolean ShowPassword = true;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = getSharedPreferences("Userapp", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("userId");
        editor.commit();
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        preferencesUtils = new Preferences_Utils();
        message = findViewById(R.id.message);
        progressDialog = new ProgressDialog(this);

    }

    public void savestring(String key, String value) {
        SharedPreferences.Editor editor =
                getSharedPreferences("Userapp", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void Login(View view) {
        if (TextUtils.isEmpty(Email.getText().toString())) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Password.getText().toString())) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        } else {
            String E = Email.getText().toString();
            String P = Password.getText().toString();
            progressDialog.setTitle("Signed in");
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            auth.signInWithEmailAndPassword(E, P)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = auth.getCurrentUser();
                                reference = firestore.collection(USERS).document(user.getEmail());
                                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if ((User) documentSnapshot.toObject(User.class) != null) {
                                            currentuser = documentSnapshot.toObject(User.class);
                                            Profile.user = new User(currentuser);
                                            savestring("userId", currentuser.getEmail());
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(LOGIN_IN.this, MainActivity.class);
                                            startActivity(intent);
                                            EmptyField();
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LOGIN_IN.this, "Invalid email,must sign up first", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LOGIN_IN.this, "Invalid email,must sign up first", Toast.LENGTH_LONG).show();
                }
            });


        }

    }

    public void EmptyField() {
        Email.setText("");
        Password.setText("");
    }


    public void Sign_UP(View view) {
        Intent intent = new Intent(LOGIN_IN.this, SIGN_UP.class);
        startActivity(intent);
        finish();
    }

    public void SHOW_PASSWORD(View view) {


        if (ShowPassword) {
            Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ShowPassword = false;
        } else {
            Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ShowPassword = true;
        }
    }
}
