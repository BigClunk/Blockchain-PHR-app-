package com.example.BlockchainPHR;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.BlockchainPHR.Blockchain.BlockChainMain;

public class HomeActivity extends AppCompatActivity {

    private ImageButton button;
    private ImageView profile, location, email, docProfile, logout, home, settings;




    SQLiteHelper testDB;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uimain);

        button = (ImageButton) findViewById(R.id.addRecordbtn);
        profile = (ImageView) findViewById(R.id.imv7);
        logout = (ImageView) findViewById(R.id.imageviewtest1);
        home = (ImageView) findViewById(R.id.home);
        docProfile = (ImageView) findViewById(R.id.imv9);
        location = (ImageView) findViewById(R.id.imv3);
        email = (ImageView) findViewById(R.id.imv5);
        settings =(ImageView) findViewById(R.id.imageViewtest5);


        testDB = new SQLiteHelper(this);


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

        docProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), DocProfile.class);
                startActivity(intent);
            }
        });


        profile.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendmail();

/*
                Intent intent = new Intent(HomeActivity.this, EmailActivity.class);
                startActivity(intent);
*/
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo: 42.30857983401924, -83.0319874731131"));
                startActivity(intent);

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BlockChainMain.class);
                startActivity(intent);

            }
        });









    }
    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void sendmail() {

        String[] TO_EMAILS = {"Doctor@example.com", "emailtwo@example.com"};
        String[] CC = {"emailthree@example.com"};
        String[] BCC = {"emailfour@example.com"};

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, TO_EMAILS);
        intent.putExtra(Intent.EXTRA_CC, CC);
        intent.putExtra(Intent.EXTRA_BCC, BCC);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the email.");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the email");

        startActivity(Intent.createChooser(intent, "Choose your email client"));

    }



}