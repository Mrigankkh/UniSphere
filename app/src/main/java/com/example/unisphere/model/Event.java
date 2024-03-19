package com.example.unisphere.model;

import java.io.Serializable;

public class Event implements Serializable {

    private String organizerImage;
    private String eventTitle;
    private String eventDate;
    private String eventPlace;

    public Event() {
    }

    public Event(String organizerImage, String eventTitle, String eventDate, String eventPlace) {
        this.organizerImage = organizerImage;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventPlace = eventPlace;
    }

    public String getOrganizerImage() {
        return organizerImage;
    }

    public void setOrganizerImage(String organizerImage) {
        this.organizerImage = organizerImage;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    @Override
    public String toString() {
        return "Event{" +
                "organizerImage='" + organizerImage + '\'' +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventPlace='" + eventPlace + '\'' +
                '}';
    }
}
