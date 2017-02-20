package info.dicj.ato_fr_musicplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

import info.dicj.ato_fr_musicplayer.adapter.musiqueAdapter;
import info.dicj.ato_fr_musicplayer.items.musique;

import android.widget.MediaController.MediaPlayerControl;

/**
 * Created by utilisateur on 31/01/2017.
 */
public class bibliotheque extends AppCompatActivity implements MediaPlayerControl
{

    private ArrayList<musique> listeMusiques;
    private ListView musiqueView;
    private MusicService serviceMusique;//variable service
    private Intent playIntent;
    private boolean musicBound=false;
    private MusicController controleur;
    private boolean paused=false, playbackPaused=false;

    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("DICJ","Creation de la classe bibliotheque");




        super.onCreate(savedInstanceState);
        setContentView(R.layout.bibliotheque);

        musiqueView = (ListView)findViewById(R.id.listeDeMusiques);//listeView qui se trouve dans le layout "bibliotheque"

        listeMusiques = new ArrayList<musique>();//la liste des musiques disponibles dans le telephone

        getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone



        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        musiqueAdapter musiqueAdapteur = new musiqueAdapter(this,listeMusiques);

        musiqueView.setAdapter(musiqueAdapteur);

        setControleur();//initialisation du controller
        //controleur.show(0);//je fais un show dans le
    }



    @Override
    protected void onStart()//au lancement de l'activite de la classe bibliotheque
    {
        Log.i("DICJ","onStart de la bibliotheque");

        super.onStart();

        if(playIntent==null)
        {
            playIntent = new Intent(this, MusicService.class);//intention de la classe bibliotheque vers la classe MusicService

            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            Log.i("DICJ","Lancement du service");
            startService(playIntent);//lancement du service de la musique
        }

    }

    //connect to the service.Creation d'une connection
    private ServiceConnection musicConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)//methode appele quand on se connecte au service
        {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;

            serviceMusique = binder.getService();//get service

            serviceMusique.setList(listeMusiques);//je passe la liste de musique

            musicBound = true;

            Log.i("DICJ","Connexion au service");



            if(isPlaying()||(serviceMusique.getPlayBackPause()==true))//affichage du controlleur au demarrage du service dans la bibliotheque.
            {
                Log.i("DICJ","Connexion au service effectuée. Affichage du controlleur.");
                setControleur();
                controleur.show(0);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            musicBound = false;
        }

    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item)//gestion du menu
    {
        //menu item selected
        switch (item.getItemId())
        {
            case R.id.action_shuffle:
                //shuffle
                serviceMusique.setShuffle();

                break;

            case R.id.action_end:

                stopService(playIntent);
                serviceMusique=null;
                System.exit(0);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()//a la destruction de l'activite de bibliotheque
    {
        Log.i("DICJ","onDestroy de la bibliotheque");

        //Log.i("DICJ","Arret du service de musique.");
        //stopService(playIntent);//arret du service de musique
        //serviceMusique=null;
        super.onDestroy();
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
                long idMusique = curseurMusique.getLong(idColumn);//je recupere l'id de la musique
                String titreMusique = curseurMusique.getString(titreColumn);
                String artisteMusique = curseurMusique.getString(artisteColumn);

                listeMusiques.add(new musique(R.drawable.tulips,idMusique, titreMusique, artisteMusique));
            }
            while (curseurMusique.moveToNext());
        }
    }

    public void musiqueCliquee(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        Log.i("DICJ","Musique cliquée.");

        serviceMusique.setPositionMusique(Integer.parseInt(view.getTag().toString()));//a la position de la musique on affecte le tag de la vue cliquée
        serviceMusique.playSong();

        /*if(playbackPaused)
        {
            setControleur();
            playbackPaused=false;
        }*/
        setControleur();

        /*if(playbackPaused)
        {
            playbackPaused=false;
        }*/



        controleur.show(0);
    }

    public void setControleur()//je definis ce qui se passe quand on clique sur le controlleur
    {
        Log.i("DICJ","Config du controlleur." );

        //set the controller up
        controleur = new MusicController(this);

        controleur.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }

        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controleur.setMediaPlayer(this);
        controleur.setAnchorView(findViewById(R.id.listeDeMusiques));//ancre du controleur
        controleur.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    private void playNext()
    {
        serviceMusique.playNext();

        /*if(playbackPaused)
        {
            setControleur();
            playbackPaused=false;
        }*/
        setControleur();
        controleur.show(0);//affiche le controleur
    }

    private void playPrev()
    {
        serviceMusique.playPrev();

        /*if(playbackPaused)
        {
            setControleur();
            playbackPaused=false;
        }*/

        setControleur();
        controleur.show(0);
    }

    @Override
    public void start()//methode herite du MediaPlayerController
    {
        Log.i("DICJ","start du MediaPlayerController");
        serviceMusique.start();
    }

    @Override
    public void pause()//methode herite du MediaPlayerController
    {
        //playbackPaused=true;



        serviceMusique.pausePlayer();
    }

    @Override
    public int getDuration()//methode herite du MediaPlayerController
    {
        if(serviceMusique!=null && musicBound && serviceMusique.isPlaying())
        return serviceMusique.getDuration();
        else return 0;
    }

    @Override
    public int getCurrentPosition()//methode herite du MediaPlayerController
    {
        if(serviceMusique!=null && musicBound && serviceMusique.isPlaying())
            return serviceMusique.getPosition();
        else
            return 0;

    }

    @Override
    public void seekTo(int pos)//methode herite du MediaPlayerController
    {
        serviceMusique.seek(pos);
    }

    @Override
    public boolean isPlaying()//methode herite du MediaPlayerController
    {
        if(serviceMusique!=null && musicBound)
            return serviceMusique.isPlaying();
        else
            return false;
    }

    @Override
    public int getBufferPercentage()
    {
        return 0;
    }

    @Override
    public boolean canPause()
    {
        return true;
    }

    @Override
    public boolean canSeekBackward()
    {
        return true;
    }

    @Override
    public boolean canSeekForward()
    {
        return true;
    }

    @Override
    public int getAudioSessionId()
    {
        return 0;
    }

    @Override
    protected void onPause()//avant le onStop() de l'activite
    {
        Log.i("DICJ","onPause(avant le onStop de l'activite) de la bibliotheque");
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume()//apres le lancement de l'activite
    {
        Log.i("DICJ","onResume(apres le onStart de l'activite) de la bibliotheque");

        super.onResume();

        if(paused)
        {
            //setControleur();
            paused=false;
        }


    }

    @Override
    protected void onStop()//arret de l'activite
    {
        Log.i("DICJ","onStop de la bibliotheque");
        //controleur.hide();
        super.onStop();
    }
}
