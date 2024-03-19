package com.example.unisphere.model;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {

    private String organizerImage;
    private String eventTitle;
    private String eventDate;
    private String eventPlace;
    private String inputTextLabel;
    private String inputTextButtonLabel;
    private String radioLabel;
    private List<String> radioOptions;
    private String radioButtonLabel;


    public Event() {
    }

    public Event(String organizerImage, String eventTitle, String eventDate, String eventPlace,
                 String inputTextLabel, String inputTextButtonLabel, String radioLabel,
                 List<String> radioOptions, String radioButtonLabel) {
        this.organizerImage = organizerImage;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventPlace = eventPlace;
        this.inputTextLabel = inputTextLabel;
        this.inputTextButtonLabel = inputTextButtonLabel;
        this.radioLabel = radioLabel;
        this.radioOptions = radioOptions;
        this.radioButtonLabel = radioButtonLabel;
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
                "organizerImage='" + organizerImage + '\'' +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventPlace='" + eventPlace + '\'' +
                ", inputTextLabel='" + inputTextLabel + '\'' +
                ", inputTextButtonLabel='" + inputTextButtonLabel + '\'' +
                ", radioLabel='" + radioLabel + '\'' +
                ", radioOptions=" + radioOptions +
                ", radioButtonLabel='" + radioButtonLabel + '\'' +
                '}';
    }
}
