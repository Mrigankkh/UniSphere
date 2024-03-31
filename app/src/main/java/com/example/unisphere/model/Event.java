package com.example.unisphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {

    private String userId;
    private String eventId;
    private String eventTitle;
    private String eventDescription;
    private String eventImage;
    private String eventStartDate;
    private String eventEndDate;
    private String eventPlace;
    private String inputTextLabel;
    private String inputTextButtonLabel;
    private String radioLabel;
    private List<String> radioOptions;
    private String radioButtonLabel;
    private List<Comment> comments;
    private String pollResults;
    private String pollCsvLink;
    private String questionResults;
    private String questionCsvLink;


    public Event() {
        this.comments = new ArrayList<>();
    }

    public Event(String userId, String eventId, String eventTitle, String eventDescription, String eventImage, String eventStartDate, String eventEndDate, String eventPlace, String inputTextLabel, String inputTextButtonLabel, String radioLabel, List<String> radioOptions, String radioButtonLabel, List<Comment> comments) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventImage = eventImage;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventPlace = eventPlace;
        this.inputTextLabel = inputTextLabel;
        this.inputTextButtonLabel = inputTextButtonLabel;
        this.radioLabel = radioLabel;
        this.radioOptions = radioOptions;
        this.radioButtonLabel = radioButtonLabel;
        this.comments = comments;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getPollResults() {
        return pollResults;
    }

    public void setPollResults(String pollResults) {
        this.pollResults = pollResults;
    }

    public String getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(String questionResults) {
        this.questionResults = questionResults;
    }

    public String getPollCsvLink() {
        return pollCsvLink;
    }

    public void setPollCsvLink(String pollCsvLink) {
        this.pollCsvLink = pollCsvLink;
    }

    public String getQuestionCsvLink() {
        return questionCsvLink;
    }

    public void setQuestionCsvLink(String questionCsvLink) {
        this.questionCsvLink = questionCsvLink;
    }

}
