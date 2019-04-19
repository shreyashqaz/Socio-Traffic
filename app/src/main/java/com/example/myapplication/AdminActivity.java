package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {


    RadioButton radioButton;
    private Button[] btn = new Button[2];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btn0, R.id.btn1};
    private RecyclerView userlist;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userref,mainref,normaluserref,reporteduserref;
    private String currentuserid;
    private ImageButton logout,adminprofile;
    int flag=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        firebaseAuth=FirebaseAuth.getInstance();
        currentuserid=firebaseAuth.getCurrentUser().getUid();

        normaluserref=FirebaseDatabase.getInstance().getReference().child("users").child("normal");
        reporteduserref=FirebaseDatabase.getInstance().getReference().child("users").child("reported");


        userref= FirebaseDatabase.getInstance().getReference().child("users").child("normal");
        mainref= FirebaseDatabase.getInstance().getReference().child("admin");
        mainref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(!(dataSnapshot.hasChild(currentuserid)))
                    {
                        Toast.makeText(getApplicationContext(),"Not an Admin",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logout=(ImageButton)findViewById(R.id.admin_logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                sendUsertoLogIn();
                finish();
            }
        });
        adminprofile=(ImageButton)findViewById(R.id.admin_profile_button);
        adminprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AdminProfile.class));
            }
        });
        userlist=(RecyclerView)findViewById(R.id.user_list);
        userlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userlist.setLayoutManager(linearLayoutManager);

        for(int i = 0; i < btn.length; i++){
            btn[i] = (Button) findViewById(btn_id[i]);
           // btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        btn_unfocus = btn[0];


        DisplayAllUser();


    }


    private void sendUsertoLogIn() {
        Intent loginIntent=new Intent(AdminActivity.this,Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void DisplayAllUser() {

        FirebaseRecyclerOptions<UserMap> options= new FirebaseRecyclerOptions.Builder<UserMap>()
                .setQuery(userref,UserMap.class).build();
        FirebaseRecyclerAdapter<UserMap,UserViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<UserMap, UserViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final UserMap model) {

                        holder.setFullname(model.getFullname());
                        holder.setProfile_pic(getApplicationContext(),model.getProfile_pic());
                        if(flag==1)
                        {
                            holder.rbactive.setChecked(true);
                        }
                        if(flag==2)
                        {
                            holder.rbdeactive.setChecked(true);
                        }
                        holder.image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent startprofileview=new Intent(getApplicationContext(),UserProfileView.class);
                                startprofileview.putExtra("userid",model.getUid());
                                startprofileview.putExtra("flag",flag);
                                startActivity(startprofileview);
                            }
                        });
                        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId)
                                {
                                    case R.id.rbactive:
                                                              normaluserref.child(model.getUid()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                             @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                        reporteduserref.child(model.getUid()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                            }
                                                        });
                                        break;
                                    case R.id.rbdeactive:
                                                            reporteduserref.child(model.getUid()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            });
                                                            normaluserref.child(model.getUid()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                }
                                                            });
                                                            break;
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adminres,viewGroup,false);
                        UserViewHolder viewHolder=new UserViewHolder(view);
                        return viewHolder;
                    }
                };
        userlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }



    @Override
    public void onClick(View v) {
        //setForcus(btn_unfocus, (Button) findViewById(v.getId()));
        //Or use switch
        switch (v.getId()){
            case R.id.btn0 :
                //setFocus(btn_unfocus, btn[0]);
                userref= FirebaseDatabase.getInstance().getReference().child("users").child("normal");
                flag=1;
                DisplayAllUser();
                break;

            case R.id.btn1 :
               //setFocus(btn_unfocus, btn[1]);
                userref= FirebaseDatabase.getInstance().getReference().child("users").child("reported");
                flag=2;
                DisplayAllUser();

                break;




        }
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View uview;
        RadioGroup radioGroup;
        CircleImageView image;
        RelativeLayout userRL;
        RadioButton rbactive,rbdeactive,radioButton;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            uview=itemView;
            radioGroup=(RadioGroup)uview.findViewById(R.id.rg);
            image=(CircleImageView)uview.findViewById(R.id.user_profile_image);
            userRL=(RelativeLayout)uview.findViewById(R.id.userRL);
            rbactive=(RadioButton)uview.findViewById(R.id.rbactive);
            rbdeactive=(RadioButton)uview.findViewById(R.id.rbdeactive);

        }


        public void setFullname(String fullname){
            TextView userfullname=(TextView)uview.findViewById(R.id.user_name);
            userfullname.setText(fullname);
        }
        public void setProfile_pic(Context ctx,String profile_pic){

            Glide.with(ctx).load(profile_pic).into(image);
        }
        public void setUid(String uid){}
    }

    private void setFocus(Button btn_unfocus, Button btn_focus){
        btn_unfocus.setTextColor(Color.rgb(49, 50, 51));
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_focus.setTextColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(Color.rgb(3, 106, 150));
        this.btn_unfocus = btn_focus;
    }

}