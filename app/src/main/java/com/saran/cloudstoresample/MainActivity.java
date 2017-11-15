package com.saran.cloudstoresample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 100;
    Button profile_btn, add_profile_btn, update_profile_btn, uploadimage;
    EditText et_name, et_email, et_phnum;
    FirebaseFirestore firestore_db;
    String TAG = "Firestore_D";
    String id = "MdQ3qQb300fnvmr20lDH";
    ImageView profilepic;
    private Uri selectedImageUri;
    private String selectedImagePath;
    StorageReference storageRef;
    FirebaseStorage firebase_storage;
    String profilepicUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_btn = (Button) findViewById(R.id.id_profile_btn);
        add_profile_btn = (Button) findViewById(R.id.id_add_profile_btn);
        update_profile_btn = (Button) findViewById(R.id.id_update_profile_btn);
        profilepic = (ImageView) findViewById(R.id.id_profile_pic);
        uploadimage = (Button) findViewById(R.id.upload_image_btn);

        et_name = (EditText) findViewById(R.id.id_et_name);
        et_email = (EditText) findViewById(R.id.id_et_email);
        et_phnum = (EditText) findViewById(R.id.id_et_phonenumber);

        firestore_db = FirebaseFirestore.getInstance();
        firebase_storage = FirebaseStorage.getInstance();
        storageRef = firebase_storage.getReference().child("Player/team1/p1");


        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getMyProfile();
            }
        });
        add_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addMyProfile();
            }
        });
        update_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateMyProfile();
            }
        });
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectimage();
            }
        });

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimageStorage();
            }
        });

    }

    private void uploadimageStorage() {


//        storageRef.getParent().child("images/saranr689.jpg");
        UploadTask uploadTask = storageRef.putFile(selectedImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "onSuccess: " + downloadUrl);
            }
        });

    }

    private void selectimage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                selectedImagePath = selectedImageUri.toString();
                Picasso.with(MainActivity.this)
                        .load(selectedImageUri)
                        .placeholder(R.mipmap.ic_launcher).
                        into(profilepic);
            }
        }
    }


    private void getMyProfile() {

        firestore_db.collection("GetProfile").document("saranr689@gmail.com").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    et_name.setText(task.getResult().getString("name") + " ");
                    et_email.setText(task.getResult().getString("email") + " ");
                    et_phnum.setText(task.getResult().getString("phonenumber") + " ");
                    profilepicUrl = task.getResult().getString("profilepic");

                    Picasso.with(MainActivity.this).load(profilepicUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(profilepic);
                    Picasso.with(MainActivity.this).setLoggingEnabled(true);
                }
            }
        });

    }

    private void updateMyProfile() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", et_name.getText().toString());
        user.put("email", et_email.getText().toString());
        user.put("phonenumber", et_phnum.getText().toString());
        user.put("profilepic", profilepicUrl);
        firestore_db.collection("GetProfile").document("saranr689@gmail.com").update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: update successfull");
                } else {
                    Log.d(TAG, "onComplete: update fail" + task.getException());

                }
            }
        });
    }

    private void addMyProfile() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", et_name.getText().toString());
        user.put("email", et_email.getText().toString());
        user.put("phonenumber", et_phnum.getText().toString());
        firestore_db.collection("Profile").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: " + documentReference.getId());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error adding document", e);

            }
        });
    }

    private void getAllProfile() {

        firestore_db.collection("Profile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (int i = 0; i < documents.size(); i++) {
                        Log.d(TAG, "onComplete: ID:" + documents.get(i).getId());
                        Log.d(TAG, "onComplete: Name:" + documents.get(i).getString("name"));
                        Log.d(TAG, "onComplete: email:" + documents.get(i).getString("email"));
                        Log.d(TAG, "onComplete: phonenumer:" + documents.get(i).getString("phonenumber"));

                    }
                    id = documents.get(0).getId();

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Failure getting documents.", e);

            }
        });
    }
}
