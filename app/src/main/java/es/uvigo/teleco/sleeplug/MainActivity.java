package es.uvigo.teleco.sleeplug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import es.uvigo.teleco.sleeplug.utils.NotificationsUtils;

public class MainActivity extends AppCompatActivity {

    private TextView tvOnSleePlug;
    private TextView tvStats;
    private TextView tvSettings;
    private CardView cvStats;
    private CardView cvSettings;

    private Switch swOnSleePlug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onInit();
    }

    private void onInit() {
        tvOnSleePlug = findViewById(R.id.tvOnSleePlug);
        cvStats = findViewById(R.id.cvStats);
        tvStats = findViewById(R.id.tvStats);
        cvSettings = findViewById(R.id.cvSettings);
        tvSettings = findViewById(R.id.tvSettings);
        swOnSleePlug = findViewById(R.id.swOnSleePlug);

        cvStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stats = new Intent(view.getContext(), StatsActivity.class);
                view.getContext().startActivity(stats);
            }
        });

        cvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings = new Intent(view.getContext(), SettingsActivity.class);
                view.getContext().startActivity(settings);
            }
        });

        swOnSleePlug.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //NotificationsUtils.showIsManualWorking(buttonView.getContext());
                if (isChecked) {
                    SleePlugApplication.getInstance().getCurrentConnection().write("1");
                } else {
                    SleePlugApplication.getInstance().getCurrentConnection().write("0");
                }
            }
        });
    }
}
