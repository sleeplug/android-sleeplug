package es.uvigo.teleco.sleeplug;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import androidx.annotation.Nullable;
import es.uvigo.teleco.sleeplug.model.Cuna;

public class DiaryStatsFragment extends Fragment {

    private static final String TAG = "DiaryStatsFragment";

    private List<Cuna> cunas;

    private BarChart chart;

    public DiaryStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        chart = getView().findViewById(R.id.chart1);

        // Consulta a la RASPI para obtener todos los datos guardados en BDD
        DataFromPI dataFromPI = new DataFromPI();
        dataFromPI.execute();
    }

    private void setChartBarValues() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            float value = getCunaByDay(22, i);
            values.add(new BarEntry(i, value));
            Log.d(TAG, new BarEntry(i, value) + "");
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Data Set");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            chart.setData(data);
            chart.setFitBars(true);
        }

        chart.invalidate();

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chart.animateY(1500);

        chart.getLegend().setEnabled(false);

        chart.getData().setDrawValues(true);
    }

    private float getCunaByDay(int day, int hour) {
        float count = 0;
        for (int i = 0; i < cunas.size(); i++) {
            Cuna cuna = cunas.get(i);
            int dayOfMonth = cuna.getHoraCalendar().get(Calendar.DAY_OF_MONTH);
            int hourOfDay = cuna.getHoraCalendar().get(Calendar.HOUR_OF_DAY);
            //Log.d(TAG, cuna.getHoraCalendar().getTime().getDay() + "");
            if (dayOfMonth == day && hourOfDay == hour) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary_stats, container, false);
    }

    class DataFromPI extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(getActivity());
                    dialog.setTitle("Esperando a recibir datos");
                    dialog.setMessage("Cargando...");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.show();
                    dialog.setCancelable(false);
                }
            });
            String response = SleePlugApplication.getInstance().getCurrentConnection().writeAndReadLine("9");

            Type listType = new TypeToken<Collection<Cuna>>() {
            }.getType();
            Gson gson = new Gson();
            Collection<Cuna> responseCunas = gson.fromJson(response, listType);
            cunas = new ArrayList<>(responseCunas);

            Log.d(TAG, cunas.size() + "");
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            super.onProgressUpdate(voids);
        }

        @Override
        protected void onPostExecute(Void result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });

            setChartBarValues();

            chart.getData().setDrawValues(true);

            super.onPostExecute(result);
        }
    }
}
