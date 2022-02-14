package com.example.BlockchainPHR;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DocProfile extends AppCompatActivity {
     Button home;

     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docprofile);

         home = (Button) findViewById(R.id.returnBtn2);

         home.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                 startActivity(intent);
             }
         });

    }
}