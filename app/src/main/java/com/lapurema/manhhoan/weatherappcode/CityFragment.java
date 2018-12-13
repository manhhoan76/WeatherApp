package com.lapurema.manhhoan.weatherappcode;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.lapurema.manhhoan.weatherappcode.Common.Common;
import com.lapurema.manhhoan.weatherappcode.Model.WeatherResult;
import com.lapurema.manhhoan.weatherappcode.Retrofit.IOpenWeatherMap;
import com.lapurema.manhhoan.weatherappcode.Retrofit.RetrofitClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {

    private List<String> lstCities;
    private MaterialSearchBar searchBar;
    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_tempreture, txt_date_time, txt_descrition, txt_wind, txt_geo_coord;

    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;


    static CityFragment instance;

    public static CityFragment getInstance() {
        if (instance == null) {
            instance = new CityFragment();
        }
        return instance;
    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_city, container, false);

        img_weather = (ImageView) view.findViewById(R.id.img_weather_search);
        txt_city_name = (TextView) view.findViewById(R.id.txt_city_name_search);
        txt_date_time = (TextView) view.findViewById(R.id.txt_date_time_search);
        txt_descrition = (TextView) view.findViewById(R.id.txt_description_search);
        txt_geo_coord = (TextView) view.findViewById(R.id.txt_geo_coord_search);
        txt_humidity = (TextView) view.findViewById(R.id.txt_humidity_search);
        txt_pressure = (TextView) view.findViewById(R.id.txt_pressure_search);
        txt_sunrise = (TextView) view.findViewById(R.id.txt_sunrise_search);
        txt_sunset = (TextView) view.findViewById(R.id.txt_sunset_search);
        txt_tempreture = (TextView) view.findViewById(R.id.txt_temperature_search);
        txt_wind = (TextView) view.findViewById(R.id.txt_wind_search);

        weather_panel = (LinearLayout) view.findViewById(R.id.weather_panel_search);
        loading = (ProgressBar) view.findViewById(R.id.loading_search);

        searchBar = (MaterialSearchBar) view.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        new LoadCities().execute();

        return view;
    }


    private class LoadCities extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackgroundSimple() {

            lstCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream inputStream = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader inBufferedReader = new BufferedReader(reader);

                String readed;
                while ((readed = inBufferedReader.readLine()) != null)
                    builder.append(readed);
                lstCities = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {
                }.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("ok", lstCities.toString());
            return lstCities;
        }

        @Override
        protected void onSuccess(final List<String> listCities) {
            super.onSuccess(listCities);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : listCities) {
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                    Toast.makeText(getContext(), "onStateChange", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCities);
                }

                @Override
                public void onButtonClicked(int buttonCode) {
                    Toast.makeText(getContext(), "ClickButton", Toast.LENGTH_SHORT).show();
                }
            });
            searchBar.setLastSuggestions(listCities);
            loading.setVisibility(View.GONE);

        }
    }

    private void getWeatherInformation(String cityname) {
        compositeDisposable.add(mService.getWeatherByCityName(
                String.valueOf(cityname),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @SuppressLint("SetTextI18n")
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
}
