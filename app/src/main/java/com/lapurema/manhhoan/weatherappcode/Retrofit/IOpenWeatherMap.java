package com.lapurema.manhhoan.weatherappcode.Retrofit;

import com.lapurema.manhhoan.weatherappcode.Model.WeatherForecastResult;
import com.lapurema.manhhoan.weatherappcode.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByCityName(
            @Query("q") String cityName,
            @Query("appid") String appid,
            @Query("units") String units
    );

    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLon(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("appid") String appid,
            @Query("units") String units
    );

    @GET("forecast")
    Observable<WeatherForecastResult> getForecastWeatherByLatLon(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("appid") String appid,
            @Query("units") String units
    );
}
