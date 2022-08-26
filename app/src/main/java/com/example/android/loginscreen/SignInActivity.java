package com.example.android.loginscreen;

import static com.example.android.loginscreen.App.mAuth;
import static com.example.android.loginscreen.App.mGoogleSignInClient;
import static com.example.android.loginscreen.ProgressUtils.hideProgressBar;
import static com.example.android.loginscreen.ProgressUtils.showProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    private static final int SING_IN_REQUEST_CODE = 1232;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextInputLayout mEmailLayout, mPasswordLayout;
    private CardView mBtnSignin;
    private TextView mBtnRegisterUser;
    private SignInButton mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        mSignInButton = findViewById(R.id.googleSignInBtn);

        View childAt = mSignInButton.getChildAt(0);
        int min = Math.min(childAt.getPaddingRight(), childAt.getPaddingEnd());
        childAt.setPadding(min,childAt.getPaddingTop(),min,childAt.getPaddingBottom());

        //   GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        mBtnSignin.setOnClickListener(this::singInUser);
        mBtnRegisterUser.setOnClickListener(this::createUser);
        mSignInButton.setOnClickListener(this::signInWithGoogle);

        ProgressUtils.initAddProgress(this);
        hideProgressBar();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    updateUI();
                }
            }
        };
    }


    private void signInWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,SING_IN_REQUEST_CODE);
        showProgressBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SING_IN_REQUEST_CODE){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignIn(accountTask);
        }
    }

    private void handleGoogleSignIn(Task<GoogleSignInAccount> accountTask) {
        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            View pare = findViewById(android.R.id.content);
            String statusCodeString = GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode());
            hideProgressBar();
            Snackbar snackbar = Snackbar.make(pare,""+statusCodeString,Snackbar.LENGTH_LONG);
            snackbar.show();

//            mOutputText.setText(GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()) +"\n"+ e.getStatusCode());
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       hideProgressBar();
                    }
                });
    }


    private void updateUI() {
        FirebaseUser account = mAuth.getCurrentUser();
        if (account != null) {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            if (mAuth != null){
                mAuth.removeAuthStateListener(authStateListener);
            }
        }
    }

    private void singInUser(View view) {

        if (!validateEmailAddress() | !validatePassword()) {
            // Email or Password not valid,
            return;
        }
        showProgressBar();
        //Email and Password valid, sign in user here
        String userEmail = mEmailLayout.getEditText().getText().toString().trim();
        String userPassword = mPasswordLayout.getEditText().getText().toString().trim();
        Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(userEmail, userPassword);
        authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    hideProgressBar();
                    Toast.makeText(SignInActivity.this, "login Successful", Toast.LENGTH_SHORT).show();
                }else {
                    hideProgressBar();
                    if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        Toast.makeText(SignInActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(SignInActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }



    private void createUser(View view) {

        if (!validateEmailAddress() | !validatePassword()) {
            // Email or Password not valid,
            return;
        }
        //Email and Password valid, create user here
        String userEmail = mEmailLayout.getEditText().getText().toString().trim();
        String userPassword = mPasswordLayout.getEditText().getText().toString().trim();
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(userEmail, userPassword);
        authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    hideProgressBar();
                    Toast.makeText(SignInActivity.this, " user created  & login Successful", Toast.LENGTH_SHORT).show();
                }else {
                    hideProgressBar();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(SignInActivity.this, "Email Already Exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initViews() {
        mEmailLayout = findViewById(R.id.et_email);
        mPasswordLayout = findViewById(R.id.et_password);
        mBtnSignin = findViewById(R.id.btn_singin);
        mBtnRegisterUser = findViewById(R.id.btn_registeruser);

    }

    private boolean validateEmailAddress() {

        String email = mEmailLayout.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            mEmailLayout.setError("Email is required. Can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailLayout.setError("Invalid Email. Enter valid email address.");
            return false;
        } else {
            mEmailLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = mPasswordLayout.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            mPasswordLayout.setError("Password is required. Can't be empty.");
            return false;
        } else if (password.length() < 8) {
            mPasswordLayout.setError("Password length short. Minimum 8 characters required.");
            return false;
        } else {
            mPasswordLayout.setError(null);
            return true;
        }
    }




}
