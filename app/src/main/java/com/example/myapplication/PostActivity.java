package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.myapplication.SetupActivity.GALLLERY_PICK;

public class PostActivity extends AppCompatActivity {

  //private Toolbar mtoolbar;
  private FusedLocationProviderClient fusedLocationClient;
    private String savecurrentdate,savecurrenttime,randompostname;
    String downloadurl;
    private Uri imageUri;
  private ImageButton selectpostimage;
  private EditText post_title;
  private EditText post_description;
  private Button update_button;
    private String title;
    private StorageReference postimagerefrence;
    private DatabaseReference userreference,postref,postref2;
    private FirebaseAuth mauth;
    private String currentuserid;
    private String description;
    private ProgressDialog loadingbar;
    public long temp=0;
    private String userfullname,userprofileimage;
    Geocoder geocoder;
    List<Address> addresses;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
     //    mtoolbar=(Toolbar)findViewById(R.id.update_post_toolbar);
        loadingbar=new ProgressDialog(this);
         post_title=(EditText)findViewById(R.id.post_title);
         post_description=(EditText)findViewById(R.id.post_description);
         update_button=(Button)findViewById(R.id.update_button);
         selectpostimage=(ImageButton)findViewById(R.id.select_post_image);
         postimagerefrence= FirebaseStorage.getInstance().getReference();

         userreference= FirebaseDatabase.getInstance().getReference().child("users").child("normal");
         postref= FirebaseDatabase.getInstance().getReference().child("posts");

        geocoder = new Geocoder(this, Locale.getDefault());


        // postref2=FirebaseDatabase.getInstance().getReference().child("posts").child("post_likes");

        mauth=FirebaseAuth.getInstance();
         currentuserid=mauth.getCurrentUser().getUid();

        userreference.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("total_post"))
                    {
                        temp=(long)dataSnapshot.child("total_post").getValue();
                        userfullname=dataSnapshot.child("fullname").getValue().toString();
                        userprofileimage=dataSnapshot.child("profile_pic").getValue().toString();

                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         selectpostimage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 openGallery();
             }
         });

        //setSupportActionBar(mtoolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setTitle("Update Post");
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePostInfo();
            }
        });
    }

    private void validatePostInfo() {
        description=post_description.getText().toString();
        title=post_title.getText().toString();
        if(imageUri==null)
        {
            Toast.makeText(this,"Please select an image",Toast.LENGTH_SHORT).show();
        }
        else if(title.isEmpty())
        {
            Toast.makeText(this,"Please enter the post title",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Add new post");
            loadingbar.setMessage("Please wait while uploading post");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            uploadImageToSorage();
        }
    }

    private void uploadImageToSorage() {
        Calendar callfordate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MM-yyyy");
        savecurrentdate=currentdate.format(callfordate.getTime());

        Calendar callfortime=Calendar.getInstance();
        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currenttime.format(callfortime.getTime());

        randompostname=savecurrentdate+savecurrenttime;

        StorageReference filepath=postimagerefrence.child("Post Images").child(imageUri.getLastPathSegment()+randompostname+".jpg");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    Task<Uri> result= task.getResult().getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadurl=uri.toString();
                            savingPostInformationToDatabase(downloadurl);
                        }
                    });
                    Toast.makeText(PostActivity.this,"Post Uploaded Successfully",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(PostActivity.this,"Uploaded UnSuccessfully",Toast.LENGTH_SHORT).show();

                }

            }
        });
        
    }

    private void savingPostInformationToDatabase(final String downloadurl1) {
        final double[] postlatitude = new double[1];
        final double[] postlongitude = new double[1];


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(PostActivity.this);

        if (ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(PostActivity.this,"Inside if",Toast.LENGTH_SHORT).show();

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(PostActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            postlatitude[0] =location.getLatitude();
                            postlongitude[0] =location.getLongitude();
                            //Toast.makeText(PostActivity.this,"long:"+postlongitude[0]+" lat:"+postlatitude[0],Toast.LENGTH_SHORT).show();
                            try {
                                addresses = geocoder.getFromLocation(postlatitude[0], postlongitude[0], 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String address = addresses.get(0).getAddressLine(0);
                            String [] add=new String[6];
                            add=address.split(",");
                            Post newpost=new Post(currentuserid,savecurrentdate,savecurrenttime,title,
                                    userprofileimage,userfullname,description,downloadurl1, postlatitude[0], postlongitude[0],0,0,0,currentuserid + randompostname,"","","","","",add[2]+add[3]);
                            postref.child(currentuserid + randompostname).setValue(newpost).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful())
                                    {

                                        sendUserToMainPage();
                                        //Toast.makeText(PostActivity.this,downloadurl,Toast.LENGTH_SHORT).show();

                                    }
                                    else
                                    {
                                        Toast.makeText(PostActivity.this,"Error in uploading post",Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                }
                            });


                        }
                    }
                });


    }

    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLLERY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLLERY_PICK && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            selectpostimage.setImageURI(imageUri);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.home)
        {
            sendUserToMainPage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainPage() {

        userreference.child(currentuserid).child("total_post").setValue(++temp).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingbar.dismiss();
                startActivity(new Intent(PostActivity.this,MainPage.class));

            }
        });

    }

    private void updatecount(long temp) {

            temp++;





    }
}
