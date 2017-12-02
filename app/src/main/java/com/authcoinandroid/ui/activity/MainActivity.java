package com.authcoinandroid.ui.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
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

                        Class currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass();
                        if (!currentFragment.equals(selectedFragment)) {
                            applyFragment(selectedFragment, false, false);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

        if (WalletService.getInstance().isWalletCreated(getApplicationContext())) {
            applyFragment(IdentityFragment.class, false, false);
        } else {
            applyFragment(WelcomeFragment.class, false, true);
        }
    }

    public void applyFragment(@NonNull Class fragmentClass, boolean addToBackStack, boolean hideNavigation) {
        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }

            if (hideNavigation) {
                findViewById(R.id.bottom_navigation).setVisibility(View.INVISIBLE);
            } else {
                findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyFullFragmentWithBundle(@NonNull Class fragmentClass, Bundle bundle) {
        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            findViewById(R.id.bottom_navigation).setVisibility(View.INVISIBLE);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Hide keyboard and lose focus on EditText when clicking anywhere outside
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void displayError(String logTag, String message) {
        Log.e(logTag, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
