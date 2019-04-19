package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileView extends AppCompatActivity {

    private TextView user_fullname,total_post,like_post,dislike_post,username,email,gender,mobile;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userref;
    private String currentuserid;
    private CircleImageView user_profile;
    private int flag=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        user_fullname=(TextView)findViewById(R.id.user_full_name2);
        user_profile=(CircleImageView)findViewById((R.id.user_profile_image2));
        total_post=(TextView)findViewById(R.id.user_total_post2);
        like_post=(TextView)findViewById(R.id.user_like_post2);
        dislike_post=(TextView)findViewById(R.id.user_dislike_post2);
        username=(TextView)findViewById(R.id.user_username2);
        email=(TextView)findViewById(R.id.user_email2);
        gender=(TextView)findViewById(R.id.user_gender2);
        mobile=(TextView)findViewById(R.id.user_mobile2);


        flag=getIntent().getIntExtra("flag",1);
        firebaseAuth=FirebaseAuth.getInstance();
        if(flag==1) {
            userref = FirebaseDatabase.getInstance().getReference().child("users").child("normal");
        }
        else if(flag==2)
        {
            userref = FirebaseDatabase.getInstance().getReference().child("users").child("reported");

        }

        currentuserid=getIntent().getStringExtra("userid");


        //email.setText(firebaseAuth.getCurrentUser().getEmail());
        userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("fullname"))
                    {
                        user_fullname.setText(dataSnapshot.child("fullname").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("username"))
                    {
                        username.setText(dataSnapshot.child("username").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("mobile"))
                    {
                        mobile.setText(dataSnapshot.child("mobile").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("gender"))
                    {
                        gender.setText(dataSnapshot.child("gender").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("profile_pic"))
                    {
                        String imageurl=dataSnapshot.child("profile_pic").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageurl).into(user_profile);
                    }
                    if (dataSnapshot.hasChild("total_post"))
                    {
                        total_post.setText(dataSnapshot.child("total_post").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("like_post"))
                    {
                        like_post.setText(dataSnapshot.child("like_post").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("dislike_post"))
                    {
                        dislike_post.setText(dataSnapshot.child("dislike_post").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("email"))
                    {
                        email.setText(dataSnapshot.child("email").getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
