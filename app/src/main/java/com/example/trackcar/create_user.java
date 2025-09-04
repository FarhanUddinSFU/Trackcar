package com.example.trackcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.core.view.WindowInsetsCompat;

public class create_user extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }

    public void createUser(View V) {
        EditText newEmail = findViewById(R.id.editTextTextEmailAddress);
        EditText newPassword = findViewById(R.id.editTextTextPassword);
        String editedEmail = newEmail.getText().toString().trim();
        String editedPassword = newPassword.getText().toString().trim();
        if (editedEmail.isEmpty() || editedPassword.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(editedEmail, editedPassword).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    //Take them to the start_page page
                    Intent i = new Intent(this, start_page.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed: "
                            + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
