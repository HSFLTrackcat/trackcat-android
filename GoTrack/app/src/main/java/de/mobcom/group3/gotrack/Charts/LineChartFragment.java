package de.mobcom.group3.gotrack.Charts;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mobcom.group3.gotrack.R;

import java.util.Arrays;

public class LineChartFragment extends Fragment {
    private static final String PREF_DARK_THEME = "dark_theme";
    private final int LOWER_BOUNDARY_X = 0;
    private final int LOWER_BOUNDARY_Y = 0;
    private View view;
    private XYPlot plot;
    private int pointPerSegment = 10;
    private int incrementStepsX = 1;
    private double incrementStepsY = 10;
    private Number[] series1Numbers;
    private double[] values;
    private String rangeTitle;
    private String title;
    private LineAndPointFormatter series1Format;;

    public LineChartFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Checks current Theme and uses the correct xml and formatter */
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PREF_DARK_THEME, false)){
            view = inflater.inflate(R.layout.fragment_line_chart_dark, container, false);
            series1Format =
                    new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels_dark);
        }else{
            view = inflater.inflate(R.layout.fragment_line_chart, container, false);
            series1Format =
                    new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels);
        }

        if (getArguments() != null) {
            /* Gets all Arguments from Bundle */
            title = getArguments().getString("title");
            rangeTitle = getArguments().getString("rangeTitle");
            values = getArguments().getDoubleArray("array");
        } else {
            /* Setting default vals, if plot is not used with bundle arguments */
            title = "Series1";
            rangeTitle = "Range";
            values = new double[]{1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
        }

        /* Sets every value as Value in number Array */
        series1Numbers = new Number[values.length];
        double maxValue=0;
        for (int i = 0; i < series1Numbers.length; i++) {
            series1Numbers[i] = (int) Math.round(values[i]);
            if (maxValue < values[i]){
                maxValue = values[i];
            }
        }

        /* Incrementing Steps are created dynamically */
        pointPerSegment = series1Numbers.length;
        incrementStepsY = maxValue / 5;
        incrementStepsX = series1Numbers.length/5;

        /* Turning Arrays to XYSeries */
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, title);

        /* Smoothing curves */
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(pointPerSegment, CatmullRomInterpolator.Type.Centripetal));
        series1Format.setPointLabelFormatter(null);

        /* Getting in xml defined Plot */
        plot = view.findViewById(R.id.linePlot);
        plot.setTitle(title);
        plot.setRangeLabel(rangeTitle);

        /* Add a new Series to the XYPlot */
        plot.addSeries(series1, series1Format);

        /* Lower Boundaries are set to 0 as defined in final Variables */
        plot.setDomainLowerBoundary(LOWER_BOUNDARY_X, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(LOWER_BOUNDARY_Y, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, incrementStepsX);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, incrementStepsY);

        return view;
    }
}
