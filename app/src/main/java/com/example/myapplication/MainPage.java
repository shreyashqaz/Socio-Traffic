package com.example.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPage extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postlist;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userref,postref,postref2,reporteduserref;
    private CircleImageView navprofileimage;
    private String savecurrentdate,savecurrenttime;
    private TextView navusername,loctext,disttext;
    ImageButton refreshbutton;
    private FusedLocationProviderClient fusedLocationClient;
    final long[] flag1 = {-1};
    int d=3;
    SeekBar distbar;
    Geocoder geocoder;
    List<Address> addresses;

    String currentuserid;
    final double[] postlatitude = new double[1];
    final double[] postlongitude = new double[1];


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        drawerLayout=(DrawerLayout)findViewById(R.id.dlayout);
        navigationView=(NavigationView)findViewById(R.id.nvlayout);
        loctext=(TextView)findViewById(R.id.loctext);

        disttext=(TextView)findViewById(R.id.disttext);
        distbar=(SeekBar)findViewById(R.id.distancebar);
        refreshbutton=(ImageButton)findViewById(R.id.postrefresh);

        distbar.setMin(1);
        distbar.setMax(10);
        distbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                disttext.setText(""+progress);
                d=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DisplayAllUserPost();
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        currentuserid=firebaseAuth.getCurrentUser().getUid();

        postlist=(RecyclerView)findViewById(R.id.all_users_post_list);
        postlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);

        View navView=navigationView.inflateHeaderView(R.layout.navigation_header);
        navprofileimage=(CircleImageView)navView.findViewById(R.id.nav_profile_image);
        navusername=(TextView)navView.findViewById(R.id.nav_username);
        navusername.setText(currentuserid);


        userref= FirebaseDatabase.getInstance().getReference().child("users").child("normal");
        reporteduserref=FirebaseDatabase.getInstance().getReference().child("users").child("reported");
        postref=FirebaseDatabase.getInstance().getReference().child("posts");
        postref2=FirebaseDatabase.getInstance().getReference().child("post_response");

        geocoder = new Geocoder(this, Locale.getDefault());


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Inside if",Toast.LENGTH_SHORT).show();

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainPage.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    postlatitude[0] = location.getLatitude();
                                    postlongitude[0] = location.getLongitude();
                                    try {
                                        addresses = geocoder.getFromLocation(postlatitude[0], postlongitude[0], 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String address = addresses.get(0).getAddressLine(0);
                                    String [] add=new String[6];
                                    add=address.split(",");
                                    int size=add.length;
                                    String knownName = addresses.get(0).getFeatureName();
                                    if(knownName.isEmpty()) {
                                        loctext.setText(add[1]+add[2]);
                                    }
                                    else
                                    {
                                        loctext.setText(add[2]+add[3]);
                                    }
                                }
                            }
                        });
       // Toast.makeText(MainPage.this,"lat"+postlatitude+" long"+postlongitude,Toast.LENGTH_LONG).show();


        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayAllUserPost();
            }
        });

        userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {


                    if (dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        navusername.setText(fullname);

                    }

                    if (dataSnapshot.hasChild("profile_pic"))
                    {
                        String image = dataSnapshot.child("profile_pic").getValue().toString();
                        Glide.with(getApplicationContext()).load(image).into(navprofileimage);


                    }
                }
                else
                {
                    checkreported();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               userMenuSelecter(menuItem);
                return false;
            }
        });
        DisplayAllUserPost();
    }

    private void checkreported() {
        reporteduserref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {Toast.makeText(MainPage.this,"User Deactivated Contact Admin",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();}
                else
                {
                    Toast.makeText(MainPage.this,"Not a valid user",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void DisplayAllUserPost() {

        FirebaseRecyclerOptions<Post> options= new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postref,Post.class).build();

        FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, PostViewHolder>
                        (options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull final Post model) {
                        final long[] temp = {-1};
                        final long[] temp2 = {-1};
                        final long[] temp3 = {-1};
                        final int[] flag = {-1};
                        final UserMap[] owner = new UserMap[1];
                        holder.setPost_title(model.getPost_title());
                        holder.setPostloc(model.getPostloc());
                        holder.setPost_date(model.getPost_date());
                        holder.setPost_description(model.getPost_description());
                        holder.setPost_time(model.getPost_time());
                        holder.setUser_full_name(model.getUser_full_name());
                        holder.setPost_image(getApplicationContext(), model.getPost_image());
                        holder.setUser_profile_image(getApplicationContext(), model.getUser_profile_image());
                        holder.setLikes(model.getLikes());
                        holder.setDislikes(model.getDislikes());
                        holder.setReports(model.getReports());
                        //postref2=postref.child(model.getPostid()).child("post_likes");
                        postref2.child(model.getPostid()).child("post_id").setValue(currentuserid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        postref2.child(model.getPostid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    if(dataSnapshot.hasChild(currentuserid))
                                    {
                                        flag[0] =1;
                                    }
                                    else {
                                        flag[0] =0;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        userref.child(model.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    if(dataSnapshot.hasChild("like_post"))
                                    {
                                        temp[0] =(long)dataSnapshot.child("like_post").getValue();
                                    }
                                    if(dataSnapshot.hasChild("dislike_post"))
                                    {
                                        temp2[0] =(long)dataSnapshot.child("dislike_post").getValue();
                                    }
                                    if(dataSnapshot.hasChild("reported"))
                                    {
                                        temp3[0] =(long)dataSnapshot.child("reported").getValue();
                                    }
                                    owner[0] =dataSnapshot.getValue(UserMap.class);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //holder.setDislikes(model.getDislikes());

                        double dist = caldistance(postlatitude[0], postlongitude[0], model.getPost_lat(), model.getPost_long());
                        //Toast.makeText(MainPage.this,"dist="+dist, Toast.LENGTH_LONG).show();
                        if(dist>d){holder.rlpost.setVisibility(View.INVISIBLE);}

                        Calendar callfordate = Calendar.getInstance();
                        final SimpleDateFormat currentdate = new SimpleDateFormat("dd-MM-yyyy");
                        savecurrentdate = currentdate.format(callfordate.getTime());

                        Calendar callfortime = Calendar.getInstance();
                        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm");
                        savecurrenttime = currenttime.format(callfortime.getTime());

                        Date date1 = callfortime.getTime();
                        Date date2 = null;
                        try {
                            date2 = currenttime.parse(model.getPost_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date3 = callfordate.getTime();
                        Date date4 = null;
                        try {
                            date4 = currentdate.parse(model.getPost_date());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long diff = date1.getTime() - date2.getTime();
                        long days = (int) (diff / (1000 * 60 * 60 * 24));
                        long hours = (int) ((diff - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                        long min = (int) (diff - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                        hours = (hours < 0 ? -hours : hours);
                        min = (min < 0 ? -min : min);
                       // Toast.makeText(MainPage.this, "diff in hr"+hours, Toast.LENGTH_LONG).show();

                        if (hours >0) {

                            postref2.child(model.getPostid()).setValue(null);
                            postref.child(model.getPostid()).setValue(null);


                        }
                        holder.image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent startprofileview=new Intent(getApplicationContext(),UserProfileView.class);
                                startprofileview.putExtra("userid",model.getUid());
                                startActivity(startprofileview);
                            }
                        });

                        holder.post_like_button.setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View v) {
                                likeopt();
                                }

                            private void likeopt() {

                                int likeinit = model.getLikes();
                                likeinit++;
                                if(currentuserid.equals(model.getUid())){Toast.makeText(MainPage.this, "Cannot like your own post", Toast.LENGTH_LONG).show(); }
                                else {
                                    if(flag[0] ==0){


                                        postref.child(model.getPostid()).child("likes").setValue(likeinit).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                                        holder.setLikes(model.getLikes());
                                        postref2.child(model.getPostid()).child(currentuserid).setValue(currentuserid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(MainPage.this, "Liked", Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                                        if(!(temp[0] <0)){
                                            userref.child(model.getUid()).child("like_post").setValue(++temp[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });}



                                    }
                                    else if(flag[0] ==1) {
                                        Toast.makeText(MainPage.this, "Already Responded", Toast.LENGTH_LONG).show();
                                    }}
                            }
                        });
                        holder.post_dislike_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int dislikeinit = model.getDislikes();
                                dislikeinit++;
                                if(currentuserid.equals(model.getUid())){Toast.makeText(MainPage.this, "Cannot like your own post", Toast.LENGTH_LONG).show(); }
                                else {
                                    if(flag[0] ==0){


                                        postref.child(model.getPostid()).child("dislikes").setValue(dislikeinit).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                                        holder.setDislikes(model.getDislikes());
                                        postref2.child(model.getPostid()).child(currentuserid).setValue(currentuserid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(MainPage.this, "Dis-Liked", Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                                        if(!(temp2[0] <0)){

                                            userref.child(model.getUid()).child("dislike_post").setValue(++temp2[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });}



                                    }
                                    else if(flag[0] ==1) {
                                        Toast.makeText(MainPage.this, "Already Responded", Toast.LENGTH_LONG).show();
                                    }}
                            }
                        });
                        holder.post_del_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentuserid.equals(model.getUid())) {

                                    postref2.child(model.getPostid()).setValue(null);
                                    postref.child(model.getPostid()).setValue(null);



                                } else {
                                    Toast.makeText(MainPage.this, "This Post does not belong to you", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        holder.post_report_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int reportinit = model.getReports();
                                if(currentuserid.equals(model.getUid())){Toast.makeText(MainPage.this, "Cannot report own post", Toast.LENGTH_LONG).show(); }
                                else {
                                    holder.rlpost.setVisibility(View.INVISIBLE);
                                    switch (reportinit) {
                                        case 0:
                                            reportinit++;
                                            postref.child(model.getPostid()).child("reports").setValue(reportinit);
                                            postref.child(model.getPostid()).child("rid1").setValue(currentuserid);
                                            holder.setReports(model.getReports());
                                            Toast.makeText(MainPage.this, "Reported", Toast.LENGTH_LONG).show();
                                            break;
                                        case 1:
                                            if (currentuserid.equals(model.getRid1())) {
                                                Toast.makeText(MainPage.this, "Already Reported", Toast.LENGTH_LONG).show();
                                            } else {
                                                reportinit++;
                                                postref.child(model.getPostid()).child("reports").setValue(reportinit);
                                                postref.child(model.getPostid()).child("rid2").setValue(currentuserid);
                                                holder.setReports(model.getReports());
                                                Toast.makeText(MainPage.this, "Reported", Toast.LENGTH_LONG).show();
                                            }
                                            break;
                                        case 2:
                                            if ((currentuserid.equals(model.getRid1())) || (currentuserid.equals(model.getRid2()))) {
                                                Toast.makeText(MainPage.this, "Already Reported", Toast.LENGTH_LONG).show();
                                            } else {
                                                reportinit++;
                                                Toast.makeText(MainPage.this, "Reported", Toast.LENGTH_LONG).show();
                                                if (reportinit > 2) {
                                                    if(!(temp3[0] <0)){

                                                        userref.child(model.getUid()).child("reported").setValue(++temp3[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                            }
                                                        });
                                                        if(temp3[0]>4)
                                                        {
                                                            reporteduserref.child(model.getUid()).setValue(owner[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            });
                                                            userref.child(model.getUid()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                }
                                                            });
                                                        }
                                                    }

                                                    postref2.child(model.getPostid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });
                                                    postref.child(model.getPostid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });


                                                }
                                                Toast.makeText(MainPage.this, "Post Removed", Toast.LENGTH_LONG).show();

                                            }
                                            break;
                                    }
                                }




                            }
                        });

                    }

                    private double caldistance(double lat1, double lon1, double lat2, double lon2) {
                        double R = 6371; // Radius of the earth in km
                        double dLat = deg2rad(lat2-lat1);  // deg2rad below
                        double dLon = deg2rad(lon2-lon1);
                        double a =
                                Math.sin(dLat/2) * Math.sin(dLat/2) +
                                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                                Math.sin(dLon/2) * Math.sin(dLon/2)
                                ;
                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                        double d = R * c; // Distance in km
                        return d;

                    }

                    private double deg2rad(double deg) {
                        return deg * (Math.PI/180);
                    }


                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout,viewGroup,false);
                        PostViewHolder viewHolder=new PostViewHolder(view);
                        return viewHolder;
                    }
                };
        postlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        ImageButton post_like_button;
        ImageButton post_dislike_button;
        ImageButton post_del_button;
        ImageButton post_report_button;
        TextView postlike,postdislike,postreports;
        int likeinit=0,dislikeinit=0,reportinit=0;
        RelativeLayout rlpost;
        CircleImageView image;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            postlike=(TextView)mview.findViewById(R.id.post_like_count);
            postdislike=(TextView)mview.findViewById(R.id.post_dislike_count);
            postreports=(TextView)mview.findViewById(R.id.post_report_count);
            post_del_button=(ImageButton)mview.findViewById(R.id.post_del_button);
            post_like_button=(ImageButton)mview.findViewById(R.id.post_like_button);
            post_dislike_button=(ImageButton)mview.findViewById(R.id.post_dislike_button);
            post_report_button=(ImageButton)mview.findViewById(R.id.post_report_button);
            rlpost=(RelativeLayout)mview.findViewById(R.id.rlpost);
            image=(CircleImageView)mview.findViewById(R.id.post_profile_image);


        }

        public void setUser_full_name(String user_full_name)
        {
            TextView userfullname=(TextView)mview.findViewById(R.id.post_user_name);
            userfullname.setText(user_full_name);
        }
        public void setUser_profile_image(Context ctx,String user_profile_image)
        {
            Glide.with(ctx).load(user_profile_image).into(image);
        }
        public void setPost_time(String post_time)
        {
            TextView posttime=(TextView)mview.findViewById(R.id.post_time);
            posttime.setText("    "+post_time);
        }
        public void setPost_date(String post_date)
        {
            TextView postdate=(TextView)mview.findViewById(R.id.post_date);
            postdate.setText("    "+post_date);
        }
        public void setPostloc(String post_loc) {
            TextView postloc=(TextView)mview.findViewById(R.id.postloc);
            postloc.setText(post_loc);
        }
        public void setPost_title(String post_title)
        {
            TextView posttitle=(TextView)mview.findViewById(R.id.post_title);
            posttitle.setText(post_title);
        }
        public void setPost_image(Context ctx,String post_image)
        {
            ImageView image=(ImageView)mview.findViewById(R.id.post_image);
            Glide.with(ctx).load(post_image).into(image);
        }
        public void setPost_description(String post_description)
        {
            TextView postdesc=(TextView)mview.findViewById(R.id.post_description);
            postdesc.setText(post_description);
        }
        public void setLikes(int likes)
        {
            postlike.setText(String.valueOf(likes));
            likeinit=likes;
        }
        public void setDislikes(int dislikes)
        {
            postdislike.setText(String.valueOf(dislikes));
            dislikeinit=dislikes;
        }
        public void setReports(int reports)
        {
            postreports.setText(String.valueOf(reports));
            reportinit=reports;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=firebaseAuth.getCurrentUser();
        if(currentuser==null)
        {
            sendUsertoLogIn();
        }
        else
        {
           //checkUserExistence();
        }
    }

 /*   private void checkUserExistence() {
        final String currentuserid=firebaseAuth.getCurrentUser().getUid();
        userref= FirebaseDatabase.getInstance().getReference().child("Users");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentuserid))
                {
                   // startActivity(new Intent(MainPage.this,SetupActivity.class));
                  //  finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }*/

    private void sendUsertoLogIn() {
        Intent loginIntent=new Intent(MainPage.this,Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void userMenuSelecter(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_profile:

                startActivity(new Intent(MainPage.this,UserProfileActivity.class));
                break;
            case R.id.nav_feed:
                Toast.makeText(this,"Live Feed",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_post:
                sendUserToPostActivity();
                Toast.makeText(this,"Add Post",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_emergency:
                startActivity(new Intent(MainPage.this,AboutUsActivity.class));                break;
            case R.id.nav_setting:
                startActivity(new Intent(MainPage.this,FAQActivity.class));                  break;
            case R.id.nav_logout:
               firebaseAuth.signOut();
               sendUsertoLogIn();

                break;

        }
        }

    private void sendUserToPostActivity() {

        Intent addpostintent =new Intent(MainPage.this,PostActivity.class);
        startActivity(addpostintent);
    }
}

