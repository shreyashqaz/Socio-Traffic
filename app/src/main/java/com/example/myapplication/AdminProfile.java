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

public class AdminProfile extends AppCompatActivity {

    private TextView user_fullname,username,email,gender,mobile;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userref;
    private String currentuserid;
    private CircleImageView user_profile;
    private Button btupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        user_fullname=(TextView)findViewById(R.id.user_full_name1);
        user_profile=(CircleImageView)findViewById((R.id.user_profile_image1));
        username=(TextView)findViewById(R.id.user_usernam1);
        email=(TextView)findViewById(R.id.user_email1);
        gender=(TextView)findViewById(R.id.user_gender1);
        mobile=(TextView)findViewById(R.id.user_mobile1);
        btupdate=(Button)findViewById(R.id.btupdate1);
        btupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent setupactivity=new Intent(getApplicationContext(),SetupActivity.class);
                setupactivity.putExtra("flag",1);
                startActivity(setupactivity);

            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        userref= FirebaseDatabase.getInstance().getReference().child("admin");

        currentuserid=firebaseAuth.getCurrentUser().getUid();

        email.setText(firebaseAuth.getCurrentUser().getEmail());
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

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
