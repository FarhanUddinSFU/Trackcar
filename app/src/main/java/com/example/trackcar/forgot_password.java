package com.example.trackcar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class forgot_password extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        mAuth = FirebaseAuth.getInstance();
    }
    //When reset button is clicked reset passcode
    public void resetPasscode(View v){
        EditText resetEmail = findViewById(R.id.forgotEmailEntry);
        String resetEmailEdited = resetEmail.getText().toString().trim();
        if(resetEmailEdited.isEmpty()){
            Toast.makeText(this,"Empty input, please enter an email", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.sendPasswordResetEmail(resetEmailEdited).addOnCompleteListener(this, task->{
               if(task.isSuccessful()){
                   Toast.makeText(this, "Reset link sent to email" + resetEmailEdited, Toast.LENGTH_SHORT).show();
                   //Return to the log in page so they can log in with new passcode
                   finish();
               }
               else{
                    Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
               }
            });
        }

    }
}