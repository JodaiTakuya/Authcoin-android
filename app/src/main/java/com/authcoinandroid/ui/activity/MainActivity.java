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
import android.view.View;
import com.authcoinandroid.R;
import com.authcoinandroid.service.identity.WalletService;
import com.authcoinandroid.ui.fragment.ChallengeFragment;
import com.authcoinandroid.ui.fragment.IdentityFragment;
import com.authcoinandroid.ui.fragment.TrustFragment;
import com.authcoinandroid.ui.fragment.WelcomeFragment;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Class selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_identity:
                                selectedFragment = IdentityFragment.class;
                                Log.d(LOG_TAG, "User opened identity fragment");
                                break;
                            case R.id.action_challenges:
                                selectedFragment = ChallengeFragment.class;
                                Log.d(LOG_TAG, "User opened challenges fragment");
                                break;
                            case R.id.action_trust:
                                selectedFragment = TrustFragment.class;
                                Log.d(LOG_TAG, "User opened trust fragment");
                                break;
                        }
                        applyFragment(selectedFragment, false);
                        return true;
                    }
                });

        if (WalletService.getInstance().isWalletCreated(getApplicationContext())) {
            applyFragment(IdentityFragment.class, false);
        } else {
            applyFragment(WelcomeFragment.class, false);
            bottomNavigationView.setVisibility(View.INVISIBLE);
        }
    }

    public void applyFragment(@NonNull Class fragmentClass, boolean addToBackStack) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (addToBackStack) {
                transaction.addToBackStack(null).add(R.id.fragment_container, fragment);
            } else {
                transaction.replace(R.id.fragment_container, fragment);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
