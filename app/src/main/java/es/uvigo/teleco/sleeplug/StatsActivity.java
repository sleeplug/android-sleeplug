package es.uvigo.teleco.sleeplug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import es.uvigo.teleco.sleeplug.model.Cuna;
import es.uvigo.teleco.sleeplug.utils.InputFilterMinMax;

public class StatsActivity extends AppCompatActivity {

    private static final int[] SLEEPLUG_COLORS = {
            Color.rgb(102, 204, 255), Color.rgb(204, 204, 255)
    };

    private static final String TAG = "StatsActivity";

    private List<Cuna> cunas;

    private BarChart chart;

    private LineChart chart2;

    private EditText textDay, textMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setupActionBar();

        textDay = findViewById(R.id.textDay);
        textDay.setFilters(new InputFilter[]{new InputFilterMinMax("1", "31")});

        Calendar calendarToday = Calendar.getInstance();
        int dayToday = calendarToday.get(Calendar.DAY_OF_MONTH);
        textDay.setText(dayToday + "");

        textMonth = findViewById(R.id.textMonth);
        textMonth.setFilters(new InputFilter[]{new InputFilterMinMax("1", "12")});

        int dayMonth = calendarToday.get(Calendar.MONTH);
        textMonth.setText(dayMonth + 1 + "");

        // Consulta a la RASPI para obtener todos los datos guardados en BDD
        DataFromPI dataFromPI = new DataFromPI();
        dataFromPI.execute();

        // CHART 1
        chart = findViewById(R.id.chart1);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(true);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chart.animateY(1500);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        textDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0 && cunas != null)
                    setChart1Values();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // CHART 2
        chart2 = findViewById(R.id.chart2);
        chart2.getDescription().setEnabled(false);
        chart2.setDrawGridBackground(false);
        XAxis xAxis2 = chart2.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        xAxis2.setDrawAxisLine(false);
        xAxis2.setGranularity(1f); // only intervals of 1 day

        YAxis leftAxis2 = chart2.getAxisLeft();
        leftAxis2.setLabelCount(10);
        leftAxis2.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis2 = chart2.getAxisRight();
        rightAxis2.setLabelCount(10);
        rightAxis2.setDrawGridLines(false);
        rightAxis2.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l2 = chart2.getLegend();
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);
        l2.setFormSize(8f);
        l2.setFormToTextSpace(4f);
        l2.setXEntrySpace(6f);

        chart2.animateX(1500);

        textMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0 && cunas != null)
                    setChart2Values();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setChart1Values() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            float valueAutomatic = getCunaByDay(Integer.parseInt(textDay.getText().toString()), i, 2);
            float valueManual = getCunaByDay(Integer.parseInt(textDay.getText().toString()), i, 1);
            if (valueAutomatic != 0 || valueManual != 0) {
                values.add(new BarEntry(i, new float[]{valueAutomatic, valueManual}));
            }
        }

        BarDataSet set1;

        set1 = new BarDataSet(values, "");
        set1.setStackLabels(new String[]{"Automático", "Manual"});
        set1.setColors(getColors());

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setData(data);

        chart.animateY(1500);

        chart.setFitBars(true);

        chart.invalidate();

        chart.getData().setDrawValues(true);
    }

    private void setChart2Values() {
        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 1; i < 31; i++) {
            float valueAutomatic = getCunaByMonth(Integer.parseInt(textMonth.getText().toString()), i, 2);
            values1.add(new Entry(i, (int) valueAutomatic));
            //values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(values1, "Automático");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setColor(SLEEPLUG_COLORS[0]);
        d1.setDrawValues(false);

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 1; i < 31; i++) {
            float valueManual = getCunaByMonth(Integer.parseInt(textMonth.getText().toString()), i, 1);
            values2.add(new Entry(i, (int) valueManual));
            //values2.add(new Entry(i, values1.get(i-1).getY() - 30));
        }

        LineDataSet d2 = new LineDataSet(values2, "Manual");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(SLEEPLUG_COLORS[1]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        // set data
        chart2.setData(new LineData(sets));

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        chart2.animateX(1500);

        chart2.getData().setDrawValues(true);
    }

    private void setChartBarValues() {
        setChart1Values();
        setChart2Values();
    }

    private float getCunaByDay(int day, int hour, int comando) {
        float count = 0;
        for (int i = 0; i < cunas.size(); i++) {
            Cuna cuna = cunas.get(i);
            int dayOfMonth = cuna.getHoraCalendar().get(Calendar.DAY_OF_MONTH);
            int hourOfDay = cuna.getHoraCalendar().get(Calendar.HOUR_OF_DAY);
            //Log.d(TAG, cuna.getHoraCalendar().getTime().getDay() + "");
            if (dayOfMonth == day && hourOfDay == hour && cuna.getComando() == comando) {
                count++;
            }
        }
        return count;
    }

    private float getCunaByMonth(int month, int day, int comando) {
        float count = 0;
        for (int i = 0; i < cunas.size(); i++) {
            Cuna cuna = cunas.get(i);
            int monthOfYear = cuna.getHoraCalendar().get(Calendar.MONTH) + 1;
            int dayOfMonth = cuna.getHoraCalendar().get(Calendar.DAY_OF_MONTH);
            //Log.d(TAG, cuna.getHoraCalendar().getTime().getDay() + "");
            if (monthOfYear == month && dayOfMonth == day && cuna.getComando() == comando) {
                count++;
            }
        }
        return count;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private int[] getColors() {
        // have as many colors as stack-values per entry
        int[] colors = new int[2];

        System.arraycopy(SLEEPLUG_COLORS, 0, colors, 0, 2);

        return colors;
    }

    class DataFromPI extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;

        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(StatsActivity.this);
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

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            super.onProgressUpdate(voids);
        }

        @Override
        protected void onPostExecute(Void result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
            setChartBarValues();
            super.onPostExecute(result);
        }
    }
}
