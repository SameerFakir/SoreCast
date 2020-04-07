package com.example.sorecastv1;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sorecastv1.Common.Common;
import com.example.sorecastv1.Model.WeatherResult;
import com.example.sorecastv1.Retrofit.IOpenWeatherMap;
import com.example.sorecastv1.Retrofit.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_temperature, txt_pressure, txt_description, txt_date_time, txt_wind, txt_geo_coord, txt_pain_index;
    LinearLayout weather_panel;
    ProgressBar loading;


    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;


    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if(instance==null)
            instance = new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);

        img_weather = (ImageView)itemView.findViewById(R.id.img_weather);
        txt_city_name = (TextView)itemView.findViewById(R.id.txt_city_name);
        txt_humidity = (TextView)itemView.findViewById(R.id.txt_humidity);
        txt_sunrise = (TextView)itemView.findViewById(R.id.txt_sunrise);
        txt_sunset = (TextView)itemView.findViewById(R.id.txt_sunset);
        txt_pressure = (TextView)itemView.findViewById(R.id.txt_pressure);
        txt_temperature = (TextView)itemView.findViewById(R.id.txt_temperature);
        txt_description = (TextView)itemView.findViewById(R.id.txt_description);
        txt_date_time = (TextView)itemView.findViewById(R.id.txt_date_time);
        txt_wind = (TextView)itemView.findViewById(R.id.txt_wind);
        txt_geo_coord = (TextView)itemView.findViewById(R.id.txt_geo_coord);
        //Testing Pain index
        txt_pain_index = (TextView)itemView.findViewById(R.id.txt_pain_index);

        weather_panel = (LinearLayout)itemView.findViewById(R.id.weather_panel);

        loading = (ProgressBar)itemView.findViewById(R.id.loading);
        
        getWeatherInformation();

        return itemView;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.API_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        //Load Image

                        // I should change "https://openweathermap.org/img/wn/" to "https://openweathermap.org/img/w/" If png doesnt load

                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                        .append(".png").toString()).into(img_weather);

                        //Load Weather Data

                        txt_city_name.setText(weatherResult.getName());
                        txt_description.setText(new StringBuilder("Weather in ")
                                .append(weatherResult.getName()).toString());
                        txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txt_geo_coord.setText(new StringBuilder("[").append(weatherResult.getCoord().toString()).append("]").toString());
                        //Testing index pain
                        txt_pain_index.setText(new StringBuilder("Pain Index: ").append(calculatePain(
                                weatherResult.getMain().getHumidity(),
                                weatherResult.getMain().getPressure(),
                                weatherResult.getWind().getSpeed()
                                )).toString());
                        //Wind test
                        txt_wind.setText(new StringBuilder(String.valueOf(weatherResult.getWind().getSpeed())).append(" meters/s").toString());
                        //Display Panel

                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);





                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(),""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })

        );
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

        //Include some visuals (numberline) to show where they are on the pain scale


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

        /*if (pain < 2 ){
            level = "Very Low";
        }
        else if (pain < 4){
            level = "Low";
        }
        else if (pain < 6){
            level = "Average";
        }
        else if (pain < 8){
            level = "High";
        }
        else if (pain < 10){
            level = "Very High";
        }
        //level = level + " " + pain;
        return level;
         */
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
}
