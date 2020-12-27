package com.stevenrummler.familymap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.stevenrummler.familymap.R;
import com.stevenrummler.familymap.data.DataCache;

import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Family Map: Search");

        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search();
                return false;
            }
        });

        recyclerView = findViewById(R.id.results);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
    }

    private void search() {
        SearchView searchView = findViewById(R.id.search);
        TextView textView = findViewById(R.id.count);
        String query = searchView.getQuery().toString();
        if (query.equals("")) {
            textView.setText("");
        } else {
            DataCache cache = DataCache.getInstance();
            ArrayList<Event> events = cache.searchEvents(query);
            ArrayList<Person> persons = cache.searchPersons(query);
            String resultStats = events.size() + " events and " + persons.size() +
                    " persons found matching query \"" + query + "\"";
            textView.setText(resultStats);
            SearchAdapter adapter = new SearchAdapter(events, persons);
            recyclerView.setAdapter(adapter);
        }
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

    // RecyclerView Classes

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Event> events;
        private final List<Person> persons;

        private SearchAdapter(List<Event> events, List<Person> persons) {
            this.events = events;
            this.persons = persons;
        }

        @Override
        public int getItemViewType(int position) {
            return position < events.size() ? 0 : 1;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < events.size()) {
                holder.bind(events.get(position));
            } else {
                holder.bind(persons.get(position - events.size()));
            }
        }

        @Override
        public int getItemCount() {
            return events.size() + persons.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView data1;
        private final TextView data2;

        private final int viewType;
        private Event event;
        private Person person;

        public SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            icon = itemView.findViewById(R.id.icon);
            data1 = itemView.findViewById(R.id.data1);
            data2 = itemView.findViewById(R.id.data2);
        }

        private void bind(Event event) {
            this.event = event;
            person = DataCache.getInstance().getPersons().get(event.getPersonID());
            String text = event.getEventType() + ": " + event.getCity() + ", " +
                    event.getCountry() + " (" + event.getYear() + ")";
            data1.setText(text);
            text = person.getFirstName() + " " + person.getLastName();
            data2.setText(text);
            int color = Math.round(DataCache.getInstance().getEventTypes().get(event.getEventType()));
            Drawable drawable = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).color(color);
            icon.setImageDrawable(drawable);
        }

        private void bind(Person person) {
            this.person = person;
            String text = person.getFirstName() + " " + person.getLastName();
            data1.setText(text);
            data2.setText("");
            Drawable drawable = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).color(Color.rgb(255, 192, 203));
            if (person.getGender().equals("m")) {
                drawable = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).color(Color.BLUE);
            }
            icon.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            if (viewType == 0) {
                Intent intent = new Intent(SearchActivity.this, EventActivity.class).putExtra("id", event.getEventID());
                startActivity(intent);
            } else {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class).putExtra("id", person.getPersonID());
                startActivity(intent);
            }
        }
    }
}