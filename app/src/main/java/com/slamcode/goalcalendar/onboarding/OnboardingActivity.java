package com.slamcode.goalcalendar.onboarding;

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

import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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

        private List<PageFragmentData> pagesDataList;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.initializePagesData();
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(pagesDataList.get(position));
        }

        @Override
        public int getCount() {
            return this.pagesDataList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.pagesDataList.get(position).title;
        }

        private void initializePagesData()
        {
            this.pagesDataList = new ArrayList<>();

            this.pagesDataList.add(new PageFragmentData("Page 1", "Description 1", R.drawable.calendar_check, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
            this.pagesDataList.add(new PageFragmentData("Page 2", "Description 2", R.drawable.calendar_range, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
            this.pagesDataList.add(new PageFragmentData("Page 3", "Description 3", R.drawable.calendar_text, ContextCompat.getColor(getApplicationContext(), R.color.listAccentColor)));
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
}
