package info.dicj.ato_fr_musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by utilisateur on 30/01/2017.
 */
public class theme extends AppCompatActivity implements View.OnClickListener
{

    private musicService serviceMusique;//variable service
    private Intent playIntent;
    String message;
    TextView titre;
    ImageView bleu;
    ImageView jaune;
    ImageView vert;
    ImageView rouge;
    ImageView rose;
    ImageView bleuClair;
    ImageView capuccine;
    ImageView dore;
    ImageView orange;
    ImageView marron;
    ImageView saumon;
    ImageView magenta;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme);

        Log.i("DICJ","Creation de l'activité theme");

        titre = (TextView) findViewById(R.id.titre);

        bleu = (ImageView)findViewById(R.id.bleu);
        jaune = (ImageView)findViewById(R.id.jaune);
        vert = (ImageView)findViewById(R.id.vert);
        rouge = (ImageView)findViewById(R.id.rouge);
        rose = (ImageView)findViewById(R.id.rose);
        bleuClair = (ImageView)findViewById(R.id.bleuClair);
        capuccine = (ImageView)findViewById(R.id.capuccine);
        dore = (ImageView)findViewById(R.id.dore);
        orange = (ImageView)findViewById(R.id.orange);
        marron = (ImageView)findViewById(R.id.marron);
        saumon = (ImageView)findViewById(R.id.saumon);
        magenta = (ImageView)findViewById(R.id.magenta);

        bleu.setOnClickListener(this);
        jaune.setOnClickListener(this);
        vert.setOnClickListener(this);
        rouge.setOnClickListener(this);
        rose.setOnClickListener(this);
        bleuClair.setOnClickListener(this);
        capuccine.setOnClickListener(this);
        dore.setOnClickListener(this);
        orange.setOnClickListener(this);
        marron.setOnClickListener(this);
        saumon.setOnClickListener(this);
        magenta.setOnClickListener(this);

        Intent intent = getIntent();

        if(intent!=null)
        {
            //message = intent.getStringExtra(mainActivity.EXTRA_MESSAGE);
            //texte.setText(message);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(playIntent==null)//premiere ouverture de l'activité
        {
            Log.i("DICJ","PlayIntent est null");
            playIntent = new Intent(this, musicService.class);//intention de la classe bibliotheque vers la classe musicService
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //Log.i("DICJ","Lancement du service");
            startService(playIntent);//lancement du service de la musique
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)//methode appele quand on se connecte au service
        {
            Log.i("DICJ", " binder connection du service");

            musicService.MusicBinder binder = (musicService.MusicBinder) service;

            serviceMusique = binder.getService();//get service


        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }

    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i("DICJ","onDestroy de l'activité theme");
        unbindService(musicConnection);
    }

    @Override
    public void onClick(View v)
    {
        int idView = v.getId();
        String nomTheme  = v.getTag().toString();

        Log.i("DICJ","ID DE L'ELEMENT CLIQUÉ : "+ idView);

        Log.i("DICJ","TAG DE L'ELEMENT CLIQUÉ : "+ nomTheme);

        serviceMusique.setNomTheme(v.getTag().toString());

        Toast.makeText(getApplication(),"Nouveau theme : "+ nomTheme,Toast.LENGTH_SHORT).show();
    }

    /*public void changerTheme(View view)
    {
        Toast.makeText(getApplication()," Changement de theme ",Toast.LENGTH_SHORT ).show();
        Log.i("DICJ"," CHANGEMENT DE THEME ");

        int idView = view.getId();

        Log.i("DICJ","ID DE L'ELEMENT CLIQUÉ : "+ idView);

    }*/

}
