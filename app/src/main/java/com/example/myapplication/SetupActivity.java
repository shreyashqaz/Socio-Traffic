package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText setup_username,setup_fullname,setup_gender,setup_mobile;
    private Button btsave;
    private CircleImageView profileimage;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;
    private DatabaseReference userreference;
    String currentuserid;
    final static int GALLLERY_PICK =1;
    private StorageReference userprofileref;
    final int[] flag = {0};
    UserMap user;
    String downloadurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setup_username=(EditText)findViewById(R.id.setup_username);
        setup_fullname=(EditText)findViewById(R.id.setup_fullname);
        setup_gender=(EditText)findViewById(R.id.setup_gender);
        setup_mobile=(EditText)findViewById(R.id.setup_mobile);

        flag[0]=getIntent().getIntExtra("flag",0);

        btsave=(Button)findViewById(R.id.btsave);
        profileimage=(CircleImageView)findViewById(R.id.setup_profileimage);
        pd=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();
        currentuserid=firebaseAuth.getCurrentUser().getUid();
        if(flag[0]==0) {
            userreference = FirebaseDatabase.getInstance().getReference().child("users").child("normal").child(currentuserid);
        }
        else if(flag[0]==1)
        {
            userreference = FirebaseDatabase.getInstance().getReference().child("admin").child(currentuserid);
        }

        userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    user=dataSnapshot.getValue(UserMap.class);
                    setup_fullname.setText(user.getFullname());
                    setup_gender.setText(user.getGender());
                    setup_mobile.setText(user.getMobile());
                    setup_username.setText(user.getUsername());
                    downloadurl=user.getProfile_pic();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userprofileref= FirebaseStorage.getInstance().getReference().child("Profile Images");

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLLERY_PICK);
            }
        });

        btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountSetUpInfo();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLLERY_PICK && data!=null)
        {
            Uri imageuri=data.getData();
            StorageReference filepath=userprofileref.child(currentuserid+".jpg");
            profileimage.setImageURI(imageuri);
            filepath.putFile(imageuri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this,"Profile Upload Succesfull",Toast.LENGTH_SHORT).show();

                            Task<Uri> result= task.getResult().getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadurl=uri.toString();
                                }
                            });

                            //Picasso.get().load(downloadurl).placeholder(R.drawable.profile).into(profileimage);



                           /* pr.setValue(downloadurl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                               // startActivity(new Intent( SetupActivity.this,SetupActivity.class));
                                                Toast.makeText(SetupActivity.this,"Added to database",Toast.LENGTH_SHORT).show();


                                            }
                                            else
                                            {            Toast.makeText(SetupActivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });*/
                        }
                        }
                    });





        }

        }


    private void saveAccountSetUpInfo() {
        String username=setup_username.getText().toString();
        String fullname=setup_fullname.getText().toString();
        String gender=setup_gender.getText().toString();
        String mobile=setup_mobile.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please enter username",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this,"Please enter Full Name",Toast.LENGTH_SHORT).show();
            return;
        }
      if( (!gender.equalsIgnoreCase("Male"))&&(!gender.equalsIgnoreCase("Female")))

        {
            Toast.makeText(this,"Please verify gender",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mobile))
        {
            Toast.makeText(this,"Please enter Mobile",Toast.LENGTH_SHORT).show();
            return;
        }
        String email=firebaseAuth.getCurrentUser().getEmail().toString();

           UserMap um= new UserMap(username,fullname,mobile,"Active",gender,downloadurl,currentuserid,0,0,0,0,email);

           pd.setMessage("Saving Information");
           pd.show();
           userreference.setValue(um)
                   .addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        pd.dismiss();
                        Toast.makeText(SetupActivity.this, "Your account is created Successfully", Toast.LENGTH_SHORT).show();
                        if (flag[0] == 0) {
                            startActivity(new Intent(SetupActivity.this, MainPage.class));
                            finish();
                        }
                        else if(flag[0]==1){
                            startActivity(new Intent(SetupActivity.this, AdminActivity.class));
                            finish();
                        }


                    }
                    else {
                        pd.dismiss();
                        Toast.makeText(SetupActivity.this,"Problem in account Creation",Toast.LENGTH_SHORT).show();
                    }

                }
            });

       }

    }

