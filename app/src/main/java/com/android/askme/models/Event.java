/*
 * ==================================================================
 *   Copyright (c) 2015 Dhanraj Padmashali (Humesis, Inc.)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *  ==================================================================
 */

package com.android.askme.models;

import com.android.askme.utils.GoogleDirection;
import com.google.android.gms.location.places.Place;

import org.w3c.dom.Document;

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

    public static class DirectionEvent{
        GoogleDirection googleDirection;
        Document document;
        Place source;
        Place dest;

        public DirectionEvent() {
        }

        public DirectionEvent(GoogleDirection googleDirection, Document document, Place source, Place dest) {
            this.googleDirection = googleDirection;
            this.document = document;
            this.source = source;
            this.dest = dest;
        }

        public GoogleDirection getGoogleDirection() {
            return googleDirection;
        }

        public Document getDocument() {
            return document;
        }

        public Place getSource() {
            return source;
        }

        public Place getDest() {
            return dest;
        }
    }
}
