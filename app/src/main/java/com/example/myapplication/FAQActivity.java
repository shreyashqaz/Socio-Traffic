package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ms.square.android.expandabletextview.ExpandableTextView;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv1 = (ExpandableTextView)findViewById(R.id.expand_text_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv1.setText(getString(R.string.text1));

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv2 = (ExpandableTextView)findViewById(R.id.expand_text2_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv2.setText(getString(R.string.text2));

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv3 = (ExpandableTextView)findViewById(R.id.expand_text3_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv3.setText(getString(R.string.text3));

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv4 = (ExpandableTextView)findViewById(R.id.expand_text4_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv4.setText(getString(R.string.text4));

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv5 = (ExpandableTextView)findViewById(R.id.expand_text5_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv5.setText(getString(R.string.text5));

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv6 = (ExpandableTextView)findViewById(R.id.expand_text6_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv6.setText(getString(R.string.text6));

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv7 = (ExpandableTextView)findViewById(R.id.expand_text7_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv7.setText(getString(R.string.text7));

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv8 = (ExpandableTextView)findViewById(R.id.expand_text8_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv8.setText(getString(R.string.text8));
    }
}
