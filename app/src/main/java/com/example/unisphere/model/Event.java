package com.example.unisphere.model;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {

    private String eventTitle;
    private String eventDescription;
    private String organizerImage;
    private String eventStartDate;
    private String eventEndDate;
    private String eventPlace;
    private String inputTextLabel;
    private String inputTextButtonLabel;
    private String radioLabel;
    private List<String> radioOptions;
    private String radioButtonLabel;


    public Event() {
    }

    public Event(String eventTitle, String eventDescription, String organizerImage, String eventStartDate, String eventEndDate, String eventPlace, String inputTextLabel, String inputTextButtonLabel, String radioLabel, List<String> radioOptions, String radioButtonLabel) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.organizerImage = organizerImage;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventPlace = eventPlace;
        this.inputTextLabel = inputTextLabel;
        this.inputTextButtonLabel = inputTextButtonLabel;
        this.radioLabel = radioLabel;
        this.radioOptions = radioOptions;
        this.radioButtonLabel = radioButtonLabel;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getOrganizerImage() {
        return organizerImage;
    }

    public void setOrganizerImage(String organizerImage) {
        this.organizerImage = organizerImage;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public String getInputTextLabel() {
        return inputTextLabel;
    }

    public void setInputTextLabel(String inputTextLabel) {
        this.inputTextLabel = inputTextLabel;
    }

    public String getInputTextButtonLabel() {
        return inputTextButtonLabel;
    }

    public void setInputTextButtonLabel(String inputTextButtonLabel) {
        this.inputTextButtonLabel = inputTextButtonLabel;
    }

    public String getRadioLabel() {
        return radioLabel;
    }

    public void setRadioLabel(String radioLabel) {
        this.radioLabel = radioLabel;
    }

    public List<String> getRadioOptions() {
        return radioOptions;
    }

    public void setRadioOptions(List<String> radioOptions) {
        this.radioOptions = radioOptions;
    }

    public String getRadioButtonLabel() {
        return radioButtonLabel;
    }

    public void setRadioButtonLabel(String radioButtonLabel) {
        this.radioButtonLabel = radioButtonLabel;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventTitle='" + eventTitle + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", organizerImage='" + organizerImage + '\'' +
                ", eventStartDate='" + eventStartDate + '\'' +
                ", eventEndDate='" + eventEndDate + '\'' +
                ", eventPlace='" + eventPlace + '\'' +
                ", inputTextLabel='" + inputTextLabel + '\'' +
                ", inputTextButtonLabel='" + inputTextButtonLabel + '\'' +
                ", radioLabel='" + radioLabel + '\'' +
                ", radioOptions=" + radioOptions +
                ", radioButtonLabel='" + radioButtonLabel + '\'' +
                '}';
    }
}
