package com.example.trackcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//Android programming is event driven programming so when events happen we call the appropriate handler to complete the request if nothing is happening its just sitting in idle
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance(); //
    }
    //MainActivity -> start_page if log in is successful
    public void loginButtonClicked(View v) {
        //Get password and email
        EditText email = findViewById(R.id.usernameField);
        EditText password = findViewById(R.id.PasswordField);
        String editedEmail = email.getText().toString().trim();
        String editedPassword = password.getText().toString().trim();
        if (editedEmail.isEmpty() || editedPassword.isEmpty()) {
            Toast.makeText(this, "Text field cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(editedEmail, editedPassword).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    //If successfully logged in then take them to the next page
                    Intent i = new Intent(this, start_page.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    //MainActivity -> create_user
    public void createUser(View V){
        Intent i = new Intent(this, create_user.class);
        startActivity(i);
    }
    //Main Activity -> forgot_password
    public void forgotPasscode(View V){
        Intent i = new Intent(this, forgot_password.class);
        startActivity(i);
    }
}