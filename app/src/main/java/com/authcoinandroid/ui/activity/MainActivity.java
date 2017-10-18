package com.authcoinandroid.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import com.authcoinandroid.R;
import com.authcoinandroid.ui.fragment.ChallengeFragment;
import com.authcoinandroid.ui.fragment.IdentityFragment;
import com.authcoinandroid.ui.fragment.TrustFragment;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_identity:
                                selectedFragment = new IdentityFragment();
                                Log.d(LOG_TAG, "User opened identity fragment");
                                break;
                            case R.id.action_challenges:
                                selectedFragment = new ChallengeFragment();
                                Log.d(LOG_TAG, "User opened challenges fragment");
                                break;
                            case R.id.action_trust:
                                selectedFragment = new TrustFragment();
                                Log.d(LOG_TAG, "User opened trust fragment");
                                break;
                        }
                        applyFragment(selectedFragment, true);
                        return true;
                    }
                });

        applyFragment(new IdentityFragment(), false);
    }

    private void applyFragment(Fragment selectedFragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }
}
