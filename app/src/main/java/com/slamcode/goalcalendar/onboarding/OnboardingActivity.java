package com.slamcode.goalcalendar.onboarding;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private PageChangeListener pageChangeListener;

    private List<PageFragmentData> pagesDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        this.initializePagesData();
        this.setupButtons();

        pageChangeListener  = new PageChangeListener();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.onboarding_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(pageChangeListener);
    }

    @Override
    protected void onStop() {
        mViewPager.removeOnPageChangeListener(pageChangeListener);
        super.onStop();
    }

    private void setupButtons()
    {
        Button skipButton = (Button) this.findViewById(R.id.onboarding_skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        final Button finishButton = (Button) this.findViewById(R.id.onboarding_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        ImageButton nextButton = (ImageButton) this.findViewById(R.id.onboarding_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNextPage();
            }
        });
    }

    private void moveNextPage()
    {
        int curPosition = mViewPager.getCurrentItem();
        if(curPosition < pagesDataList.size()-1)
            mViewPager.setCurrentItem(curPosition + 1);
    }

    private void finishOnboarding()
    {
        Intent intent = new Intent(this, MonthlyGoalsActivity.class);
        startActivity(intent);
    }

    private void initializePagesData()
    {
        this.pagesDataList = new ArrayList<>();

        this.pagesDataList.add(new PageFragmentData("Page 1", "Description 1", R.drawable.ic_calendar_check_white_96dp, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        this.pagesDataList.add(new PageFragmentData("Page 2", "Description 2", R.drawable.ic_calendar_range_white_48dp, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        this.pagesDataList.add(new PageFragmentData("Page 3", "Description 3", R.drawable.ic_calendar_text_white_48dp, ContextCompat.getColor(getApplicationContext(), R.color.listAccentColor)));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_PAGE_TITLE = "page_title";
        private static final String ARG_PAGE_DESCRIPTION = "page_description";
        private static final String ARG_PAGE_IMAGE_RESOURCE_ID = "page_image_res_id";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(PageFragmentData data) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PAGE_DESCRIPTION, data.description);
            args.putString(ARG_PAGE_TITLE, data.title);
            args.putInt(ARG_PAGE_IMAGE_RESOURCE_ID, data.imageResourceId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_onboarding_fragment, container, false);

            TextView titleTextView = (TextView) rootView.findViewById(R.id.onboarding_fragment_title_label);
            titleTextView.setText(this.getArguments().getString(ARG_PAGE_TITLE));

            TextView descTextView = (TextView) rootView.findViewById(R.id.onboarding_fragment_description_label);
            descTextView.setText(this.getArguments().getString(ARG_PAGE_DESCRIPTION));

            ImageView imageView = (ImageView) rootView.findViewById(R.id.onboarding_fragment_image);
            imageView.setImageResource(this.getArguments().getInt(ARG_PAGE_IMAGE_RESOURCE_ID));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(pagesDataList.get(position));
        }

        @Override
        public int getCount() {
            return pagesDataList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagesDataList.get(position).title;
        }
    }

    private class PageFragmentData{

        private int imageResourceId;

        private String title;

        private String description;

        private int pageColorValue;

        private PageFragmentData(String title, String description, int imageResourceId, int pageColorValue) {
            this.title = title;
            this.description = description;
            this.imageResourceId = imageResourceId;
            this.pageColorValue = pageColorValue;
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener
    {
        private ArgbEvaluator evaluator;
        private LinearLayout indicatorsLayout;

        public PageChangeListener() {

            this.evaluator = new ArgbEvaluator();
            this.updateIndicators(0);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int colorToUpdate = (Integer) evaluator.evaluate(
                    positionOffset,
                    pagesDataList.get(position).pageColorValue,
                    pagesDataList.get(position == pagesDataList.size() -1 ? position : position + 1).pageColorValue);
            mViewPager.setBackgroundColor(colorToUpdate);
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setBackgroundColor(pagesDataList.get(position).pageColorValue);
            this.updateIndicators(position);
            this.updateButtons(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        private void updateIndicators(int position) {
            if(indicatorsLayout == null)
                indicatorsLayout = (LinearLayout) findViewById(R.id.onboarding_indicators_layout);

            for(int i =0; i < pagesDataList.size(); i++) {
                ImageView indicatorView = (ImageView) indicatorsLayout.getChildAt(i);
                if(i == position)
                    indicatorView.setBackgroundResource(R.drawable.page_viewer_indicator_selected);
                else
                    indicatorView.setBackgroundResource(R.drawable.page_viewer_indicator_unselected);
            }
        }

        private void updateButtons(int position) {

            ImageButton nextButton = (ImageButton) findViewById(R.id.onboarding_next_button);
            Button finishButton = (Button) findViewById(R.id.onboarding_finish_button);

            if(position == pagesDataList.size()-1)
            {
                nextButton.setVisibility(View.GONE);
                finishButton.setVisibility(View.VISIBLE);
            }
            else
            {
                nextButton.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.GONE);
            }
        }

    }
}
