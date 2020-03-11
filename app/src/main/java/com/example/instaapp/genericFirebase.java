package com.example.instaapp;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instaapp.models.Comment;
import com.example.instaapp.models.Post;
import com.example.instaapp.models.Profile;
import com.example.instaapp.models.User;
import com.example.instaapp.models.likes;
import com.example.instaapp.ui.activity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.example.instaapp.Utils.POSTS;
import static com.example.instaapp.Utils.USERS;
/**
 * Created by Abdo GHazi
 */
public class genericFirebase {
    static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    static StorageReference storageReference;

    private static String getFileExtension(final Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static void upLoadPost(final Uri uri, final Post obj, final String user_id, final Context context) {
        storageReference = FirebaseStorage.getInstance().getReference(POSTS);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(POSTS).add(obj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                obj.setPostId(documentReference.getId());
                final StorageReference fileReference = storageReference.child(documentReference.getId()
                        + "." + getFileExtension(uri, context));
                fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                obj.setImage(uri.toString());
                                firebaseFirestore.collection(POSTS).document(documentReference.getId()).set(obj);
                                Profile.user.getPosts().add(documentReference.getId());
                                firebaseFirestore.collection(USERS).document(Profile.user.getEmail()).set(Profile.user);

                            }
                        });
                    }
                });
            }

        });

    }

    public static void unfollowing_relation(final User following, final User follower, final Context context) {
        if (follower.getFollowing() != null) {
            follower.getFollowing().remove(following.getId());
        }
        Profile.user = new User(follower);
        firebaseFirestore.collection(USERS).document(follower.getId()).set(follower);
        if (following.getFollowers() != null) {
            following.getFollowers().remove(follower.getId());
        }
        firebaseFirestore.collection(USERS).document(following.getId()).set(following);
    }

    public static void following_relation(final User following, final User follower, final Context context) {
        if (follower.getFollowing() != null) {
            follower.getFollowing().add(following.getId());
        } else {
            List<String> followinglist = new ArrayList<String>();
            followinglist.add(following.getId());
            follower.setFollowing(followinglist);
        }
        Profile.user = new User(follower);
        firebaseFirestore.collection(USERS).document(follower.getId()).set(follower);
        if (following.getFollowers() != null) {
            following.getFollowers().add(follower.getId());
        } else {
            List<String> followerlist = new ArrayList<String>();
            followerlist.add(follower.getId());
            following.setFollowers(followerlist);
        }
        firebaseFirestore.collection(USERS).document(following.getId()).set(following);
    }

    public static void profile(String doc_id, final Context context) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(USERS).document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final User user = documentSnapshot.toObject(User.class);
                firebaseFirestore.collection(POSTS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<Post> postslist = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                            if (user.getPosts().contains(documentSnapshot1.getId())) {
                                postslist.add(documentSnapshot1.toObject(Post.class));
                            }
                        }
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "error" + e, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static void getPosts(String userId, final Context context) {
        MainActivity.postList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(USERS).document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                firebaseFirestore.collection(POSTS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (String follow : documentSnapshot.toObject(User.class).getFollowing()) {
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                Post post = documentSnapshot1.toObject(Post.class);
                                if (post.getUserId().equals(follow)) {
                                    MainActivity.postList.add(post);
                                }

                            }
                        }
                    }
                });
            }
        });
        Collections.sort(MainActivity.postList);
    }

    public static boolean checkFollowing(final String following, final String follower, final Context context) {
        final boolean[] check = new boolean[1];
        firebaseFirestore.collection(USERS).document(follower).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User follower_user = documentSnapshot.toObject(User.class);
                firebaseFirestore.collection(USERS).document(following).set(follower_user);
                for (String name : follower_user.getFollowing()) {
                    if (name.equals(following)) {
                        check[0] = true;
                        break;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "error" + e, Toast.LENGTH_SHORT).show();
            }
        });
        return check[0];
    }
}