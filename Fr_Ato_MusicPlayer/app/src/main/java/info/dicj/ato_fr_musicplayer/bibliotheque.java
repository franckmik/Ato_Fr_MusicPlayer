package info.dicj.ato_fr_musicplayer;

import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by utilisateur on 31/01/2017.
 */
public class bibliotheque extends AppCompatActivity implements MediaPlayer.OnCompletionListener
{

    private ArrayList<musique> listeMusiques;
    LinearLayout controleurTemporaire;
    private ListView musiqueView;
    private musicService serviceMusique;//variable service
    private Intent playIntent;
    private boolean musicBound=false;
    ImageView imageLecturePause;
    private RelativeLayout contenuPrincipal;

    TextView titreMusiqueControleurTemporaire,messageAucuneMusique;
    public final static String EXTRA_MESSAGE = "labIntention.info.dicj.ato_fr_musicplayer.MESSAGE";
    //private MusicController controleur;
    private boolean paused=false, playbackPaused=false;

    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("DICJ","Creation de la classe bibliotheque");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bibliotheque);
        musiqueView = (ListView)findViewById(R.id.listeDeMusiques);//listeView qui se trouve dans le layout "bibliotheque"
        listeMusiques = new ArrayList<musique>();//la liste des musiques disponibles dans le telephone
        controleurTemporaire = (LinearLayout)findViewById(R.id.controleurTemporaire);
        imageLecturePause = (ImageView)findViewById(R.id.imageLecturePause);
        titreMusiqueControleurTemporaire = (TextView)findViewById(R.id.titreMusiqueControleurTemporaire);
        messageAucuneMusique = (TextView)findViewById(R.id.messageAucuneMusique);
        contenuPrincipal = (RelativeLayout)findViewById(R.id.contenuPrincipal);

        getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

        if(listeMusiques.size() == 0)//il n'ya pas de musique dans le telephone
        {
            musiqueView.setVisibility(View.INVISIBLE);
            messageAucuneMusique.setVisibility(View.VISIBLE);
        }
        else
        {
            musiqueView.setVisibility(View.VISIBLE);
            messageAucuneMusique.setVisibility(View.INVISIBLE);
        }

        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        musiqueAdapter musiqueAdapteur = new musiqueAdapter(this,listeMusiques);
        musiqueView.setAdapter(musiqueAdapteur);
    }



    @Override
    protected void onStart()//au lancement de l'activite de la classe bibliotheque
    {
        Log.i("DICJ","onStart de la bibliotheque");

        super.onStart();

        updateListeMusiques();

        if(playIntent==null)//premiere ouverture de l'activité
        {
            Log.i("DICJ","PlayIntent est null");
            playIntent = new Intent(this, musicService.class);//intention de la classe bibliotheque vers la classe musicService
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //Log.i("DICJ","Lancement du service");
            //startService(playIntent);//lancement du service de la musique
        }
        else
        {

            Log.i("DICJ","PlayIntent n'est pas null");

            setOnCompletion();

            if(serviceMusique.getMusicStarted() == true)//le user a commence a écouter la musique
            {
                controleurTemporaire.setVisibility(View.VISIBLE);

                if(!serviceMusique.isPlaying())
                {
                    imageLecturePause.setImageResource(R.drawable.lecture);
                }
                else
                {
                    imageLecturePause.setImageResource(R.drawable.pause2);
                }

                updateTitreMusique();
            }

        }

    }

    @Override
    protected void onDestroy()//a la destruction de l'activite de bibliotheque
    {
        Log.i("DICJ","onDestroy de la bibliotheque");
        //Log.i("DICJ","Arret du service de musique.");
        //stopService(playIntent);//arret du service de musique
        //serviceMusique=null;
        //controleur.hide();
        Log.i("DICJ","Deconnexion du service");
        unbindService(musicConnection);//on se deconnecte du service
        //stopService(playIntent);//arret du service de musique
        super.onDestroy();
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
        // controleur.hide();
        super.onStop();
    }

    @Override
    protected void onPause()//avant le onStop() de l'activite
    {
        Log.i("DICJ","onPause(avant le onStop de l'activite) de la bibliotheque");
        //unbindService(musicConnection);
        super.onPause();
        paused = true;
    }

    //connect to the service.Creation d'une connection
    private ServiceConnection musicConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)//methode appele quand on se connecte au service
        {
            Log.i("DICJ"," binder connection du service");

            musicService.MusicBinder binder = (musicService.MusicBinder)service;

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

            Log.i("DICJ","Connexion au service");

            if(serviceMusique.getMusicStarted() == true)//le user a commence a écouter la musique
            {
                Log.i("DICJ","Connexion au service effectuée. Affichage du controlleur de la bibliotheque.");
                controleurTemporaire.setVisibility(View.VISIBLE);

                if(!serviceMusique.isPlaying())
                {
                    imageLecturePause.setImageResource(R.drawable.lecture);
                }
                else
                {
                    imageLecturePause.setImageResource(R.drawable.pause2);
                }

                updateTitreMusique();
            }
            else
            {
                Log.i("DICJ","Controlleur de la bibliotheque pas affiche.");
            }

            setOnCompletion();

            /*if(isPlaying()||serviceMusique.getPlayBackPause())//affichage du controlleur au demarrage du service dans la bibliotheque.
            {
                Log.i("DICJ","Connexion au service effectuée. Affichage du controlleur.");
                controleurTemporaire.setVisibility(View.VISIBLE);

                updateTitreMusique();

                if(!serviceMusique.isPlaying())
                {
                    imageLecturePause.setImageResource(R.drawable.play);
                }

            }
            else
            {
                Log.i("DICJ","Connexion au service effectuée. Mais il y'a une pause. Pas de controlleur affiche");
                controleurTemporaire.setVisibility(View.INVISIBLE);

            }*/

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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Log.i("DICJ","OnCompletion dans la biblioitheque");
        mp.reset();

        serviceMusique.playNext();
        updateTitreMusique();

    }

    private void setOnCompletion()
    {
        serviceMusique.getPlayer().setOnCompletionListener(this); // Important
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
                //Log.i("DICJ","Musique d'indice : "+idMusique+ "ajoutée");
            }
            while (curseurMusique.moveToNext());
        }
    }

    public void musiqueCliquee(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        Log.i("DICJ","Musique cliquée.");

        serviceMusique.setPositionMusique(Integer.parseInt(view.getTag().toString()));//a la position de la musique on affecte le tag de la vue cliquée

        serviceMusique.setList(listeMusiques);//je passe la liste de musique

        serviceMusique.setFavorisEnCour(false);

        serviceMusique.playSong();
        controleurTemporaire.setVisibility(View.VISIBLE);
        updateTitreMusique();

        serviceMusique.setMusicStarted(true);

        imageLecturePause.setImageResource(R.drawable.pause2);
        //setControleur();
        //controleur.show(0);
        /*if(playbackPaused)
        {
            playbackPaused=false;
        }*/
    }

    public void musiqueSuivante(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        serviceMusique.playNext();
        //controleurTemporaire.setVisibility(View.VISIBLE);
        updateTitreMusique();
        imageLecturePause.setImageResource(R.drawable.pause2);
    }

    public void musiquePrecedente(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        serviceMusique.playPrev();
        //controleurTemporaire.setVisibility(View.VISIBLE);
        updateTitreMusique();
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

    public void activiteControleur(View view)
    {
        Intent intent;

        intent = new Intent(bibliotheque.this, controleur.class);

        startActivity(intent);
    }

    private void updateTitreMusique()
    {
        Log.i("DICJ","Update du titre");
        titreMusiqueControleurTemporaire.setText(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getTitreMusique());
        /*if(serviceMusique.getListeMusiques().size()!= 0)
        {
            titreMusiqueControleurTemporaire.setText(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getTitreMusique());
        }*/
    }

    private void updateListeMusiques()
    {
        listeMusiques.clear();

        getMusiques();

        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        musiqueAdapter musiqueAdapteur = new musiqueAdapter(this,listeMusiques);
        musiqueView.setAdapter(musiqueAdapteur);
    }
}
