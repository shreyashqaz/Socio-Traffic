package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btregister;
    private TextView tvalready;
  //  private TextView tvtest;
    private EditText etemail;
    private EditText etpassword,etcpassword;
    private ProgressDialog pd;
    private RadioGroup radiotype;
    final int[] flag = {0};

     FirebaseAuth fba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btregister=(Button)findViewById(R.id.btregister);
        tvalready=(TextView) findViewById(R.id.tvalready);
        radiotype=(RadioGroup)findViewById(R.id.radioGroup);
        etemail=(EditText) findViewById(R.id.etemail);
        etpassword=(EditText) findViewById(R.id.etpassword);
        etcpassword=(EditText) findViewById(R.id.etcpassword);
       // tvtest=(TextView)findViewById(R.id.tvtest);
        radiotype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbuser:
                        flag[0] =0;
                        break;
                    case R.id.rbadmin:
                        flag[0] =1;
                        break;
                }
            }
        });

        btregister.setOnClickListener(this);
        tvalready.setOnClickListener(this);
       // tvtest.setOnClickListener(this);

        pd=new ProgressDialog(this);

        fba=FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        if(v==btregister)
        {
            registeruser();
        }
      else if(v==tvalready) {
            finish();
            startActivity(new Intent(MainActivity.this,Login.class));
        }
      /*  else if(v==tvtest) {
            startActivity(new Intent(MainActivity.this,MainPage.class));
        }*/
        }

    private void registeruser() {
        String email=etemail.getText().toString().trim();
        String password=etpassword.getText().toString().trim();
        String cpassword=etcpassword.getText().toString().trim();
        if(TextUtils.isEmpty((email)))
        {
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
        return;
        }
        if(TextUtils.isEmpty(cpassword))
        {Toast.makeText(this,"Please Re-enter password",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(cpassword))
        {Toast.makeText(this,"Password miss-match",Toast.LENGTH_SHORT).show();
            return;
        }

        pd.setMessage("Registering Please wait");
        pd.show();


        fba.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            pd.dismiss();
                           Toast.makeText(MainActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                           Intent setupactivity=new Intent(MainActivity.this,SetupActivity.class);
                           setupactivity.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           setupactivity.putExtra("flag",flag[0]);
                           startActivity(setupactivity);
                           finish();
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(MainActivity.this,"Registration Unsuccessful",Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }

}

