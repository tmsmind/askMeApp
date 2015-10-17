/*
 * Copyright (c) 2015.
 */

package humesis.apps.humesisdirectionapp.models;

import com.google.android.gms.location.places.Place;

/**
 * Created by dhanraj on 25/09/15.
 */
public class Event {

    public enum EventType{
        PLACE_PICKED,
        NO_PLACE_PICKED,
        SOURCE_PICKED,
        DEST_PICKED,
        NEW_CAR
    }

    EventType eventType;

    public Event() {
    }

    public Event(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public static class PlaceEvent{
        Place place;
        EventType eventType;

        public PlaceEvent(Place place) {
            this.place = place;
        }

        public PlaceEvent(EventType eventType,Place place) {
            this.place = place;
            this.eventType = eventType;
        }

        public Place getPlace() {
            return place;
        }

        public EventType getEventType() {
            return eventType;
        }
    }

    public static class NewCarEvent{
        SavedCars cars;
        EventType eventType;

        public NewCarEvent(EventType eventType) {
            this.eventType = eventType;
        }

        public NewCarEvent(SavedCars cars, EventType eventType) {
            this.cars = cars;
            this.eventType = eventType;
        }

        public SavedCars getCars() {
            return cars;
        }

        public EventType getEventType() {
            return eventType;
        }
    }
}
