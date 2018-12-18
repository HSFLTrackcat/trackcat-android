package de.mobcom.group3.gotrack.RecordList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.mobcom.group3.gotrack.Charts.BarChartFragment;
import de.mobcom.group3.gotrack.Charts.LineChartFragment;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.Recording_UI.CurrentPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class RecordPageViewerCharts extends Fragment {


    private List<Fragment> listFragments = new ArrayList<>();

    public RecordPageViewerCharts() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Layout inflaten*/
        View view = inflater.inflate(R.layout.fragment_page_viewer_charts, container, false);

        /*Anzeige der Höhenmeter*/
        double[] altitudeValues = getArguments().getDoubleArray("altitudeArray");
        Bundle bundleAltitide = new Bundle();
        bundleAltitide.putDoubleArray("array", altitudeValues);
        bundleAltitide.putString("title", "Höhenmeter");

        LineChartFragment lineFragAltitude = new LineChartFragment();
        lineFragAltitude.setArguments(bundleAltitide);
        listFragments.add(lineFragAltitude);

        /*Anzeige der Geschwindigkeit*/
        double[] speedValues = getArguments().getDoubleArray("speedArray");
        Bundle bundleSpeed = new Bundle();
        bundleSpeed.putDoubleArray("array", speedValues);
        bundleSpeed.putString("title", "Geschwindigkeit");

        LineChartFragment lineFragSpeed = new LineChartFragment();
        lineFragSpeed.setArguments(bundleSpeed);
        listFragments.add(lineFragSpeed);
        //  listFragments.add(barFrag);
        //  BarChartFragment barFrag = new BarChartFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = view.findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new RecordPageViewerCharts.ScreenSlidePagerAdapter(MainActivity.getInstance().getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);

        LinearLayout mLinearLayout = view.findViewById(R.id.indicator);

        /* create Indicator (little buttons) */
        CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle);
        mIndicator.setPageCount(listFragments.size());
        mIndicator.show();
        return view;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /* called on Swipe */
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }
    }
}
