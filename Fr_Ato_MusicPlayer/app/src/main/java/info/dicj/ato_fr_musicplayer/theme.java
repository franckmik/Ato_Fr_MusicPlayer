package info.dicj.ato_fr_musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by utilisateur on 30/01/2017.
 */
public class theme extends AppCompatActivity {

    TextView texte;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme);

        texte = (TextView) findViewById(R.id.texte);

        Intent intent = getIntent();

        if(intent!=null)
        {
            message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
            texte.setText(message);
        }


    }

}
