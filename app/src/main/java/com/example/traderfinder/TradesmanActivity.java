package com.example.traderfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.traderfinder.ui.completed.TradesmanCompletedFragment;
import com.example.traderfinder.ui.confirmed.TradesmanConfirmedFragment;
import com.example.traderfinder.ui.home.TradesmanHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TradesmanActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Fragments
    private TradesmanHomeFragment tradesmanHomeFragment;
    private TradesmanConfirmedFragment tradesmanConfirmedFragment;
    private TradesmanCompletedFragment tradesmanCompletedFragment;
    private TradesmanProfileDetailsFragment tradesmanProfileDetailsFragment;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradesman);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle NavigationView item clicks
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    // Open Tradesman Profile Details fragment
                    setFragment(tradesmanProfileDetailsFragment);
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    // Redirect to login activity
                    Intent intent = new Intent(TradesmanActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                default:
                    return false;
            }
            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        tradesmanHomeFragment = new TradesmanHomeFragment();
        tradesmanConfirmedFragment = new TradesmanConfirmedFragment();
        tradesmanCompletedFragment = new TradesmanCompletedFragment();
        tradesmanProfileDetailsFragment = new TradesmanProfileDetailsFragment();

        setFragment(tradesmanHomeFragment);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        setFragment(tradesmanHomeFragment);
                        return true;

                    case R.id.confirmed:
                        setFragment(tradesmanConfirmedFragment);
                        return true;

                    case R.id.completed:
                        setFragment(tradesmanCompletedFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.tradesman_frame, fragment);
        fragmentTransaction.commit();
    }
}
