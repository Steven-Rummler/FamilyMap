package com.stevenrummler.familymap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.stevenrummler.familymap.fragment.MapFragment;
import com.stevenrummler.familymap.R;
import com.stevenrummler.familymap.data.DataCache;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {
    String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setTitle("Family Map: Event Details");

        event = getIntent().getStringExtra("id");

        FragmentManager fm = this.getSupportFragmentManager();

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.fragmentContainer);
        if (mapFragment == null) {
            mapFragment = createMapFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, mapFragment).commit();
        }
    }

    private MapFragment createMapFragment() {
        MapFragment mapFragment = new MapFragment();

        Bundle args = new Bundle();
        args.putString("event", event);
        mapFragment.setArguments(args);

        return mapFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        if (menu.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(menu);
    }
}