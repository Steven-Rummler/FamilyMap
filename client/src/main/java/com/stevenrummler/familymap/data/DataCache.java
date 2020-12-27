package com.stevenrummler.familymap.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.internal.LinkedTreeMap;
import com.stevenrummler.familymap.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.AuthToken;
import model.Event;
import model.Person;

public class DataCache {
    private static DataCache instance;

    private AuthToken authToken;
    private String userPersonId;
    private Person user;
    private final Map<String, Boolean> settings = new HashMap<>();

    private final Map<String, Person> persons = new HashMap<>();
    private final Map<String, Event> events = new HashMap<>();

    private final Set<String> immediateFamilyMales = new HashSet<>();
    private final Set<String> immediateFamilyFemales = new HashSet<>();
    private final Set<String> fatherSideMales = new HashSet<>();
    private final Set<String> fatherSideFemales = new HashSet<>();
    private final Set<String> motherSideMales = new HashSet<>();
    private final Set<String> motherSideFemales = new HashSet<>();

    private final Map<String, Float> eventTypes = new HashMap<>();

    // Settings default to true
    private DataCache() {
        settings.put("life", true);
        settings.put("tree", true);
        settings.put("spouse", true);
        settings.put("father", true);
        settings.put("mother", true);
        settings.put("male", true);
        settings.put("female", true);
    }

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    // Populating Data

    public void setPersons(ArrayList<Object> persons) {
        this.persons.clear();
        for (Object personObject : persons) {
            LinkedTreeMap map = (LinkedTreeMap) personObject;
            String associatedUsername = map.get("associatedUsername").toString();
            String personID = map.get("personID").toString();
            String firstName = map.get("firstName").toString();
            String lastName = map.get("lastName").toString();
            String gender = map.get("gender").toString();
            String fatherID = "";
            String motherID = "";
            String spouseID = "";
            if (map.containsKey("fatherID")) fatherID = map.get("fatherID").toString();
            if (map.containsKey("motherID"))  motherID = map.get("motherID").toString();
            if (map.containsKey("spouseID"))  spouseID = map.get("spouseID").toString();
            Person person = new Person(personID, associatedUsername, firstName, lastName, gender, fatherID, motherID, spouseID);
            this.persons.put(person.getPersonID(), person);
        }
        user = this.persons.get(userPersonId);
        organizePeople();
    }

    public void setEvents(ArrayList<Object> events) {
        this.events.clear();
        for (Object eventObject : events) {
            LinkedTreeMap map = (LinkedTreeMap) eventObject;
            String associatedUsername = map.get("associatedUsername").toString();
            String eventID = map.get("eventID").toString();
            String personID = map.get("personID").toString();
            Float latitude = ((Double) map.get("latitude")).floatValue();
            Float longitude = ((Double) map.get("longitude")).floatValue();
            String country = map.get("country").toString();
            String city = map.get("city").toString();
            String eventType = map.get("eventType").toString();
            Integer year = ((Double) map.get("year")).intValue();
            Event event = new Event(eventID, associatedUsername, personID, latitude, longitude, country, city, eventType, year);
            this.events.put(event.getEventID(), event);
        }
        setupEventTypes();
    }

    private void organizePeople() {
        //  Add the person and their spouse to the immediate family
        if (user.getGender().equals("m")) {
            immediateFamilyMales.add(user.getPersonID());
            if (persons.containsKey(user.getSpouseID()))
                immediateFamilyFemales.add(user.getSpouseID());
        } else {
            immediateFamilyFemales.add(user.getPersonID());
            if (persons.containsKey(user.getSpouseID()))
                immediateFamilyMales.add(user.getSpouseID());
        }

        // Organize ancestors
        if (persons.containsKey(user.getFatherID()))
            organizeFatherSide(persons.get(user.getFatherID()));
        if (persons.containsKey(user.getMotherID()))
            organizeMotherSide(persons.get(user.getMotherID()));
    }

    private void organizeFatherSide(Person person) {
        if (person == null) return;
        if (person.getGender().equals("m")) {
            fatherSideMales.add(person.getPersonID());
        } else {
            fatherSideFemales.add(person.getPersonID());
        }
        if (persons.containsKey(person.getFatherID())) {
            organizeFatherSide(persons.get(person.getFatherID()));
        }
        if (persons.containsKey(person.getMotherID())) {
            organizeFatherSide(persons.get(person.getMotherID()));
        }
    }

    private void organizeMotherSide(Person person) {
        if (person == null) return;
        if (person.getGender().equals("m")) {
            motherSideMales.add(person.getPersonID());
        } else {
            motherSideFemales.add(person.getPersonID());
        }
        if (persons.containsKey(person.getFatherID())) {
            organizeMotherSide(persons.get(person.getFatherID()));
        }
        if (persons.containsKey(person.getMotherID())) {
            organizeMotherSide(persons.get(person.getMotherID()));
        }
    }

    private void setupEventTypes() {
        Set<String> availableTypes = new HashSet<>();
        for (Event event : new ArrayList<>(events.values())) {
            availableTypes.add(event.getEventType());
        }
        float hue = (float) 0;
        float increment = (float) (360 / availableTypes.size());
        for (String availableType : availableTypes) {
            eventTypes.put(availableType, hue);
            hue += increment;
        }
    }

    // Searching and Filtering

    public Event findEvent(String personId, String eventType) {
        for (Event event : events.values()) {
            if (event.getPersonID().equals(personId) && event.getEventType().equals(eventType)) {
                return event;
            }
        }
        return null;
    }

    public ArrayList<Event> lifeStory(String personID) {
        ArrayList<Event> personEvents = new ArrayList<>();
        ArrayList<Event> valid = getValidEvents();
        for (Event event : valid) {
            if (event.getPersonID().equals(personID)) {
                personEvents.add(event);
            }
        }
        Collections.sort(personEvents,
                this::compareEvents);
        return personEvents;
    }

    private int compareEvents(Event event, Event other) {
        int eventYear = event.getYear();
        int otherYear = other.getYear();
        String eventType = event.getEventType().toLowerCase();
        String otherType = other.getEventType().toLowerCase();

        if (eventType.equals("birth")) return -1;
        if (otherType.equals("birth")) return 1;
        if (eventType.equals("death")) return 1;
        if (otherType.equals("death")) return -1;

        if (eventYear < otherYear) return -1;
        if (eventYear > otherYear) return 1;
        return eventType.compareTo(otherType);
    }

    public ArrayList<Person> findChildren(String personID) {
        ArrayList<Person> children = new ArrayList<>();
        for (Person person : persons.values()) {
            if (person.getFatherID().equals(personID) || person.getMotherID().equals(personID)) {
                children.add(person);
            }
        }
        return children;
    }

    public ArrayList<Event> searchEvents(String query) {
        ArrayList<Event> eventResults = new ArrayList<>();
        ArrayList<Event> valid = getValidEvents();
        if (settings.get("father"));
            query = query.toLowerCase();
        for (Event event : valid) {
            if (event.getCountry().toLowerCase().contains(query) || event.getCity().toLowerCase().contains(query) || event.getEventType().toLowerCase().contains(query) || event.getYear().toString().contains(query)) {
                eventResults.add(event);
            }
        }
        return eventResults;
    }

    public ArrayList<Person> searchPersons(String query) {
        ArrayList<Person> personResults = new ArrayList<>();
        query = query.toLowerCase();
        for (Person person : persons.values()) {
            if (person.getFirstName().toLowerCase().contains(query) || person.getLastName().toLowerCase().contains(query)) {
                personResults.add(person);
            }
        }
        return personResults;
    }

    public ArrayList<Event> getValidEvents() {
        ArrayList<Event> valid = new ArrayList<>();
        for (Event event : events.values()) {
            if (settings.get("father")) {
                if (settings.get("male"))
                    if (fatherSideMales.contains(event.getPersonID())) valid.add(event);
                if (settings.get("female"))
                    if (fatherSideFemales.contains(event.getPersonID())) valid.add(event);
            }
            if (settings.get("mother")) {
                if (settings.get("male"))
                    if (motherSideMales.contains(event.getPersonID())) valid.add(event);
                if (settings.get("female"))
                    if (motherSideFemales.contains(event.getPersonID())) valid.add(event);
            }
            if (settings.get("male"))
                if (immediateFamilyMales.contains(event.getPersonID())) valid.add(event);
            if (settings.get("female"))
                if (immediateFamilyFemales.contains(event.getPersonID())) valid.add(event);
        }
        return valid;
    }

    // Getters and Setters

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public void setUserPersonId(String userPersonId) {
        this.userPersonId = userPersonId;
    }

    public Map<String, Boolean> getSettings() {
        return settings;
    }

    public Map<String, Person> getPersons() {
        return persons;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public Map<String, Float> getEventTypes() {
        return eventTypes;
    }

    public Set<String> getImmediateFamilyMales() {
        return immediateFamilyMales;
    }

    public Set<String> getImmediateFamilyFemales() {
        return immediateFamilyFemales;
    }

    public Set<String> getFatherSideMales() {
        return fatherSideMales;
    }

    public Set<String> getFatherSideFemales() {
        return fatherSideFemales;
    }

    public Set<String> getMotherSideMales() {
        return motherSideMales;
    }

    public Set<String> getMotherSideFemales() {
        return motherSideFemales;
    }
}
