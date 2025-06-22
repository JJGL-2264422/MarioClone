package io.github.screens.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import io.github.some_example_name.R;

public class customPlayer extends AppCompatActivity {
    ColorPickerView playerColorPick;
    BrightnessSlideBar brightnessSlideBar;
    FrameLayout colorpickPreview;
    TextView colorText, hatName, hatCount;
    ImageView hatPreview;

    String hexCode = "#FF000000"; Integer intCode;

    int[] hatList = new int[]{R.drawable.nohat, R.drawable.roundhat, R.drawable.tophat};
    String[] hatNameList = new String[]{"Ninguno","Redondo","De copa"};
    int hatListCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_custom_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        playerColorPick = findViewById(R.id.playerColorPick);
        brightnessSlideBar = findViewById(R.id.brightnessSlide);
        colorpickPreview = findViewById(R.id.colorpickPrev);
        colorText = findViewById(R.id.colorText);
        hatName = findViewById(R.id.hatName);
        hatCount = findViewById(R.id.hatCount);
        hatPreview = findViewById(R.id.hatPreview);

        playerColorPick.attachBrightnessSlider(brightnessSlideBar);

        SharedPreferences sp = getSharedPreferences("playerData",Context.MODE_PRIVATE);
        intCode = sp.getInt("playerIntCode",0);
        hatListCounter = sp.getInt("playerHat",0);
        playerColorPick.setInitialColor(intCode);
        hatPreview.setImageResource(hatList[hatListCounter]);
        hatCount.setText(String.format("%d/3", hatListCounter + 1));
        hatName.setText(hatNameList[hatListCounter]);

        playerColorPick.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                colorpickPreview.setBackgroundColor(envelope.getColor());
                colorText.setText("#" + envelope.getHexCode());
                hexCode = "#" + envelope.getHexCode().substring(2);
                intCode = envelope.getColor();
            }
        });
    }

    public void saveColor(View v){
        SharedPreferences sp = getSharedPreferences("playerData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("playerHexCode",hexCode);
        editor.apply();
        editor.putInt("playerIntCode",intCode);
        editor.apply();
        Toast.makeText(this, "Color de jugador guardado!", Toast.LENGTH_LONG).show();
    }

    public void play(View v){
        Intent intent = new Intent(customPlayer.this, AndroidLauncher.class);
        startActivity(intent);
        finish();
    }

    public void nextHat(View v){
        hatListCounter += 1;
        if(hatListCounter <= 2){
            hatPreview.setImageResource(hatList[hatListCounter]);
            hatCount.setText(String.format("%d/3", hatListCounter + 1));
            hatName.setText(hatNameList[hatListCounter]);
        }else{
            hatListCounter = 2;
        }
    }

    public void prevHat(View v){
        hatListCounter -= 1;
        if(hatListCounter >= 0){
            hatPreview.setImageResource(hatList[hatListCounter]);
            hatCount.setText(String.format("%d/3", hatListCounter + 1));
            hatName.setText(hatNameList[hatListCounter]);
        }else{
            hatListCounter = 0;
        }

    }

    public void saveHat(View v){
        SharedPreferences sp = getSharedPreferences("playerData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("playerHat",hatListCounter);
        editor.apply();
        Toast.makeText(this, "Sombrero seleccionado!", Toast.LENGTH_LONG).show();
    }
}
