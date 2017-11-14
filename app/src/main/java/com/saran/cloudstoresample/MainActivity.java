package com.saran.cloudstoresample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button profile_btn,add_profile_btn,update_profile_btn;
    FirebaseFirestore firestore_db;
    String TAG = "Firestore_D";
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profile_btn =(Button)findViewById(R.id.id_profile_btn);
        add_profile_btn =(Button)findViewById(R.id.id_add_profile_btn);
        update_profile_btn =(Button)findViewById(R.id.id_update_profile_btn);
        firestore_db = FirebaseFirestore.getInstance();

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
    }

    private void updateMyProfile() {
        firestore_db.collection("Profile").document(id).update("name","sridhar").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: update successfull");
                }else {
                    Log.d(TAG, "onComplete: update fail"+task.getException());

                }
            }
        });
    }

    private void addMyProfile() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "venkatesh");
        user.put("email", "venkatesh214@gmail.com");
        user.put("phonenumber", "9677321890");
        firestore_db.collection("Profile").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: "+documentReference.getId());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error adding document", e);

            }
        });
    }

    private void getMyProfile() {

        firestore_db.collection("Profile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful())
                {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (int i =0; i<documents.size();i++)
                    {
                        Log.d(TAG, "onComplete: ID:"+documents.get(i).getId());
                        Log.d(TAG, "onComplete: Name:"+documents.get(i).getString("name"));
                        Log.d(TAG, "onComplete: email:"+documents.get(i).getString("email"));
                        Log.d(TAG, "onComplete: phonenumer:"+documents.get(i).getString("phonenumber"));

                    }
                    id = documents.get(0).getId();

                }else {
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
