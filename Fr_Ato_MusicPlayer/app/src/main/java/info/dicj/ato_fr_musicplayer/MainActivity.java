package info.dicj.ato_fr_musicplayer;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.dicj.ato_fr_musicplayer.adapter.slidingMenuAdapter;
import info.dicj.ato_fr_musicplayer.items.itemSlideMenu;
import info.dicj.ato_fr_musicplayer.items.musique;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaController.MediaPlayerControl {

    private List<itemSlideMenu> listeItems;//liste de slidingDeMenu donc d'item
    private slidingMenuAdapter adaptateur;//L'adaptateur qui affiche chaque item de mon menu de slide
    private ListView listeViewItems;//liste de vues qui se trouve dans le layout "Main_Activity"
    private DrawerLayout ecranPrincipal;//ecran principal
    private ActionBarDrawerToggle actionBarDrawerToggle;//bar de navigation( menu de slide)
    public final static String EXTRA_MESSAGE = "labIntention.info.dicj.ato_fr_musicplayer.MESSAGE";
    TextView texteBibliotheque;
    private Intent playIntent;
    private MusicService serviceMusique;//variable service
    private ArrayList<musique> listeMusiques;
    private boolean musicBound=false;//on check si le la connection au service est etablie
    private MusicController controleur;
    private boolean paused=false, playbackPaused=false;


        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            listeViewItems = (ListView) findViewById(R.id.listeMenuSlide);
            ecranPrincipal = (DrawerLayout) findViewById(R.id.ecranPrincipal);//je recupere tout mon affichage principal
            listeItems = new ArrayList<>();
            texteBibliotheque = (TextView)findViewById(R.id.lienBibliotheque);

            texteBibliotheque.setOnClickListener(this);

            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Acceuil"));
            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Themes"));

            adaptateur = new slidingMenuAdapter(this, listeItems);//je cree mon adaptateur en lui passant en parametre ma liste de slide
            listeViewItems.setAdapter(adaptateur);//la liste de vues reference a toutes les items de mon menu


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            listeViewItems.setItemChecked(0, true);

            ecranPrincipal.closeDrawer(listeViewItems);

            //replaceFragment(0);


            listeViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() /*Je cree un evenement sur chacun de mes items*/ {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

            listeMusiques = new ArrayList<musique>();//la liste des musiques disponibles dans le telephone

            getMusiques();//je rempli la liste "listeMusiques" avec les informations des musiques de mon telephone

            setControleur();//initialisation du controller


        }

        @Override
        protected void onStart()//au lancement de l'activite de la classe MainActivity
        {
            Log.i("DICJ","onStart du mainActivity.");
            super.onStart();
            if(playIntent==null)
            {
                playIntent = new Intent(this, MusicService.class);//intention de la classe MainActivity vers la classe MusicService
                Log.i("DICJ","Connexion au service");
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
                Log.i("DICJ","Lancement du service");
                startService(playIntent);//lancement du service de la musique
            }

            /*if(this.isPlaying() == true)
            {
                Log.i("DICJ","La musique joue.J'affiche le controlleur.");
                controleur.show();
            }
            else
            {
                Log.i("DICJ","La musique ne joue pas.Je n'affiche pas le controlleur.");
            }*/

        }


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
        protected void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            actionBarDrawerToggle.syncState();
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

                    intent = new Intent(MainActivity.this, theme.class);

                    intent.putExtra(EXTRA_MESSAGE," My Message");

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

        @Override
        public void onClick(View v)
        {

            Intent intent;

            int id = v.getId();

            switch (id)
            {
                case R.id.lienBibliotheque:

                     intent = new Intent(MainActivity.this, bibliotheque.class);

                    //intent.putExtra(EXTRA_MESSAGE," My Message");

                    startActivity(intent);

                    break;

                default:

                    break;
            }


        }



        private ServiceConnection musicConnection = new ServiceConnection()
        {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service)//methode appele quand on se connecte au service
            {
                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;

                serviceMusique = binder.getService();//get service

                serviceMusique.setList(listeMusiques);//je passe la liste de musique

                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                musicBound = false;
            }

        };

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
            controleur.setAnchorView(findViewById(R.id.contenuPrincipal));//ancre du controleur
            controleur.setEnabled(true);
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
            playbackPaused=true;
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


        private void playNext()
        {
            serviceMusique.playNext();

            /*if(playbackPaused)
            {
                setControleur();
                playbackPaused=false;
            }*/
            setControleur();

            if(playbackPaused)
            {
                playbackPaused=false;
            }

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

            if(playbackPaused)
            {
                playbackPaused=false;
            }

            controleur.show(0);
        }


    @Override
    protected void onResume()
    {
        Log.i("DICJ","onResume de classe MainActivity");
        super.onResume();

        if(this.isPlaying() == true)
        {
            Log.i("DICJ","La musique joue.J'affiche le controlleur.");
            setControleur();
            controleur.show();
        }
        else
        {
            Log.i("DICJ","La musique ne joue pas.Je n'affiche pas le controlleur.");
            controleur.hide();
        }

    }
}
