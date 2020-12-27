package com.stevenrummler.familymap.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.stevenrummler.familymap.R;
import com.stevenrummler.familymap.activity.EventActivity;
import com.stevenrummler.familymap.activity.MainActivity;
import com.stevenrummler.familymap.activity.PersonActivity;
import com.stevenrummler.familymap.activity.SearchActivity;
import com.stevenrummler.familymap.activity.SettingsActivity;
import com.stevenrummler.familymap.data.DataCache;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import model.Event;
import model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap map;
    final String birthEvent = "birth";
    String startEvent = "";
    Marker currentMarker;
    boolean mapExists = false;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Iconify.with(new FontAwesomeModule());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        startEvent = getArguments().getString("event");
        if (savedInstanceState != null) startEvent = savedInstanceState.getString("event");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        if (getActivity() instanceof MainActivity) {
            inflater.inflate(R.menu.main_menu, menu);

            MenuItem searchItem = menu.findItem(R.id.searchMenu);
            MenuItem settingsItem = menu.findItem(R.id.settingsMenu);

            searchItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).color(Color.WHITE).actionBarSize());
            settingsItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_sliders).color(Color.WHITE).actionBarSize());
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.searchMenu:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.settingsMenu:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        map.setOnMapLoadedCallback(this);
        map.setOnMarkerClickListener(this);
        map.getUiSettings().setZoomControlsEnabled(true);
        Marker startMarker = null;

        DataCache cache = DataCache.getInstance();

        ArrayList<Event> events = cache.getValidEvents();

        for (Event event : events) {
            LatLng coords = new LatLng(event.getLatitude(), event.getLongitude());
            Float color = cache.getEventTypes().get(event.getEventType());
            Marker marker = map.addMarker(new MarkerOptions().position(coords).icon(BitmapDescriptorFactory.defaultMarker(color)));
            marker.setTag(event.getEventID());
            if (event.getEventID().equals(startEvent)) {
                startMarker = marker;
            }
        }

        if (startMarker != null) {
            onMarkerClick(startMarker);
            if (getActivity() instanceof EventActivity) {
                map.stopAnimation();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(startMarker.getPosition()), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        map.animateCamera(CameraUpdateFactory.zoomIn());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        }
    }

    @Override
    public void onMapLoaded() {
        mapExists = true;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        currentMarker = marker;

        // Go to the marker
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        // Get event and person information
        startEvent = marker.getTag().toString();
        Event markerEvent = DataCache.getInstance().getEvents().get(startEvent);
        Person person = DataCache.getInstance().getPersons().get(markerEvent.getPersonID());
        DataCache cache = DataCache.getInstance();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);

        // Make the info box link to the person
        LinearLayout info = getActivity().findViewById(R.id.info);
        View.OnClickListener onClickListener = v -> {
            Intent intent = new Intent(getActivity(), PersonActivity.class).putExtra("id", person.getPersonID());
            startActivity(intent);
        };
        info.setOnClickListener(onClickListener);

        // Update icon to show gender
        ImageView icon = getActivity().findViewById(R.id.icon);
        Drawable drawable = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).color(Color.rgb(255, 192, 203));
        if (person.getGender().equals("m")) {
            drawable = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).color(Color.BLUE);
        }
        icon.setImageDrawable(drawable);

        // Update data to show event information
        TextView data = getActivity().findViewById(R.id.data);
        String message = person.getFirstName() + " " + person.getLastName() + "\n" +
                markerEvent.getEventType() + ": " + markerEvent.getCity() + ", " +
                markerEvent.getCountry() + " (" + markerEvent.getYear() + ")";
        data.setText(message);

        // Clear map and redraw markers
        map.clear();
        for (Event event : DataCache.getInstance().getValidEvents()) {
            LatLng coords = new LatLng(event.getLatitude(), event.getLongitude());
            Float color = DataCache.getInstance().getEventTypes().get(event.getEventType());
            Marker newMarker = map.addMarker(new MarkerOptions().position(coords).icon(BitmapDescriptorFactory.defaultMarker(color)));
            newMarker.setTag(event.getEventID());
        }

        // Draw spouse line
        if (sharedPreferences.getBoolean("spouse", true) && !person.getSpouseID().equals("")) {
            ArrayList<Event> spouseLife = cache.lifeStory(person.getSpouseID());
            if (spouseLife.size() > 0) {
                LatLng spouseLocation = new LatLng(spouseLife.get(0).getLatitude(), spouseLife.get(0).getLongitude());
                map.addPolyline(new PolylineOptions().add(marker.getPosition(), spouseLocation).color(Color.YELLOW));
            }
        }

        // Draw family tree lines
        if (sharedPreferences.getBoolean("tree", true)) {
            if (!person.getFatherID().equals("")) {
                drawMoreLines(marker.getPosition(), person.getFatherID(), Color.CYAN, 20);
            }
            if (!person.getMotherID().equals("")) {
                drawMoreLines(marker.getPosition(), person.getMotherID(), Color.MAGENTA, 20);
            }
        }

        // Draw life story lines
        if (sharedPreferences.getBoolean("life", true)) {
            ArrayList<Event> lifeStory = cache.lifeStory(person.getPersonID());
            for (int i = 0; i < lifeStory.size() - 1; i++) {
                Event eventOne = lifeStory.get(i);
                Event eventTwo = lifeStory.get(i + 1);
                LatLng one = new LatLng(eventOne.getLatitude(), eventOne.getLongitude());
                LatLng two = new LatLng(eventTwo.getLatitude(), eventTwo.getLongitude());
                map.addPolyline(new PolylineOptions().add(one, two).color(Color.BLACK));
            }
        }

        return true;
    }

    // Recursive function for drawing family tree lines on map
    private void drawMoreLines(LatLng previous, String personId, int color, float width) {
        if (width < 1) width = 1;
        DataCache cache = DataCache.getInstance();
        Person person = cache.getPersons().get(personId);
        ArrayList<Event> lifeStory = cache.lifeStory(person.getPersonID());
        if (lifeStory.size() > 0) {
            LatLng location = new LatLng(lifeStory.get(0).getLatitude(), lifeStory.get(0).getLongitude());
            map.addPolyline(new PolylineOptions().add(previous, location).color(color).width(width));

            if (!person.getFatherID().equals("")) {
                drawMoreLines(location, person.getFatherID(), color, width / 2);
            }
            if (!person.getMotherID().equals("")) {
                drawMoreLines(location, person.getMotherID(), color, width / 2);
            }
        }
    }

    // Redraws the map on resume to apply settings immediately after SettingsActivity ends
    @Override
    public void onResume() {
        super.onResume();

        if (mapExists) {
            onMapReady(map);
        }
    }

    // Handle screen rotation
    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        if (DataCache.getInstance().getAuthToken() != null) {
            outState.putString("event", startEvent);
        }

        super.onSaveInstanceState(outState);
    }
}