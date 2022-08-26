package com.example.android.loginscreen;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class App extends Application {
    public static FirebaseAuth mAuth;
    @SuppressLint("StaticFieldLeak")
    public static GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder()
                        .requestEmail()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

}
