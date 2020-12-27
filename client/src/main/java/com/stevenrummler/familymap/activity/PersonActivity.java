package com.stevenrummler.familymap.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.stevenrummler.familymap.R;
import com.stevenrummler.familymap.data.DataCache;

import java.util.ArrayList;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        setTitle("Family Map: Person Details");
        Iconify.with(new FontAwesomeModule());

        String id = getIntent().getStringExtra("id");
        DataCache cache = DataCache.getInstance();

        person = cache.getPersons().get(id);
        if (person == null) finish();

        ArrayList<Event> lifeStory = cache.lifeStory(id);

        ArrayList<Person> family = new ArrayList<>();
        ArrayList<String> familyTitles = new ArrayList<>();
        Person spouse = cache.getPersons().get(person.getSpouseID());
        if (spouse != null) {
            family.add(spouse);
            familyTitles.add("Spouse");
        }
        Person father = cache.getPersons().get(person.getFatherID());
        if (father != null) {
            family.add(father);
            familyTitles.add("Father");
        }
        Person mother = cache.getPersons().get(person.getMotherID());
        if (mother != null) {
            family.add(mother);
            familyTitles.add("Mother");
        }
        ArrayList<Person> children = cache.findChildren(id);
        for (Person child : children) {
            family.add(child);
            familyTitles.add("Child");
        }

        LinearLayout first = findViewById(R.id.first);
        LinearLayout last = findViewById(R.id.last);
        LinearLayout gender = findViewById(R.id.gender);
        ExpandableListView listView = findViewById(R.id.list);

        String firstString = person.getFirstName();
        String lastString = person.getLastName();
        String genderString = (person.getGender().equals("m")) ? "Male" : "Female";

        ((TextView) first.findViewById(R.id.data)).setText(firstString);
        ((TextView) first.findViewById(R.id.label)).setText(R.string.firstLabel);
        ((TextView) last.findViewById(R.id.data)).setText(lastString);
        ((TextView) last.findViewById(R.id.label)).setText(R.string.lastLabel);
        ((TextView) gender.findViewById(R.id.data)).setText(genderString);
        ((TextView) gender.findViewById(R.id.label)).setText(R.string.genderLabel);

        listView.setAdapter(new PersonAdapter(lifeStory, family, familyTitles));
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

    // ExpandableListView Class

    private class PersonAdapter extends BaseExpandableListAdapter {
        private static final int STORY_POSITION = 0;
        private static final int FAMILY_POSITION = 1;

        ArrayList<Event> lifeStory;
        ArrayList<Person> family;
        ArrayList<String> familyTitles;

        public PersonAdapter(ArrayList<Event> lifeStory, ArrayList<Person> family, ArrayList<String> familyTitles) {
            this.lifeStory = lifeStory;
            this.family = family;
            this.familyTitles = familyTitles;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case STORY_POSITION:
                    return lifeStory.size();
                case FAMILY_POSITION:
                    return family.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case STORY_POSITION:
                    return lifeStory;
                case FAMILY_POSITION:
                    return family;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case STORY_POSITION:
                    return lifeStory.get(childPosition);
                case FAMILY_POSITION:
                    return family.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_group_title, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case STORY_POSITION:
                    titleView.setText(R.string.storyTitle);
                    break;
                case FAMILY_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            String data1, data2;
            Drawable drawable;
            switch (groupPosition) {
                case STORY_POSITION:
                    Event event = lifeStory.get(childPosition);
                    data1 = event.getEventType() + ": " + event.getCity() + ", " +
                            event.getCountry() + " (" + event.getYear() + ")";
                    data2 = person.getFirstName() + " " + person.getLastName();
                    ((TextView) itemView.findViewById(R.id.data1)).setText(data1);
                    ((TextView) itemView.findViewById(R.id.data2)).setText(data2);
                    float hue = DataCache.getInstance().getEventTypes().get(event.getEventType());
                    int color = Color.HSVToColor(new float[]{hue, 1, 1});
                    drawable = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).color(color);
                    ((ImageView) itemView.findViewById(R.id.icon)).setImageDrawable(drawable);

                    itemView.setOnClickListener((v) -> {
                        Intent intent = new Intent(PersonActivity.this, EventActivity.class).putExtra("id", event.getEventID());
                        startActivity(intent);
                    });
                    break;
                case FAMILY_POSITION:
                    Person member = family.get(childPosition);
                    data1 = member.getFirstName() + " " + member.getLastName();
                    data2 = familyTitles.get(childPosition);
                    ((TextView) itemView.findViewById(R.id.data1)).setText(data1);
                    ((TextView) itemView.findViewById(R.id.data2)).setText(data2);
                    drawable = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).color(Color.rgb(255,192,203));
                    if (member.getGender().equals("m")) {
                        drawable = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).color(Color.BLUE);
                    }
                    ((ImageView) itemView.findViewById(R.id.icon)).setImageDrawable(drawable);

                    itemView.setOnClickListener((v) -> {
                        Intent intent = new Intent(PersonActivity.this, PersonActivity.class).putExtra("id", member.getPersonID());
                        startActivity(intent);
                    });
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}