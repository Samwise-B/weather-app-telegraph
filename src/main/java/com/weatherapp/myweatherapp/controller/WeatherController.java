package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.constraints.NotEmpty;  // or javax.validation.constraints.NotNull for older versions

import java.util.ArrayList;

@Controller
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  @GetMapping("/forecast/{city}")
  public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") @NotEmpty String city) {

    CityInfo ci = weatherService.forecastByCity(city);

    return ResponseEntity.ok(ci);
  }

  // TODO: given two city names, compare the length of the daylight hours and
  // return the city with the longest day
  @GetMapping("/compare-daylight/{city1}/{city2}")
  public ResponseEntity<CityInfo> compareDaylight(@PathVariable("city1") @NotEmpty String city1,
      @PathVariable("city2") String city2) {
    CityInfo ci = weatherService.compareDayLight(city1, city2);

    return ResponseEntity.ok(ci);
  }

  // TODO: given two city names, check which city its currently raining in
  @GetMapping("/compare-rain/{city1}/{city2}")
  public ResponseEntity<ArrayList<CityInfo>> compareRain(@PathVariable("city1") @NotEmpty String city1,
      @PathVariable("city2") String city2) {
    ArrayList<CityInfo> cityInfos = weatherService.compareRain(city1, city2);

    return ResponseEntity.ok(cityInfos);
  }
}
