package org.windning.demo;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.windning.safearea.SafeAreaController;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInst) {
        super.onCreate(savedInst);
        this.setContentView(R.layout.main_activity);
        Button button = this.findViewById(R.id.btn_refresh);
        final TextView text = this.findViewById(R.id.text_rect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = SafeAreaController.getSafeArea(MainActivity.this);
                text.setText(result);
            }
        });
        SafeAreaController.initWindowLayout(this, true);
    }
}