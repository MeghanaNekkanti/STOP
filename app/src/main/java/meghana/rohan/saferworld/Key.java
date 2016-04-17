package meghana.rohan.saferworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Key extends AppCompatActivity {

    TextView keyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        keyTv = (TextView) findViewById(R.id.keyTv);

        String key = ""+(int)(Math.random() * 9000+1000);
        keyTv.setText("Share this key with emergency contacts:"+key);

        keyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Key.this,MainActivity.class));
                finish();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("key",key);
        editor.putString("login","true");
        editor.apply();
    }
}
