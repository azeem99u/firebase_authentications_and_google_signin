package com.example.android.loginscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ProgressUtils.initAddProgress(this);
        ProgressUtils.hideProgressBar();
        mDrawerLayout = findViewById(R.id.drawerLayout_id);
        mNavigationView = findViewById(R.id.navigationView);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigationView.setCheckedItem(R.id.mainActivity1);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mainActivity1:
                        mDrawerLayout.close();
                        if (Objects.requireNonNull(mNavigationView.getCheckedItem()).getItemId() == item.getItemId()) {
                            break;
                        }
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        break;
                    case R.id.info:
                        Toast.makeText(MainActivity.this, "Info clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.setting:
                        Toast.makeText(MainActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.logOut:
                        mDrawerLayout.close();
                        ProgressUtils.showProgressBar();
                        signOut();
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });


    }


    private void signOut() {
        App.mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                App.mAuth.signOut();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isOpen()) {
            mDrawerLayout.close();
            return;
        }
        super.onBackPressed();
    }
}