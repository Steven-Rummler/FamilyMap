package com.stevenrummler.familymap;

import com.stevenrummler.familymap.data.DataCache;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import model.Event;
import model.Person;

public class DataCacheTest {
    DataCache cache = DataCache.getInstance();

    @Test
    public void FamilyPass() {
        if (cache.getPersons() != null) cache.getPersons().clear();

        // Add a family to the cache
        Person person = new Person("person", "", "", "", "", "father", "mother", "spouse");
        Person spouse = new Person("spouse", "", "", "", "", "", "", "person");
        Person father = new Person("father", "", "", "", "", "", "", "mother");
        Person mother = new Person("mother", "", "", "", "", "", "", "father");
        Person child1 = new Person("", "", "", "", "", "person", "spouse", "");
        Person child2 = new Person("", "", "", "", "", "person", "spouse", "");
        cache.getPersons().put("person", person);
        cache.getPersons().put("spouse", spouse);
        cache.getPersons().put("father", father);
        cache.getPersons().put("mother", mother);
        cache.getPersons().put("child1", child1);
        cache.getPersons().put("child2", child2);

        // Get spouse, parents, children
        Assert.assertNotNull(cache.getPersons().get(cache.getPersons().get("person").getSpouseID()));
        Assert.assertNotNull(cache.getPersons().get(cache.getPersons().get("person").getFatherID()));
        Assert.assertNotNull(cache.getPersons().get(cache.getPersons().get("person").getMotherID()));
        Assert.assertEquals(cache.findChildren("person").size(), 2);
    }

    @Test
    public void FamilyFail() {
        if (cache.getPersons() != null) cache.getPersons().clear();

        // Add a person to the cache
        Person person = new Person("person", "", "", "", "", "father", "mother", "spouse");
        cache.getPersons().put("person", person);

        // Get spouse, parents, children
        Assert.assertNull(cache.getPersons().get(cache.getPersons().get("person").getSpouseID()));
        Assert.assertNull(cache.getPersons().get(cache.getPersons().get("person").getFatherID()));
        Assert.assertNull(cache.getPersons().get(cache.getPersons().get("person").getMotherID()));
        Assert.assertEquals(cache.findChildren("person").size(), 0);
    }

    @Test
    public void FilterSimple() {
        for (String setting : cache.getSettings().keySet()) cache.getSettings().put(setting, true);

        // Add user and filter ids
        cache.getImmediateFamilyMales().clear();
        cache.getImmediateFamilyMales().add("m");

        // Add event to the cache
        Event m = new Event("m", "", "m", (float) 0, (float) 0, "", "", "", 0);
        cache.getEvents().clear();
        cache.getEvents().put("m", m);

        // Apply filters
        Assert.assertEquals(cache.getValidEvents().size(), 1);
        cache.getSettings().put("male", false);
        Assert.assertEquals(cache.getValidEvents().size(), 0);
    }

    @Test
    public void FilterComplex() {
        for (String setting : cache.getSettings().keySet()) cache.getSettings().put(setting, true);

        // Add user and filter ids
        cache.getImmediateFamilyMales().clear();
        cache.getImmediateFamilyFemales().clear();
        cache.getFatherSideMales().clear();
        cache.getFatherSideFemales().clear();
        cache.getMotherSideMales().clear();
        cache.getMotherSideFemales().clear();

        cache.getImmediateFamilyMales().add("m");
        cache.getImmediateFamilyFemales().add("f");
        cache.getFatherSideMales().add("fm");
        cache.getFatherSideFemales().add("ff");
        cache.getMotherSideMales().add("mm");
        cache.getMotherSideFemales().add("mf");

        // Add events to the cache
        Event m = new Event("m", "", "m", (float) 0, (float) 0, "", "", "", 0);
        Event f = new Event("f", "", "f", (float) 0, (float) 0, "", "", "", 0);
        Event f2 = new Event("f2", "", "f", (float) 0, (float) 0, "", "", "", 0);
        Event fm = new Event("fm", "", "fm", (float) 0, (float) 0, "", "", "", 0);
        Event ff = new Event("ff", "", "ff", (float) 0, (float) 0, "", "", "", 0);
        Event ff2 = new Event("ff2", "", "ff", (float) 0, (float) 0, "", "", "", 0);
        Event ff3 = new Event("ff3", "", "ff", (float) 0, (float) 0, "", "", "", 0);
        Event mm = new Event("mm", "", "mm", (float) 0, (float) 0, "", "", "", 0);
        Event mm2 = new Event("mm2", "", "mm", (float) 0, (float) 0, "", "", "", 0);
        Event mf = new Event("mf", "", "mf", (float) 0, (float) 0, "", "", "", 0);
        Event mf2 = new Event("mf2", "", "mf", (float) 0, (float) 0, "", "", "", 0);
        Event mf3 = new Event("mf3", "", "mf", (float) 0, (float) 0, "", "", "", 0);
        cache.getEvents().clear();
        cache.getEvents().put("m", m);
        cache.getEvents().put("f", f);
        cache.getEvents().put("f2", f2);
        cache.getEvents().put("fm", fm);
        cache.getEvents().put("ff", ff);
        cache.getEvents().put("ff2", ff2);
        cache.getEvents().put("ff3", ff3);
        cache.getEvents().put("mm", mm);
        cache.getEvents().put("mm2", mm2);
        cache.getEvents().put("mf", mf);
        cache.getEvents().put("mf2", mf2);
        cache.getEvents().put("mf3", mf3);

        // Apply gender filters
        Assert.assertEquals(cache.getValidEvents().size(), 12);
        cache.getSettings().put("male", false);
        Assert.assertEquals(cache.getValidEvents().size(), 8);
        cache.getSettings().put("female", false);
        Assert.assertEquals(cache.getValidEvents().size(), 0);
        cache.getSettings().put("male", true);
        Assert.assertEquals(cache.getValidEvents().size(), 4);
        cache.getSettings().put("female", true);
        Assert.assertEquals(cache.getValidEvents().size(), 12);

        // Apply side filters
        cache.getSettings().put("father", false);
        Assert.assertEquals(cache.getValidEvents().size(), 8);
        cache.getSettings().put("mother", false);
        Assert.assertEquals(cache.getValidEvents().size(), 3);
        cache.getSettings().put("father", true);
        Assert.assertEquals(cache.getValidEvents().size(), 7);
        cache.getSettings().put("mother", true);
        Assert.assertEquals(cache.getValidEvents().size(), 12);
    }

    @Test
    public void ChronoSimple() {
        // Reset filters
        for (String setting : cache.getSettings().keySet()) cache.getSettings().put(setting, true);

        // Add events to cache
        cache.getImmediateFamilyMales().add("person"); // personEvents only pulls valid events
        Event one = new Event("one", "", "person", (float) 0, (float) 0, "", "", "", 1);
        Event two = new Event("two", "", "person", (float) 0, (float) 0, "", "", "", 2);
        Event three = new Event("three", "", "person", (float) 0, (float) 0, "", "", "", 3);
        cache.getEvents().clear();
        cache.getEvents().put("three", three);
        cache.getEvents().put("two", two);
        cache.getEvents().put("one", one);

        ArrayList<Event> sorted = cache.lifeStory("person");
        Assert.assertEquals(sorted.get(0).getEventID(), "one");
        Assert.assertEquals(sorted.get(1).getEventID(), "two");
        Assert.assertEquals(sorted.get(2).getEventID(), "three");
    }

    @Test
    public void ChronoComplex() {
        // Reset filters
        for (String setting : cache.getSettings().keySet()) cache.getSettings().put(setting, true);

        // Add events to cache
        cache.getImmediateFamilyMales().add("person"); // personEvents only pulls valid events
        Event one = new Event("one", "", "person", (float) 0, (float) 0, "", "", "birth", 7);
        Event two = new Event("two", "", "person", (float) 0, (float) 0, "", "", "larriage", 4);
        Event three = new Event("three", "", "person", (float) 0, (float) 0, "", "", "marriage", 4);
        Event four = new Event("four", "", "person", (float) 0, (float) 0, "", "", "marriage", 5);
        Event five = new Event("five", "", "person", (float) 0, (float) 0, "", "", "death", 1);

        cache.getEvents().clear();
        cache.getEvents().put("three", three);
        cache.getEvents().put("five", five);
        cache.getEvents().put("one", one);
        cache.getEvents().put("four", four);
        cache.getEvents().put("two", two);

        ArrayList<Event> sorted = cache.lifeStory("person");
        Assert.assertEquals(sorted.get(0).getEventID(), "one"); // Birth always first
        Assert.assertEquals(sorted.get(1).getEventID(), "two"); // Same year, alpha by event type ↓
        Assert.assertEquals(sorted.get(2).getEventID(), "three");
        Assert.assertEquals(sorted.get(3).getEventID(), "four"); // Usually by year ↑
        Assert.assertEquals(sorted.get(4).getEventID(), "five"); // Death always last
    }

    @Test
    public void SearchEvents() {
        for (String setting : cache.getSettings().keySet()) cache.getSettings().put(setting, true);

        // Add events
        cache.getImmediateFamilyMales().add("person"); // search function only pulls valid events
        Event e1 = new Event("e1", "", "person", (float) 0, (float) 0, "country", "city", "marriage", 2020);
        Event e2 = new Event("e2", "", "person", (float) 0, (float) 0, "car", "cow", "wedding", 1920);
        cache.getEvents().clear();
        cache.getEvents().put("e1", e1);
        cache.getEvents().put("e2", e2);


        // Search and find results
        Assert.assertEquals(cache.searchEvents("z").size(), 0); // Nothing
        Assert.assertEquals(cache.searchEvents("1920").size(), 1); // Year
        Assert.assertEquals(cache.searchEvents("20").size(), 2); // Year
        Assert.assertEquals(cache.searchEvents("w").size(), 1); // Event type
        Assert.assertEquals(cache.searchEvents("c").size(), 2); // Country / City
        Assert.assertEquals(cache.searchEvents("ar").size(), 2); // County / Event Type


    }

    @Test
    public void SearchPersons() {
        // Add persons
        Person p1 = new Person("p1", "", "abc", "123", "", "", "", "");
        Person p2 = new Person("p2", "", "alex", "cod", "", "", "", "");
        cache.getPersons().clear();
        cache.getPersons().put("p1", p1);
        cache.getPersons().put("p2", p2);

        // Search and find results
        Assert.assertEquals(cache.searchPersons("a").size(), 2); // First name
        Assert.assertEquals(cache.searchPersons("ab").size(), 1); // First name
        Assert.assertEquals(cache.searchPersons("co").size(), 1); // Last name
        Assert.assertEquals(cache.searchPersons("z").size(), 0); // Nothing
    }
}
