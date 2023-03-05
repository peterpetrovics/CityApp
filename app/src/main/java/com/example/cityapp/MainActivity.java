package com.example.cityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

     private EditText username;
     private EditText password;
     private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("user") && password.getText().toString().equals("password")) {
                    startActivity(new Intent(MainActivity.this, City.class)); //Bezárjuk a felületet, és megnyitjuk a másikat
                    username.getText().clear();
                    password.getText().clear();
                }
                else if (password.getText().toString().equals("") || username.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Add meg az alábbi adatokat!", Toast.LENGTH_SHORT).show();
                }
                else if (!username.getText().toString().equals("user") || !password.getText().toString().equals("password")) {
                    Toast.makeText(MainActivity.this, "A megadott adatok hibásak!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}