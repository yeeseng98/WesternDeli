package com.yeeseng.westerndeli.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Menu_Item;

public class ItemGenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment=null;
            switch (menuItem.getItemId()) {
                case R.id.navigation_mainMenu:
                    fragment = new FragMainMenu();
                    break;

                case R.id.navigation_curOrder:
                    fragment = new FragCurOrder();
                    break;

                case R.id.navigation_myProfile:
                    fragment = new FragMyProfile();
                    break;
            }
            return LoadFragment(fragment);        }
    };

    private boolean LoadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_item, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_gen);

        if(savedInstanceState == null) {
            passData();
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }
    }

    private void passData() {
        Intent receiveIntent = getIntent();
        Menu_Item item = receiveIntent.getParcelableExtra("selectedItem");

        Bundle bundle=new Bundle();
        bundle.putParcelable("selectedItem", item);

        FragItemDetail itemDetail =new FragItemDetail();
        itemDetail.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_item, itemDetail)
                .commit();


    }
}
