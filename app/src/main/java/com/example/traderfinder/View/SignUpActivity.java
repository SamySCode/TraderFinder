package com.example.traderfinder.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.traderfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private RadioGroup accountTypeGroup;
    private RadioButton generalAccount;
    private RadioButton tradesmanAccount;
    private MaterialButton signUpButton;
    private TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        accountTypeGroup = findViewById(R.id.account_type_group);
        generalAccount = findViewById(R.id.general_account);
        tradesmanAccount = findViewById(R.id.tradesman_account);
        signUpButton = findViewById(R.id.button_signup);
        loginButton = findViewById(R.id.button_login);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailInput.getText().toString().trim();
                final String password = passwordInput.getText().toString().trim();
                final String accountType;

                if (email.isEmpty()) {
                    emailInputLayout.setError("Please enter your email.");
                    return;
                } else {
                    emailInputLayout.setError(null);
                }
                if (password.isEmpty()) {
                    passwordInputLayout.setError("Please enter your password.");
                    return;
                } else {
                    passwordInputLayout.setError(null);
                }

                if (generalAccount.isChecked()) {
                    accountType = "general";
                } else if (tradesmanAccount.isChecked()) {
                    accountType = "tradesman";
                } else {
                    Toast.makeText(SignUpActivity.this, "Please select an account type.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    DatabaseReference currentUserDb = mDatabase.child(userId);
                                    currentUserDb.child("accountType").setValue(accountType);

                                    Intent intent;
                                    if (accountType.equals("general")) {
                                        intent = new Intent(SignUpActivity.this, GeneralUserActivity.class);
                                    } else {
                                        intent = new Intent(SignUpActivity.this, TradesmanActivity.class);
                                    }
                                    startActivity(intent);
                                    Toast.makeText(SignUpActivity.this, "Sign up successful.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}