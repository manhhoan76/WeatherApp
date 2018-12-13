package com.lapurema.manhhoan.weatherappcode.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lapurema.manhhoan.weatherappcode.Common.Common;
import com.lapurema.manhhoan.weatherappcode.Model.WeatherForecastResult;
import com.lapurema.manhhoan.weatherappcode.R;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.getList().get(i).getWeather().get(0).getIcon())
                .append(".png")
                .toString()
        ).into(myViewHolder.img_weather);
        myViewHolder.txt_date_time.setText(
                new StringBuilder(Common.convertUnixToDate(weatherForecastResult.getList().get(i).getDt())));
        myViewHolder.txt_temparature.setText(
                new StringBuilder(String.valueOf(weatherForecastResult.getList().get(i).getMain().getTemp())).append(" °C"));
        myViewHolder.txt_description.setText(
                new StringBuilder(String.valueOf(weatherForecastResult.getList().get(i).getWeather().get(0).getDescription())));
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.getList().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_date_time, txt_description, txt_temparature;
        ImageView img_weather;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_weather = (ImageView) itemView.findViewById(R.id.img_weather_forecast);
            txt_date_time = (TextView) itemView.findViewById(R.id.txt_date_forecast);
            txt_description = (TextView) itemView.findViewById(R.id.txt_description_forecast);
            txt_temparature = (TextView) itemView.findViewById(R.id.txt_temperature_forecast);

        }
    }
}
