package com.stevenrummler.familymap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.stevenrummler.familymap.fragment.LoginFragment;
import com.stevenrummler.familymap.fragment.MapFragment;
import com.stevenrummler.familymap.R;
import com.stevenrummler.familymap.data.DataCache;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataCache cache = DataCache.getInstance();

        FragmentManager fm = this.getSupportFragmentManager();

        if (cache.getAuthToken() == null) {
            LoginFragment loginFragment = (LoginFragment) fm.findFragmentById(R.id.fragmentContainer);
            if (loginFragment == null) {
                loginFragment = createLoginFragment();
                fm.beginTransaction().add(R.id.fragmentContainer, loginFragment).commit();
            }
        } else {
            MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.fragmentContainer);
            if (mapFragment == null) {
                mapFragment = createMapFragment();
                fm.beginTransaction().add(R.id.fragmentContainer, mapFragment).commit();
            }
        }
    }

    private LoginFragment createLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();

        Bundle args = new Bundle();
        loginFragment.setArguments(args);

        return loginFragment;
    }

    private MapFragment createMapFragment() {
        MapFragment mapFragment = new MapFragment();

        Bundle args = new Bundle();
        args.putString("event", "");
        mapFragment.setArguments(args);

        return mapFragment;
    }

    public void openMap() {
        DataCache cache = DataCache.getInstance();

        FragmentManager fm = this.getSupportFragmentManager();

        if (cache.getAuthToken() != null) {
            MapFragment mapFragment = createMapFragment();
            fm.beginTransaction().replace(R.id.fragmentContainer, mapFragment).commit();
        }
    }
}
