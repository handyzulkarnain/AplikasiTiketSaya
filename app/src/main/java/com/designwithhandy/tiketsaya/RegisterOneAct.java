package com.designwithhandy.tiketsaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterOneAct extends AppCompatActivity {

    Button btn_continue;
    LinearLayout btn_back;
    EditText username, password, email_address;
    DatabaseReference reference, reference_username;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email_address = findViewById(R.id.email_address);



        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ubah ke loading
                btn_continue.setEnabled(false);
                btn_continue.setText("loading ...");

                //ambil username yang uda ada di database firebase
                reference_username = FirebaseDatabase.getInstance().getReference().child("Users").child(username.getText().toString());
                reference_username.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //jika username uda ada
                        if (snapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Username sudah tersedia!", Toast.LENGTH_SHORT).show();
                            //ubah ke active
                            btn_continue.setEnabled(true);
                            btn_continue.setText("CONTINUE");

                        }
                        else {
                            //menyimpan data pada local storage
                            SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(username_key, username.getText().toString());
                            editor.apply();


                            //simpan di database
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username.getText().toString());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().child("username").setValue(username.getText().toString());
                                    snapshot.getRef().child("password").setValue(password.getText().toString());
                                    snapshot.getRef().child("email_address").setValue(email_address.getText().toString());
                                    snapshot.getRef().child("user_balance").setValue(800);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            //berpindah activity
                            Intent gotonextregister = new Intent(RegisterOneAct.this, RegisterTwoAct.class);
                            startActivity(gotonextregister);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
