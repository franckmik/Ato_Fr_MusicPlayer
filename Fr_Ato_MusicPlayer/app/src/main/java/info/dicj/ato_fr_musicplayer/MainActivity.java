package info.dicj.ato_fr_musicplayer;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import info.dicj.ato_fr_musicplayer.adapter.slidingMenuAdapter;
import info.dicj.ato_fr_musicplayer.items.itemSlideMenu;
import info.dicj.ato_fr_musicplayer.items.musique;

public class mainActivity extends AppCompatActivity implements  MediaPlayer.OnCompletionListener
{

    private List<itemSlideMenu> listeItems;//liste de slidingDeMenu donc d'item
    private slidingMenuAdapter adaptateur;//L'adaptateur qui affiche chaque item de mon menu de slide
    private ListView listeViewItems;//liste de vues qui se trouve dans le layout "Main_Activity"
    private DrawerLayout ecranPrincipal;//ecran principal
    private ActionBarDrawerToggle actionBarDrawerToggle;//bar de navigation( menu de slide)
    public final static String EXTRA_MESSAGE = "labIntention.info.dicj.ato_fr_musicplayer.MESSAGE";
    //TextView texteBibliotheque;
    private Intent playIntent;
    LinearLayout controleurTemporaire;
    private musicService serviceMusique;//variable service
    private ArrayList<musique> listeMusiques;
    private RelativeLayout contenuPrincipal;
    private boolean musicBound=false;//on check si le la connection au service est etablie
    //private MusicController controleur;
    private boolean paused=false, playbackPaused=false;
    TextView titreMusiqueControleurTemporaire;
    ImageView imageLecturePause;


        @Override
        protected void onCreate(Bundle savedInstanceState)
        {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Log.i("DICJ","Creation de l'activité mainActivity");

            //ActivityCompat.requestPermissions(mainActivity.this, new String[]{Manifest.permi}, REQUEST_READ);
            contenuPrincipal = (RelativeLayout)findViewById(R.id.contenuPrincipal);
            listeViewItems = (ListView) findViewById(R.id.listeMenuSlide);
            ecranPrincipal = (DrawerLayout) findViewById(R.id.ecranPrincipal);//je recupere tout mon affichage principal
            controleurTemporaire = (LinearLayout)findViewById(R.id.controleurTemporaire);
            titreMusiqueControleurTemporaire = (TextView)findViewById(R.id.titreMusiqueControleurTemporaire);
            imageLecturePause = (ImageView)findViewById(R.id.imageLecturePause);
            listeItems = new ArrayList<>();
            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Acceuil"));
            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Themes"));
            adaptateur = new slidingMenuAdapter(this, listeItems);//je cree mon adaptateur en lui passant en parametre ma liste de slide
            listeViewItems.setAdapter(adaptateur);//la liste de vues reference a toutes les items de mon menu
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            listeViewItems.setItemChecked(0, true);
            ecranPrincipal.closeDrawer(listeViewItems);
            listeMusiques = new ArrayList<musique>();//la liste des musiques disponibles dans le telephone

            //replaceFragment(0);

            listeViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() /*Je cree un evenement sur chacun de mes items*/ {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    //setTitle(listeItems.get(position).getTitreImage());

                    /*if(position == 0)
                    {
                        setTitle("EasyMusic");
                    }*/
                    listeViewItems.setItemChecked(position, true);
                    ecranPrincipal.closeDrawer(listeViewItems);//je ferme le menu de slide
                    //replaceFragment(position);
                    afficheNouvelleActivite(position);
                }
            });


            actionBarDrawerToggle = new ActionBarDrawerToggle(this, ecranPrincipal, R.string.drawer_opened, R.string.drawer_closed) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    invalidateOptionsMenu();
                }


            };

            ecranPrincipal.setDrawerListener(actionBarDrawerToggle);
            //setControleur();//initialisation du controller

            getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

            //Log.i("DICJ","Nombre d'élements de la liste :"+ listeMusiques.size());

            Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
            {
                public int compare(musique a, musique b)
                {

                    return a.getTitreMusique().compareTo(b.getTitreMusique());

                }
            });


        }

    @Override
    protected void onResume()
    {
        Log.i("DICJ","onResume de la classe mainActivity");
        super.onResume();

        /*if(isPlaying()||(serviceMusique.getPlayBackPause()==true))
        {
            Log.i("DICJ","La musique joue.J'affiche le controlleur.");
            setControleur();
            controleur.show(0);
        }
        else
        {
            Log.i("DICJ","La musique ne joue pas.Je n'affiche pas le controlleur.");
            controleur.hide();
        }*/

    }

    @Override
    protected void onStart()//au lancement de l'activite de la classe mainActivity
    {
        Log.i("DICJ","onStart du mainActivity.");

        super.onStart();

        listeMusiques.clear();

        getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

        Collections.sort(listeMusiques, new Comparator<musique>()//tri des musiques par ordre alphabetique de titre
        {
            public int compare(musique a, musique b)
            {
                return a.getTitreMusique().compareTo(b.getTitreMusique());
            }
        });


        if(playIntent==null)
        {
            playIntent = new Intent(this, musicService.class);//intention de la classe mainActivity vers la classe musicService
            Log.i("DICJ","Connexion au service");
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            Log.i("DICJ","Lancement du service");
            startService(playIntent);//lancement du service de la musique
        }
        else
        {
            Log.i("DICJ","PlayIntent n'est pas null");

            setOnCompletion();

            serviceMusique.updateTheme(contenuPrincipal);

            if(serviceMusique.getMusicStarted() == true)//le user a commence a écouter la musique
            {
                Log.i("DICJ","Music Started est a true");

                controleurTemporaire.setVisibility(View.VISIBLE);

                updateTitreMusique();

                if(!serviceMusique.isPlaying())
                {
                    imageLecturePause.setImageResource(R.drawable.lecture);
                }
                else
                {
                    imageLecturePause.setImageResource(R.drawable.pause2);
                }


            }
            else
            {
                Log.i("DICJ","Music Started est a false");
            }

        }

    }


    @Override
    protected void onDestroy()
    {
        //controleur.hide();
        Log.i("DICJ","onDestroy du mainActivity");

        Log.i("DICJ","Deconnexion du service");
        unbindService(musicConnection);//on se deconnecte du service

        Log.i("DICJ","Arret du service");
        stopService(playIntent);

        super.onDestroy();

    }


    private ServiceConnection musicConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)//methode appele quand on se connecte au service
        {
            Log.i("DICJ"," binder connection du service");

            musicService.MusicBinder binder = (musicService.MusicBinder)service;

            serviceMusique = binder.getService();//get service

            if(serviceMusique.getFavorisEnCour() == false)
            {
                serviceMusique.setList(listeMusiques);//je passe la liste de musique
            }
            else
            {

            }

            musicBound = true;

            setOnCompletion();

            serviceMusique.updateTheme(contenuPrincipal);

        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            musicBound = false;
        }

    };


        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            if(actionBarDrawerToggle.onOptionsItemSelected(item))
            {
                return  true;
            }
            return super.onOptionsItemSelected(item);
        }


        @Override
        protected void onPostCreate(Bundle savedInstanceState)
        {
            super.onPostCreate(savedInstanceState);
            actionBarDrawerToggle.syncState();
        }

        @Override
        public void onCompletion(MediaPlayer mp)
        {
            Log.i("DICJ","OnCompletion dans le mainActivity");
            mp.reset();

            serviceMusique.playNext();
            updateTitreMusique();
            //updateProgressBar();
        }


        private void afficheNouvelleActivite(int pos)
        {
            Fragment fragment = null;
            Intent intent;

            switch (pos)
            {
                case 0:

                    ecranPrincipal.closeDrawer(listeViewItems);//je ferme le menu de slide

                    break;

                case 1:

                    intent = new Intent(mainActivity.this, theme.class);

                    //intent.putExtra(EXTRA_MESSAGE,"Les themes");

                    startActivity(intent);

                    break;

                default:

                    ecranPrincipal.closeDrawer(listeViewItems);//je ferme le menu de slide

                    break;
            }

            /*if(fragment != null)
            {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.contenuPrincipal,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }*/

        }

        private void setOnCompletion()
        {
            Log.i("DICJ","setOnCompletion du mainActivity");
            serviceMusique.getPlayer().setOnCompletionListener(this); // Important
        }

       /* @Override
        public void onClick(View v)
        {



            int id = v.getId();

            switch (id)
            {
                case R.id.lienBibliotheque:



                    break;

                default:

                    break;
            }


        }*/

        public void lienBibliotheque(View view)
        {
            //serviceMusique.playNext();
            Intent intent;

            intent = new Intent(mainActivity.this, bibliotheque.class);

            startActivity(intent);
        }

        public void lienFavoris(View view)
        {
            //serviceMusique.playNext();
            Intent intent;

            intent = new Intent(mainActivity.this, favoris.class);

            startActivity(intent);
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

        intent = new Intent(mainActivity.this, controleur.class);
        //intent.putExtra(EXTRA_MESSAGE," My Message");
        startActivity(intent);
    }

    private void updateTitreMusique()
    {

        Log.i("DICJ","Update du titre");

        titreMusiqueControleurTemporaire.setText(serviceMusique.getListeMusiques().get(serviceMusique.getPositionMusique()).getTitreMusique());

    }

    private void updateTheme()
    {

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
                }
                while (curseurMusique.moveToNext());
            }
        }


    /*@Override
    public void onCompletion(MediaPlayer mp)
    {
        Log.i("DICJ","OnCompletion dans le mainActivity");
        mp.reset();
        serviceMusique.playNext();

        updateTitreMusique();
    }*/




    /*@Override
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
        else
            return 0;
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
    }*/

}
