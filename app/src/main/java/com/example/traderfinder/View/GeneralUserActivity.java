package com.example.traderfinder.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.traderfinder.R;
import com.example.traderfinder.View.ui.completed.CompletedJobsFragment;
import com.example.traderfinder.View.ui.confirmed.ConfirmedJobsFragment;
import com.example.traderfinder.View.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

public class GeneralUserActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Fragments
    private HomeFragment homeFragment;
    private ConfirmedJobsFragment confirmedJobsFragment;
    private CompletedJobsFragment completedJobsFragment;

    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public GeneralUserActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user);

        homeFragment = new HomeFragment();
        confirmedJobsFragment = new ConfirmedJobsFragment();
        completedJobsFragment = new CompletedJobsFragment();

        setFragment(homeFragment);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navigationView = findViewById(R.id.nav_view);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    setFragment(new ProfileDetailsFragment());
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    // Redirect to login activity
                    Intent intent = new Intent(GeneralUserActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                default:
                    return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        setFragment(homeFragment);
                        return true;

                    case R.id.confirmed:
                        setFragment(confirmedJobsFragment);
                        return true;

                    case R.id.completed:
                        setFragment(completedJobsFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.general_user_frame, fragment);
        fragmentTransaction.commit();
    }
}
