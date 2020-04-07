package com.example.sorecastv1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sorecastv1.Common.Common;
import com.example.sorecastv1.Model.WeatherForecastResult;
import com.example.sorecastv1.R;
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Load icon

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_weather);

        holder.txt_date_time.setText(new StringBuilder(Common.convertUnixToDate(weatherForecastResult
                .list.get(position).dt)));

        holder.txt_description.setText(new StringBuilder(weatherForecastResult.list.get(position)
                .weather.get(0).getDescription()));

        holder.txt_temperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position)
                .main.getTemp())).append("°C"));

        holder.txt_pain_index.setText(new StringBuilder("Pain Index: ").append(calculatePain(
                weatherForecastResult.list.get(position).main.getHumidity(),
                weatherForecastResult.list.get(position).main.getPressure(),
                weatherForecastResult.list.get(position).wind.getSpeed()
                )).toString());

        // txt_pain_index.setText(new StringBuilder("Pain Index: ").append(calculatePain(
        //                                weatherResult.getMain().getHumidity(),
        //                                weatherResult.getMain().getTemp(),
        //                                weatherResult.getWind().getSpeed()
        //                                )).toString());

    }

    private String calculatePain(int inputHumidity, double inputPressure, double inputWind) {
        double pain, pressureRisk, humidityRisk, windRisk;
        String level = "Error";
        //tempWeight = -0.0501*(inputTemp) + 4.4575;
        //humidityWeight = 0.0155*(inputHumidity) + 2.7262;
        //windWeight = -0.0045*(inputWind) + 3.874;
        //pain = 0.8*tempWeight + 0.15*humidityWeight + 0.05*windWeight;

        humidityRisk = (12/9)*(inputHumidity - 83);
        windRisk = 2*(inputWind - 4);
        pressureRisk = (-4/11)*(inputPressure - 1013);
        pain = 100 + humidityRisk + windRisk + pressureRisk;

        // if age > 35 then * 0.80, else if age >...


        if (pain < 75){
            level = "Minimal";
        }
        else if (pain > 75 && pain < 90){
            level = "Minor";
        }
        else if (pain > 90 && pain < 110){
            level = "Average";
        }
        else if (pain > 110 && pain < 125){
            level = "Moderate";
        }
        else if (pain > 125){
            level = "Intense";
        }
        return level;

    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_date_time, txt_description, txt_temperature, txt_pain_index;
        ImageView img_weather;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_weather = (ImageView)itemView.findViewById(R.id.img_weather);
            txt_date_time = (TextView)itemView.findViewById(R.id.txt_date);
            txt_description = (TextView)itemView.findViewById(R.id.txt_description);
            txt_temperature = (TextView)itemView.findViewById(R.id.txt_temperature);
            txt_pain_index = (TextView)itemView.findViewById(R.id.txt_pain_index);

        }
    }
}
