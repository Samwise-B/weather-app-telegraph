package com.weatherapp.myweatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CityInfo {

  @JsonProperty("address")
  String address;

  @JsonProperty("description")
  String description;

  @JsonProperty("currentConditions")
  CurrentConditions currentConditions;

  @JsonProperty("days")
  List<Days> days;

  public CityInfo() {
    this.currentConditions = new CurrentConditions(); // Ensuring it's always initialized
  }

  // Getters
  public String getAddress() {
    return this.address;
  }

  public String getDescription() {
    return this.description;
  }

  public CurrentConditions getCurrentConditions() {
    return this.currentConditions;
  }

  // Setters: Test Only
  public void setAddress(String address) {
    this.address = address;
  }

  public void setDescription(String desc) {
    this.description = desc;
  }

  public static class CurrentConditions {
    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("sunrise")
    String sunrise;

    @JsonProperty("sunset")
    String sunset;

    @JsonProperty("feelslike")
    String feelslike;

    @JsonProperty("humidity")
    String humidity;

    @JsonProperty("conditions")
    String conditions;

    // Getters
    public String getCurrentTemperature() {
      return this.currentTemperature;
    }

    public String getSunrise() {
      return this.sunrise;
    }

    public String getSunset() {
      return this.sunset;
    }

    public String getFeelsLike() {
      return this.feelslike;
    }

    public String getHumidity() {
      return this.conditions;
    }

    public String getConditions() {
      return this.conditions;
    }

    // Setters: test only (no validation)
    public void setCurrentTemperature(String tmp) {
      this.currentTemperature = tmp;
    }

    public void setSunrise(String time) {
      this.sunrise = time;
    }
  
    public void setSunset(String time) {
      this.sunset = time;
    }

    public void setFeelsLike(String feelslike) {
      this.feelslike = feelslike;
    }

    public void setHumidity(String humidity) {
      this.humidity = humidity;
    }
  
    public void setConditions(String conditions) {
      this.conditions = conditions;
    }
  }

  static class Days {

    @JsonProperty("datetime")
    String date;

    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("tempmax")
    String maxTemperature;

    @JsonProperty("tempmin")
    String minTemperature;

    @JsonProperty("conditions")
    String conditions;

    @JsonProperty("description")
    String description;

    // Getters
    public String getDatetime() {
      return this.date;
    }

    public String getTemp() {
      return this.currentTemperature;
    }

    public String getTempMax() {
      return this.maxTemperature;
    }

    public String getTempMin() {
      return this.minTemperature;
    }

    public String getConditions() {
      return this.conditions;
    }

    public String getDescription() {
      return this.description;
    }

    public void setDatetime(String date) {
      this.date = date;
    }

    public void setTemp(String temp) {
      this.currentTemperature = temp;
    }

    public void setTempMax(String tmpMax) {
      this.maxTemperature = tmpMax;
    }

    public void setTempMin(String tmpMin) {
      this.minTemperature = tmpMin;
    }

    public void setConditions(String conditions) {
      this.conditions = conditions;
    }

    public void setDescription(String desc) {
      this.description = desc;
    }
  }

}
