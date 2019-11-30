package cn.surine.dashboardview;

import android.os.Build;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DashBoardView dashBoardView = findViewById(R.id.dash);
        dashBoardView.setBanAdaptive(false);
        dashBoardView.setAnimDuration(300);
        dashBoardView.setStartDegree(30);
        dashBoardView.setEndDegree(300);
        dashBoardView.setInterpolator(new OvershootInterpolator());
        dashBoardView.setForeColor(getColor(R.color.c1));
        dashBoardView.setProgress(3000 / 6000F, true);
        dashBoardView.setLadderValue(new String[]{"富强", "民主", "文明", "和谐", "爱国", "敬业", "诚信", "友善"});
        SeekBar seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dashBoardView.setProgress((progress / 100F), true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
