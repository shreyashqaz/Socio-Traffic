package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private Button btlogin;
    private TextView tvsignup;
    private EditText etusername;
    private EditText etpassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;
    CheckBox admincheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btlogin=(Button)findViewById(R.id.btlogin);
        etusername=(EditText) findViewById(R.id.etusername);
        etpassword=(EditText) findViewById(R.id.etpassword);
        tvsignup=(TextView)findViewById(R.id.tvsignup);
        admincheck=(CheckBox)findViewById(R.id.admincheck);


        firebaseAuth=FirebaseAuth.getInstance();

        pd=new ProgressDialog(this);

        btlogin.setOnClickListener(this);
        tvsignup.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if(v==btlogin)
        {
            userlogin();
        }
        else if(v==tvsignup)
        {

            startActivity(new Intent(Login.this,MainActivity.class));
        }
    }

    private void userlogin() {
        String email=etusername.getText().toString().trim();
        String password=etpassword.getText().toString().trim();

        if(TextUtils.isEmpty((email)))
        {
            Toast.makeText(this,"Please enter Username",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setMessage("Log in.... please wait");
        pd.show();


        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            pd.dismiss();
                            Toast.makeText(Login.this,"Log in Successful",Toast.LENGTH_SHORT).show();
                            if(admincheck.isChecked()){
                                Intent startadmin=new Intent(Login.this, AdminActivity.class);
                                startadmin.putExtra("flag", 1);
                                startActivity(startadmin);
                                finish();
                            }
                            else {
                                Intent startmainpage=new Intent(Login.this, MainPage.class);
                                startmainpage.putExtra("flag",0);
                                startActivity(startmainpage);
                                finish();
                            }

                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(Login.this,"Log in Unsuccessful",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
