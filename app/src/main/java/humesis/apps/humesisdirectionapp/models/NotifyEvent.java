/*
 * Copyright (c) 2015.
 */

package humesis.apps.humesisdirectionapp.models;

import com.google.android.gms.location.places.Place;

/**
 * Created by dhanraj on 25/09/15.
 */
public class NotifyEvent {

    public enum NotifyType{
        PLACE_PICKED,
        NO_PLACE_PICKED
    }

    String clip;

    Place place;

    NotifyType mNotifyType;

    NotifyEvent event;

    public NotifyEvent(NotifyEvent event) {
        this.event = event;
    }

    public NotifyEvent(NotifyType mNotifyType, String clip) {
        this.mNotifyType = mNotifyType;
        this.clip = clip;
    }

    public NotifyEvent(Place place, NotifyType mNotifyType) {
        this.place = place;
        this.mNotifyType = mNotifyType;
    }

    public NotifyEvent(NotifyType mNotifyType) {
        this.mNotifyType = mNotifyType;
    }

    public NotifyType getNotifyType() {
        return mNotifyType;
    }

    public String getClip() {
        return clip;
    }

    public NotifyEvent getEvent() {
        return event;
    }

    public void setClip(String clip) {
        this.clip = clip;
    }

    public void setmNotifyType(NotifyType mNotifyType) {
        this.mNotifyType = mNotifyType;
    }

    public Place getPlace() {
        return place;
    }
}
