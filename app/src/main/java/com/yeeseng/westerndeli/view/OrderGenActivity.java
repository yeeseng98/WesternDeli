package com.yeeseng.westerndeli.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.yeeseng.westerndeli.R;


public class OrderGenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment=null;
            switch (menuItem.getItemId()) {
                case R.id.navigation_mainMenu:
                    Intent gotoMain = new Intent(OrderGenActivity.this, MainActivity.class);
                    startActivity(gotoMain);
                    break;

                case R.id.navigation_curOrder:
                    fragment = new FragCurOrder();
                    break;

                case R.id.navigation_myProfile:
                    finish();
                    fragment = new FragMyProfile();
                    break;
            }
            return LoadFragment(fragment);        }
    };

    private boolean LoadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_order, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_gen);

        LoadFragment(new FragCurOrder());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
