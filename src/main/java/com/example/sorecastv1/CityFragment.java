 package com.example.sorecastv1;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
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
    TextView txt_city_name, txt_humidity, txt_temperature, txt_pressure, txt_description, txt_date_time, txt_wind, txt_pain_index, txt_pain_desc;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

     static CityFragment instance;

     public static CityFragment getInstance() {
         if(instance==null)
             instance = new CityFragment();
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
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_city, container, false);

        img_weather = (ImageView)itemView.findViewById(R.id.img_weather);
        txt_city_name = (TextView)itemView.findViewById(R.id.txt_city_name);
        txt_humidity = (TextView)itemView.findViewById(R.id.txt_humidity);
        txt_pressure = (TextView)itemView.findViewById(R.id.txt_pressure);
        txt_temperature = (TextView)itemView.findViewById(R.id.txt_temperature);
        txt_description = (TextView)itemView.findViewById(R.id.txt_description);
        txt_date_time = (TextView)itemView.findViewById(R.id.txt_date_time);
        txt_wind = (TextView)itemView.findViewById(R.id.txt_wind);
        txt_pain_index = (TextView)itemView.findViewById(R.id.txt_pain_index);
        txt_pain_desc = (TextView)itemView.findViewById(R.id.txt_pain_desc);

        weather_panel = (LinearLayout)itemView.findViewById(R.id.weather_panel);

        loading = (ProgressBar)itemView.findViewById(R.id.loading);

        searchBar = (MaterialSearchBar)itemView.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        new loadCities().execute(); //Async Class To Load Cities List

        return itemView;
    }

     private class loadCities extends SimpleAsyncTask<List<String>> {

         @Override
         protected List<String> doInBackgroundSimple() {

             lstCities = new ArrayList<>();
             try {
                 StringBuilder builder = new StringBuilder();
                 InputStream is = getResources().openRawResource(R.raw.city_list);
                 GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                 InputStreamReader reader = new InputStreamReader(gzipInputStream);
                 BufferedReader in = new BufferedReader(reader);

                 String readed;

                 while ((readed = in.readLine()) != null)
                     builder.append(readed);
                 lstCities = new Gson().fromJson(builder.toString(),new TypeToken<List<String>>(){}.getType());
             } catch (IOException e) {
                 e.printStackTrace();
             }
             return lstCities;
         }

         @Override
         protected void onSuccess(final List<String> listCity) {
             super.onSuccess(listCity);

             searchBar.setEnabled(true);
             searchBar.addTextChangeListener(new TextWatcher() {
                 @Override
                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                 }

                 @Override
                 public void onTextChanged(CharSequence s, int start, int before, int count) {
                     List<String> suggest = new ArrayList<>();
                     for (String search : listCity){
                         if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
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

                 }

                 @Override
                 public void onSearchConfirmed(CharSequence text) {
                     getWeatherInformation(text.toString());

                     searchBar.setLastSuggestions(listCity);
                     searchBar.clearFocus();
                 }

                 @Override
                 public void onButtonClicked(int buttonCode) {

                 }
             });
             searchBar.setLastSuggestions(listCity);
             loading.setVisibility(View.GONE);

         }
     }

     private void getWeatherInformation(String cityName) {
         compositeDisposable.add(mService.getWeatherByCityName(cityName,
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
                         //Testing index pain
                         txt_pain_index.setText(new StringBuilder(calculatePain(
                                 weatherResult.getMain().getHumidity(),
                                 weatherResult.getMain().getPressure(),
                                 weatherResult.getWind().getSpeed()
                         )).toString());
                         //Testing index desc
                         txt_pain_desc.setText(new StringBuilder(calculatePainDesc(
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

         humidityRisk = (12/9)*(inputHumidity - 83);
         windRisk = 2*(inputWind - 4);
         pressureRisk = (-4/11)*(inputPressure - 1013);
         pain = 100 + humidityRisk + windRisk + pressureRisk;

         //Include some visuals (numberline) to show where they are on the pain scale

         if (pain < 75){
             level = "Tiny";
             txt_pain_index.setTextColor(getResources().getColor(R.color.tiny));
         }
         else if (pain >= 75 && pain < 90){
             level = "Small";
             txt_pain_index.setTextColor(getResources().getColor(R.color.small));
         }
         else if (pain >= 90 && pain < 110){
             level = "Average";
             txt_pain_index.setTextColor(getResources().getColor(R.color.average));
         }
         else if (pain >= 110 && pain < 125){
             level = "High";
             txt_pain_index.setTextColor(getResources().getColor(R.color.high));
         }
         else if (pain >= 125){
             level = "Strong";
             txt_pain_index.setTextColor(getResources().getColor(R.color.strong));
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

         //Include some visuals (numberline) to show where they are on the pain scale


         if (pain > 0) {
             roundedPain = (int)Math.round(pain);
             desc = "Expect " + roundedPain + "% higher chance of pain";
         }
         else if (pain < 0) {
             roundedPain = (int)Math.round(pain*-1);
             desc = "Expect " + roundedPain + "% lower chance of pain";
         }
         else
             desc = "Expect average change of pain";

         return desc;
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
