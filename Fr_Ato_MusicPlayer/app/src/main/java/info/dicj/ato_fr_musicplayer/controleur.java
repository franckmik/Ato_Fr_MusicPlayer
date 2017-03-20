package info.dicj.ato_fr_musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import info.dicj.ato_fr_musicplayer.items.enregistrementFavoris;
import info.dicj.ato_fr_musicplayer.items.musique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by utilisateur on 21/02/2017.
 */

public class controleur extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,MediaPlayer.OnCompletionListener
{

    //private ArrayList<musique> listeMusiques;
    private musicService serviceMusique;//variable service
    private Intent playIntent;
    TextView titreMusique;
    TextView dureeCourante;
    TextView dureeTotale;
    private boolean musicBound=false;
    ImageView imageLecturePause,imageRandom,imageLike;
    SeekBar seekBar;
    utilities utils;
    private Handler mHandler= new Handler();
    private favorisDataSource datasource;
    private ArrayList<musique> listeMusiques;
    RelativeLayout contenuPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controleur);
        Log.i("DICJ","OnCreate du controlleur");

        contenuPrincipal = (RelativeLayout)findViewById(R.id.contenuPrincipal);
        imageLecturePause = (ImageView)findViewById(R.id.imageLecturePause);
        imageRandom = (ImageView)findViewById(R.id.imageRandom);
        imageLike = (ImageView)findViewById(R.id.imageLike);
        titreMusique = (TextView)findViewById(R.id.titreMusique);
        dureeCourante = (TextView)findViewById(R.id.dureeCourante);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        dureeTotale = (TextView)findViewById(R.id.dureeTotale);
        utils = new utilities();

        datasource = new favorisDataSource(this);
        Log.i("DICJ","Ouverture du datasource(de la BD)");
        datasource.open();

        listeMusiques = new ArrayList<musique>();//la liste des musiques disponibles dans le telephone

        getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        seekBar.setOnSeekBarChangeListener(this); // Important

    }

    @Override
    protected void onStart()//au lancement de l'activite de la classe Controleur
    {
        super.onStart();
        Log.i("DICJ","onStart du controleur");

        listeMusiques.clear();

        getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        if(playIntent==null)//premiere ouverture de l'activité
        {
            Log.i("DICJ","PlayIntent est null");
            playIntent = new Intent(this, musicService.class);//intention de la classe controleur vers la classe musicService
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //Log.i("DICJ","Lancement du service");
            //startService(playIntent);//lancement du service de la musique
        }
        else
        {
            Log.i("DICJ","PlayIntent n'est pas null");
        }

        if(serviceMusique == null)
        {
            Log.i("DICJ","serviceMusique est null");
        }
        else
        {
            Log.i("DICJ","serviceMusique n'est pas null");
        }

    }

    @Override
    protected void onDestroy()
    {
        Log.i("DICJ","onDestroy du controleur");

        Log.i("DICJ","Deconnexion du service");
        unbindService(musicConnection);//on se deconnecte du service
        arreteRunnable();//arret du runnable(thread)
        datasource.close();

        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("DICJ","onResume du controleur");
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        Log.i("DICJ","onStop du controleur");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("DICJ","onPause du controleur");
    }

    private ServiceConnection musicConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)//methode appele quand on se connecte au service
        {
            Log.i("DICJ", " binder connection du service");
            musicService.MusicBinder binder = (musicService.MusicBinder) service;
            serviceMusique = binder.getService();//get service

            serviceMusique.updateTheme(contenuPrincipal);

            if(serviceMusique.getFavorisEnCour() == false)
            {
                serviceMusique.setList(listeMusiques);//je passe la liste de musique
            }
            else
            {

            }


            musicBound = true;

            setOnCompletion();

            updateTitreMusique();

            updateProgressBar();

            updateImageLike();

            Log.i("DICJ", "Connexion au service");
            if(!serviceMusique.isPlaying())
            {
                imageLecturePause.setImageResource(R.drawable.lecture);
            }
            else
            {
                imageLecturePause.setImageResource(R.drawable.pause2);
            }

            if(serviceMusique.getShuffle()==false)
            {
                imageRandom.setImageResource(R.drawable.rand5);
            }
            else
            {
                imageRandom.setImageResource(R.drawable.rand5hover2);
            }

            /*if(serviceMusique.getMusicStarted() == true)//le user a commence a écouter la musique
            {
                Log.i("DICJ","Connexion au service effectuée. Affichage du controlleur.");
                controleurTemporaire.setVisibility(View.VISIBLE);

                if(!serviceMusique.isPlaying())
                {
                    imageLecturePause.setImageResource(R.drawable.play);
                }
                else
                {
                    imageLecturePause.setImageResource(R.drawable.pause);
                }

                updateTitreMusique();
            }
            else
            {

            }*/
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            musicBound = false;
        }

    };

    private void setOnCompletion()
    {
        serviceMusique.getPlayer().setOnCompletionListener(this); // Important
    }

    private void arreteRunnable()
    {
        Log.i("DICJ","Arret du runnable");
        ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
        Future longRunningTaskFuture = threadPoolExecutor.submit(mUpdateTimeTask);
        longRunningTaskFuture.cancel(true);
    }

    public void musiqueSuivante(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        //playerPret = false;
        //mUpdateTimeTask.cancel(true);
        //mHandler.
        arreteRunnable();
        //serviceMusique.setPlayerReady(false);
        serviceMusique.playNext();

        updateTitreMusique();
        updateImageLike();
        Log.i("DICJ","Update du progress bar");
        updateProgressBar();
        imageLecturePause.setImageResource(R.drawable.pause2);

    }

    public void musiquePrecedente(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        arreteRunnable();
        //playerPret = false;
        //serviceMusique.setPlayerReady(false);
        serviceMusique.playPrev();
        updateTitreMusique();
        updateImageLike();
        Log.i("DICJ","Update du progress bar");
        updateProgressBar();
        imageLecturePause.setImageResource(R.drawable.pause2);
    }

    public void musiquePauseLecture(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        //serviceMusique.playPrev();
        if(serviceMusique.isPlaying())//la musique joue
        {
            serviceMusique.pausePlayer();//on met la pause
            imageLecturePause.setImageResource(R.drawable.lecture);
        }
        else
        {
            serviceMusique.start();
            imageLecturePause.setImageResource(R.drawable.pause2);
        }
    }

    public void musiqueRandom(View view)/* methode qui s'execute quand l'icone random est cliquée*/
    {
        if(serviceMusique.getShuffle() == false)
        {
            imageRandom.setImageResource(R.drawable.rand5hover2);
        }
        else
        {
            imageRandom.setImageResource(R.drawable.rand5);
        }
        serviceMusique.setShuffle();
    }

    public void like(View view)/* methode qui s'execute quand l'icone en coeur est cliquée */
    {
        //dans le controlleur de la bibliotheque
        if(estUnFavoris(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getIdMusique()))
        {
            imageLike.setImageResource(R.drawable.coeurhover);
            Toast.makeText(getApplicationContext(),"Cette musique est deja parmis vos favoris", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Musique ajoutée aux favoris", Toast.LENGTH_LONG).show();
            enregistrementFavoris newEnregistrementFavoris = datasource.createEnregistrementFavoris(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getIdMusique());//on cree un nouvel enregistrement
            imageLike.setImageResource(R.drawable.coeurhover);
            //serviceMusique.afficheAllEnregistrementFavoris();
            afficheAllEnregistrementFavoris();
        }

    }



    private void updateTitreMusique()
    {
        Log.i("DICJ","Update du titre");
        titreMusique.setText(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getTitreMusique());
        /*if(serviceMusique.getListeMusiques().size()!= 0)
        {
            titreMusiqueControleurTemporaire.setText(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getTitreMusique());
        }*/
    }

    public void afficheAllEnregistrementFavoris()
    {
        List<enregistrementFavoris> listeEnregistrementFavoris = datasource.getAllEnregistrements();

        for (enregistrementFavoris enregistrement:listeEnregistrementFavoris )
        {
            Log.i("DICJ","Enregistrement : "+enregistrement.getId()+", indiceMusique : " + enregistrement.getIndiceMusique());
        }

    }

    public boolean estUnFavoris(int indiceMusique)
    {
        boolean estUnFavoris = false;

        List<enregistrementFavoris> listeEnregistrementFavoris = datasource.getAllEnregistrements();

        for (enregistrementFavoris enregistrement:listeEnregistrementFavoris )
        {
            //Log.i("DICJ","Enregistrement : "+enregistrement.getId()+", indiceMusique : " + enregistrement.getIndiceMusique());
            if(enregistrement.getIndiceMusique() == indiceMusique)
            {
                estUnFavoris = true;

                break;
            }
        }

        return estUnFavoris;
    }


    private void updateImageLike()
    {
        if(estUnFavoris(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getIdMusique()))
        {
            imageLike.setImageResource(R.drawable.coeurhover);
        }
        else
        {
            imageLike.setImageResource(R.drawable.coeur);
        }
    }

    public void updateProgressBar()
    {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable()
    {

        public void run()
        {


                /*if(serviceMusique.getPlayerReady()== true)
                {*/
                    //Log.i("DICJ","Runnable en cour");
                    //Log.i("DICJ","totalDuration");
                    long totalDuration = serviceMusique.getDuration();
                    //Log.i("DICJ","currentDuration");
                    long currentDuration = serviceMusique.getPosition();

                    // Displaying Total Duration time
                    dureeTotale.setText(""+utils.milliSecondsToTimer(totalDuration));
                    // Displaying time completed playing
                    dureeCourante.setText(""+utils.milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
                    //Log.d("Progress", ""+progress);
                    seekBar.setProgress(progress);
                    //serviceMusique.setPlayerReady(false);

                    // Running this thread after 100 milliseconds
                    mHandler.postDelayed(this, 100);

                /*}
                else
                {
                    Log.i("DICJ","Runnable pas en cour");
                }*/

        }
    };



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
       // Log.i("DICJ","OnProgressChanged");
    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        Log.i("DICJ","onStartTrackingTouch");
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        Log.i("DICJ","onStopTrackingTouch");
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = serviceMusique.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        serviceMusique.getPlayer().seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Log.i("DICJ","OnCompletion dans le controleur");
        mp.reset();

        serviceMusique.playNext();

        updateImageLike();
        updateTitreMusique();
        updateProgressBar();

    }

    public void getMusiques()
    {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor curseurMusique = musicResolver.query(musicUri, null, null, null, null);

        if(curseurMusique!=null && curseurMusique.moveToFirst())
        {
            //get columns
            int titreColumn = curseurMusique.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = curseurMusique.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artisteColumn = curseurMusique.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //ajoute les musiques dans la liste de musiques
            do
            {
                int idMusique = curseurMusique.getInt(idColumn);//je recupere l'id de la musique
                String titreMusique = curseurMusique.getString(titreColumn);
                String artisteMusique = curseurMusique.getString(artisteColumn);

                listeMusiques.add(new musique(R.drawable.tulips,idMusique, titreMusique, artisteMusique));
                Log.i("DICJ","Musique d'indice : "+idMusique+ "ajoutée");
            }
            while (curseurMusique.moveToNext());
        }
    }

}

