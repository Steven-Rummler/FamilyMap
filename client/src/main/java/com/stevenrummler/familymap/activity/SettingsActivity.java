package com.stevenrummler.familymap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.stevenrummler.familymap.R;
import com.stevenrummler.familymap.data.DataCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    // Putting the labels here cleans up the code in the functions, improving overall readability
    ArrayList<String> names = new ArrayList<>(Arrays.asList("Life Story Lines", "Family Tree Lines", "Spouse Lines", "Father's Side", "Mother's Side", "Male Events", "Female Events"));
    ArrayList<String> descriptions = new ArrayList<>(Arrays.asList("show life story lines", "show family tree lines", "show spouse lines", "filter by father's side of family", "filter by mother's side of family", "filter events based on gender", "filter events based on gender"));
    ArrayList<ConstraintLayout> tiles = new ArrayList<>();
    ConstraintLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Family Map: Settings");

        tiles.add(findViewById(R.id.life));
        tiles.add(findViewById(R.id.tree));
        tiles.add(findViewById(R.id.spouse));
        tiles.add(findViewById(R.id.father));
        tiles.add(findViewById(R.id.mother));
        tiles.add(findViewById(R.id.male));
        tiles.add(findViewById(R.id.female));

        logout = findViewById(R.id.logout);
        logout.findViewById(R.id.toggle).setVisibility(View.INVISIBLE);

        // Get the current settings
        DataCache cache = DataCache.getInstance();

        // Configure the tiles with text, settings, and events

        View.OnClickListener switchListener = v -> {
            View parent = ((View) v.getParent());
            String setting = getResources().getResourceEntryName(parent.getId());
            boolean checked = ((Switch) v).isChecked();
            DataCache.getInstance().getSettings().put(setting, checked);
        };

        View.OnClickListener logoutListener = v -> {
            DataCache.getInstance().setAuthToken(null);
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        };

        // For each tile:
        int tileNumber = 0;
        for (ConstraintLayout tile : tiles) {
            // Add the label
            ((TextView) tile.findViewById(R.id.name)).setText(names.get(tileNumber));
            ((TextView) tile.findViewById(R.id.description)).setText(descriptions.get(tileNumber));

            // Set the toggle
            String setting = getResources().getResourceEntryName(tile.getId());
            Switch toggle = tile.findViewById(R.id.toggle);
            toggle.setChecked(DataCache.getInstance().getSettings().get(setting));

            // Add the listener
            toggle.setOnClickListener(switchListener);

            tileNumber++;
        }

        ((TextView) logout.findViewById(R.id.name)).setText("Logout");
        ((TextView) logout.findViewById(R.id.description)).setText("return to login screen");

        logout.setOnClickListener(logoutListener);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Reset switches
        for (ConstraintLayout tile : tiles) {
            String setting = getResources().getResourceEntryName(tile.getId());
            Switch toggle = tile.findViewById(R.id.toggle);
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);
            toggle.setChecked(sharedPreferences.getBoolean(setting, true));
        }
    }
}