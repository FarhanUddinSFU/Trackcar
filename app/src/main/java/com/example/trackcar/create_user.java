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
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.core.view.WindowInsetsCompat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class create_user extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore userDb;
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
        userDb = FirebaseFirestore.getInstance();
    }

    public void createUser(View V) {
        EditText newEmail = findViewById(R.id.editTextTextEmailAddress);
        EditText newPassword = findViewById(R.id.editTextTextPassword);
        EditText usersName = findViewById(R.id.nameInput);
        String editedEmail = newEmail.getText().toString().trim();
        String editedPassword = newPassword.getText().toString().trim();
        String editedName = usersName.getText().toString().trim();
        if (editedEmail.isEmpty() || editedPassword.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(editedEmail, editedPassword).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    //Also make an instance of them on the database that will store future info
                    //Users info will be stored in key value pairs
                    HashMap<String, String> userInfo = new HashMap<>();
                    userInfo.put("Name", editedName);
                    userInfo.put("Email" , editedEmail);
                    userDb.collection("Users")
                                    .document(user.getUid()).set(userInfo).addOnSuccessListener(aVoid ->{
                                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e ->{
                                Toast.makeText(this, "Failed to save account" + e, Toast.LENGTH_LONG).show();
                            });
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
