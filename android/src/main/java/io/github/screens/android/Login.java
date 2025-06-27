package io.github.screens.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import io.github.some_example_name.R;

public class Login extends AppCompatActivity {

    private EditText nickField;
    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nickField = findViewById(R.id.player_nick);
        playButton = findViewById(R.id.play_button);
        SharedPreferences sp = getSharedPreferences("playerData",Context.MODE_PRIVATE);
        nickField.setText(sp.getString("playerNickname",""));
    }

    public void login(View v){
        String playerNick = nickField.getText().toString();
        if (!playerNick.isEmpty()) {
            SharedPreferences sp = getSharedPreferences("playerData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("playerNickname",playerNick);
            editor.apply();
            Toast.makeText(this, "Bienvenido, " + playerNick + "!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, customPlayer.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Por favor ingresa tu Nickname", Toast.LENGTH_SHORT).show();
        }
    }
}
