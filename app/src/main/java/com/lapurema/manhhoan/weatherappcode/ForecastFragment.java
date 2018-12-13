package com.lapurema.manhhoan.weatherappcode;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lapurema.manhhoan.weatherappcode.Adapter.WeatherForecastAdapter;
import com.lapurema.manhhoan.weatherappcode.Common.Common;
import com.lapurema.manhhoan.weatherappcode.Model.WeatherForecastResult;
import com.lapurema.manhhoan.weatherappcode.Retrofit.IOpenWeatherMap;
import com.lapurema.manhhoan.weatherappcode.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView txt_city_name, txt_geo_coord;

    RecyclerView recycler_forecast;

    static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if (instance == null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        txt_city_name = (TextView) view.findViewById(R.id.txt_city_name_forecast);
        txt_geo_coord = (TextView) view.findViewById(R.id.txt_geo_coord_forecast);

        recycler_forecast = (RecyclerView) view.findViewById(R.id.recycle_forecast);
        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        getForeCastInfor();

        return view;

    }

    private void getForeCastInfor() {
        compositeDisposable.add(mService.getForecastWeatherByLatLon(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric"
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForeCastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", "" + throwable.getMessage());
                    }
                }));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void displayForeCastWeather(WeatherForecastResult weatherForecastResult) {

        txt_city_name.setText(new StringBuilder(weatherForecastResult.getCity().name));
        txt_geo_coord.setText(new StringBuilder(weatherForecastResult.getCity().coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(), weatherForecastResult);
        recycler_forecast.setAdapter(adapter);
    }

}
