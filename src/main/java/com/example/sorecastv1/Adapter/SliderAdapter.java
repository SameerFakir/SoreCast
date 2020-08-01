package com.example.sorecastv1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.sorecastv1.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_image = {
            R.drawable.weather,
            R.drawable.calculate,
            R.drawable.humidity,
            R.drawable.wind_speed,
            R.drawable.air_pressure,
            R.drawable.scale
    };

    public String[] slide_headings = {
            "WEATHER",
            "CALCULATION",
            "HUMIDITY",
            "WIND SPEED",
            "AIR PRESSURE",
            "SCALE"
    };

    public String[] slide_description = {
            "We are able to estimate the chance of pain using the weather.",
            "This estimation is calculated through comparing current weather factors.",
            "Higher humidity increases the chance of pain.",
            "Faster wind speed increases the chance of pain.",
            "Lower air pressure increases the chance of pain.",
            "Chances of pain are put into the following five categories."
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_description);


        slideImageView.setImageResource(slide_image[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_description[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout) object);
    }
}
