package com.lapurema.manhhoan.weatherappcode;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lapurema.manhhoan.weatherappcode.Common.Common;
import com.lapurema.manhhoan.weatherappcode.Model.WeatherResult;
import com.lapurema.manhhoan.weatherappcode.Retrofit.IOpenWeatherMap;
import com.lapurema.manhhoan.weatherappcode.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {


    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_tempreture, txt_date_time, txt_descrition, txt_wind, txt_geo_coord;

    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;


    static TodayFragment instance;

    public static TodayFragment getInstance() {
        if (instance == null) {
            instance = new TodayFragment();
        }
        return instance;
    }


    public TodayFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        img_weather = (ImageView) view.findViewById(R.id.img_weather);
        txt_city_name = (TextView) view.findViewById(R.id.txt_city_name);
        txt_date_time = (TextView) view.findViewById(R.id.txt_date_time);
        txt_descrition = (TextView) view.findViewById(R.id.txt_description);
        txt_geo_coord = (TextView) view.findViewById(R.id.txt_geo_coord);
        txt_humidity = (TextView) view.findViewById(R.id.txt_humidity);
        txt_pressure = (TextView) view.findViewById(R.id.txt_pressure);
        txt_sunrise = (TextView) view.findViewById(R.id.txt_sunrise);
        txt_sunset = (TextView) view.findViewById(R.id.txt_sunset);
        txt_tempreture = (TextView) view.findViewById(R.id.txt_temperature);
        txt_wind = (TextView) view.findViewById(R.id.txt_wind);

        weather_panel = (LinearLayout) view.findViewById(R.id.weather_panel);
        loading = (ProgressBar) view.findViewById(R.id.loading);

        getWeatherInformation();

        return view;
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    private void getWeatherInformation() {

        compositeDisposable.add(mService.getWeatherByLatLon(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png")
                                .toString()
                        ).into(img_weather);
                        txt_city_name.setText(weatherResult.getName());
                        txt_descrition.setText(new StringBuilder("Weather in ")
                                .append(weatherResult.getName())
                                .toString()
                        );
                        txt_tempreture.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                                .append("°C").toString());
                        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txt_geo_coord.setText(new StringBuilder("[")
                                .append(weatherResult.getCoord().toString())
                                .append("]").toString()
                        );
                        txt_wind.setText("Speed: " + new StringBuilder(String.valueOf(weatherResult.getWind().getSpeed())).append(" | ").append("Deg: ").append(String.valueOf(weatherResult.getWind().getDeg())).toString());
                        //Display panel
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

}
