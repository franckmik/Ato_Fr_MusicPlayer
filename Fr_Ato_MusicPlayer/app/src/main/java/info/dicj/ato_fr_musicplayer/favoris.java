package info.dicj.ato_fr_musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.dicj.ato_fr_musicplayer.adapter.musiqueAdapter;
import info.dicj.ato_fr_musicplayer.items.enregistrementFavoris;
import info.dicj.ato_fr_musicplayer.items.musique;

/**
 * Created by utilisateur on 07/03/2017.
 */
public class favoris extends AppCompatActivity implements MediaPlayer.OnCompletionListener
{

    private musicService serviceMusique;//variable service
    private Intent playIntent;
    private boolean musicBound=false;
    private ArrayList<musique> listeMusiques;
    private ArrayList<musique> listeFavoris;
    private ListView musiqueView;
    private favorisDataSource datasource;
    TextView messageAucunFavoris;
    TextView titreMusiqueControleurTemporaire;
    ImageView imageLecturePause;
    LinearLayout controleurTemporaire;
    private RelativeLayout contenuPrincipal;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("DICJ","Creation de la classe favoris");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoris);

        contenuPrincipal = (RelativeLayout)findViewById(R.id.contenuPrincipal);
        controleurTemporaire = (LinearLayout)findViewById(R.id.controleurTemporaire);
        imageLecturePause = (ImageView)findViewById(R.id.imageLecturePause);
        titreMusiqueControleurTemporaire = (TextView)findViewById(R.id.titreMusiqueControleurTemporaire);

        musiqueView = (ListView)findViewById(R.id.listeDeMusiques);

        listeMusiques = new ArrayList<musique>();//la liste des musiques disponibles dans le telephone

        listeFavoris = new ArrayList<musique>();

        messageAucunFavoris = (TextView)findViewById(R.id.messageAucunFavoris);

        datasource = new favorisDataSource(this);
        Log.i("DICJ","Ouverture du datasource(de la BD)");
        datasource.open();

        getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

        remplirFavoris();

        if(listeFavoris.size() == 0)//il n'ya pas de musique dans le telephone
        {
            musiqueView.setVisibility(View.INVISIBLE);
            messageAucunFavoris.setVisibility(View.VISIBLE);
        }
        else
        {
            musiqueView.setVisibility(View.VISIBLE);
            messageAucunFavoris.setVisibility(View.INVISIBLE);
        }

        Log.i("DICJ","Nombre d'élements de la liste :"+ listeFavoris.size());

        Collections.sort(listeFavoris, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        musiqueAdapter musiqueAdapteur = new musiqueAdapter(this,listeFavoris);
        musiqueView.setAdapter(musiqueAdapteur);

    }

    @Override
    protected void onStart()
    {


        updateListeFavoris();

        super.onStart();
        Log.i("DICJ","OnStart de la classe favoris");
        if(playIntent==null)//premiere ouverture de l'activité
        {
            Log.i("DICJ","PlayIntent est null");
            playIntent = new Intent(this, musicService.class);//intention de la classe bibliotheque vers la classe musicService
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //Log.i("DICJ","Lancement du service");
            startService(playIntent);//lancement du service de la musique
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

    private void updateListeFavoris()
    {
        listeMusiques.clear();

        listeFavoris.clear();

        getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

        remplirFavoris();

        Collections.sort(listeFavoris, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {

                return a.getTitreMusique().compareTo(b.getTitreMusique());

            }
        });

        musiqueAdapter musiqueAdapteur = new musiqueAdapter(this,listeFavoris);
        musiqueView.setAdapter(musiqueAdapteur);
    }

    @Override
    protected void onResume()
    {
        Log.i("DICJ","OnResume de la classe favoris");
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        Log.i("DICJ","OnStop de la classe favoris");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.i("DICJ","OnDestroy de la classe favoris");
        Log.i("DICJ","Deconnexion du service");
        unbindService(musicConnection);//on se deconnecte du service
        datasource.close();
        super.onDestroy();

    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)//methode appele quand on se connecte au service
        {
            Log.i("DICJ", " binder connection du service");

            musicService.MusicBinder binder = (musicService.MusicBinder) service;

            serviceMusique = binder.getService();//get service


            if(serviceMusique.getFavorisEnCour() == false)
            {
                serviceMusique.setList(listeMusiques);//je passe la liste de musique
            }
            else
            {
                serviceMusique.setList(listeFavoris);//je passe la liste de musique
            }

            serviceMusique.updateTheme(contenuPrincipal);

            musicBound = true;

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

            //setOnCompletion();


        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            musicBound = false;
        }

    };

    public void musiqueCliquee(View view)/* methode qui s'execute quand une musique est cliquée */
    {
        Log.i("DICJ","Musique cliquée.");
        serviceMusique.setPositionMusique(Integer.parseInt(view.getTag().toString()));//a la position de la musique on affecte le tag de la vue cliquée

        serviceMusique.setList(listeFavoris);

        serviceMusique.setFavorisEnCour(true);//la liste des favoris est en lecture

        Log.i("Nouvelle liste","Liste des favoris dans la place. Elle contient : " + serviceMusique.getListeMusiques().size());
        serviceMusique.playSong();
        controleurTemporaire.setVisibility(View.VISIBLE);
        updateTitreMusique();
        serviceMusique.setMusicStarted(true);//la musique a starté
        imageLecturePause.setImageResource(R.drawable.pause2);
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

        intent = new Intent(favoris.this, controleur.class);

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
                Log.i("DICJ","Musique d'indice : "+idMusique+ "ajoutée");
            }
            while (curseurMusique.moveToNext());
        }
    }

    private void remplirFavoris()
    {
        for (musique musique: listeMusiques)
        {
            if(estUnFavoris(musique.getIdMusique()))
            {
                listeFavoris.add(musique);
            }
        }
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


    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Log.i("DICJ","OnCompletion dans les favoris");
        mp.reset();
        serviceMusique.playNext();
        updateTitreMusique();
    }
}
