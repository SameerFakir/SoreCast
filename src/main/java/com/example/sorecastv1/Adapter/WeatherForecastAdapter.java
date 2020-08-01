package com.example.sorecastv1.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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

        holder.txt_pain_desc.setText(new StringBuilder(calculatePainDesc(
                weatherForecastResult.list.get(position).main.getHumidity(),
                weatherForecastResult.list.get(position).main.getPressure(),
                weatherForecastResult.list.get(position).wind.getSpeed()
        )).toString());

        holder.txt_temperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position)
                .main.getTemp())).append("°C"));

        holder.txt_pain_index.setText(new StringBuilder(calculatePain(
                weatherForecastResult.list.get(position).main.getHumidity(),
                weatherForecastResult.list.get(position).main.getPressure(),
                weatherForecastResult.list.get(position).wind.getSpeed(),
                holder
                )).toString());



        // txt_pain_index.setText(new StringBuilder("Pain Index: ").append(calculatePain(
        //                                weatherResult.getMain().getHumidity(),
        //                                weatherResult.getMain().getTemp(),
        //                                weatherResult.getWind().getSpeed()
        //                                )).toString());

    }

    private String calculatePain(int inputHumidity, double inputPressure, double inputWind, MyViewHolder holder) {
        double pain, pressureRisk, humidityRisk, windRisk;
        String level = "Error";

        humidityRisk = (12/9)*(inputHumidity - 83);
        windRisk = 2*(inputWind - 4);
        pressureRisk = (-4/11)*(inputPressure - 1013);
        pain = 100 + humidityRisk + windRisk + pressureRisk;

        //Include some visuals (numberline) to show where they are on the pain scale

        if (pain < 75){
            level = "Tiny";
            holder.txt_pain_index.setTextColor(Color.parseColor("#134210"));
        }
        else if (pain >= 75 && pain < 90){
            level = "Small";
            holder.txt_pain_index.setTextColor(Color.parseColor("#69C700"));
        }
        else if (pain >= 90 && pain < 110){
            level = "Average";
            holder.txt_pain_index.setTextColor(Color.parseColor("#000000"));
        }
        else if (pain >= 110 && pain < 125){
            level = "High";
            holder.txt_pain_index.setTextColor(Color.parseColor("#FF9800"));
        }
        else if (pain >= 125){
            level = "Strong";
            holder.txt_pain_index.setTextColor(Color.parseColor("#B31A11"));
        }
        return level;
    }

    private String calculatePainDesc(int inputHumidity, double inputPressure, double inputWind) {
        double pain, pressureRisk, humidityRisk, windRisk;
        int roundedPain;
        String desc = "Error";

        humidityRisk = (12/9)*(inputHumidity - 83);
        windRisk = 2*(inputWind - 4);
        pressureRisk = (-4/11)*(inputPressure - 1013);
        pain = humidityRisk + windRisk + pressureRisk;


        if (pain > 0) {
            roundedPain = (int)Math.round(pain);
            desc = roundedPain + "% higher chance of pain.";
        }
        else if (pain < 0) {
            roundedPain = (int)Math.round(pain*-1);
            desc = roundedPain + "% lower chance of pain.";
        }
        else
            desc = "Average change of pain.";

        return desc;
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_date_time, txt_pain_desc, txt_temperature, txt_pain_index;
        ImageView img_weather;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_weather = (ImageView)itemView.findViewById(R.id.img_weather);
            txt_date_time = (TextView)itemView.findViewById(R.id.txt_date);
            txt_pain_desc = (TextView)itemView.findViewById(R.id.txt_pain_desc);
            txt_temperature = (TextView)itemView.findViewById(R.id.txt_temperature);
            txt_pain_index = (TextView)itemView.findViewById(R.id.txt_pain_index);

        }
    }
}
