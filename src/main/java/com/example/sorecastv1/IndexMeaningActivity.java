package com.example.sorecastv1;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sorecastv1.Adapter.SliderAdapter;

public class IndexMeaningActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;

    //For button functionality
    private Button mNextButton;
    private Button mPrevButton;
    private int mCurrentpage;

    private Button mFinButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_meaning);

        //coordinatorLayout = (CoordinatorLayout)findViewById(R.id.root_view);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// <- Stops back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("How it works");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mNextButton = (Button) findViewById(R.id.nextBtn);
        mPrevButton = (Button) findViewById(R.id.prevBtn);
        mFinButton = (Button) findViewById(R.id.finBtn);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentpage + 1);
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentpage - 1);
            }
        });

        mFinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void addDotsIndicator(int position){

        mDots = new TextView[6]; //This is manually assigned, change once no. of pages change
        mDotLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentpage = position;

            if (position==0){
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(false);
                mPrevButton.setVisibility(View.INVISIBLE);
                mFinButton.setEnabled(false);
                mFinButton.setVisibility(View.INVISIBLE);

                mNextButton.setText("Next");
                mPrevButton.setText("");
                mFinButton.setText("");
            } else if (position == mDots.length - 1){
                mNextButton.setEnabled(false);
                mNextButton.setVisibility(View.INVISIBLE);
                mPrevButton.setEnabled(true);
                mNextButton.setVisibility(View.VISIBLE);
                mFinButton.setEnabled(true);
                mFinButton.setVisibility(View.VISIBLE);

                mNextButton.setText("");
                mPrevButton.setText("Back");
                mFinButton.setText("Finish");
            } else {
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(true);
                mPrevButton.setVisibility(View.VISIBLE);
                mFinButton.setEnabled(false);
                mFinButton.setVisibility(View.INVISIBLE);

                mNextButton.setText("Next");
                mPrevButton.setText("Back");
                mFinButton.setText("");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    // So that other activities can load, check if this causes other issues
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




}
