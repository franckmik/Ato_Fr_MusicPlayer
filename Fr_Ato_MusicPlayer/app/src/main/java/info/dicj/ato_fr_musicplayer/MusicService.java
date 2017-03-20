package info.dicj.ato_fr_musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Random;

import info.dicj.ato_fr_musicplayer.items.enregistrementFavoris;
import info.dicj.ato_fr_musicplayer.items.musique;

/**
 * Created by utilisateur on 31/01/2017.
 */
public class musicService extends Service implements  MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener
{


    private MediaPlayer player; //song list

    private ArrayList<musique> listeMusiques;//liste des musiques

    private int positionMusique;//current position

    private final IBinder musicBind = new MusicBinder();

    private String titreMusique="";

    private static final int NOTIFY_ID=1;

    private boolean shuffle=false;

    private boolean playBackPause = false;

   // private boolean playerReady = false;

    private Random random;

    private boolean favorisEnCour = false;

    private boolean musicStarted = false;//me permet de savoir si le user a starte la musique

    private String nomTheme = "bleu";

    //private favorisDataSource datasource;
    //Context context = getApplicationContext();

    /* MusicBinder est un canal de connexion avec le service*/

    /*public favorisDataSource getDatasource()
    {
        return datasource;
    }*/

    /*public void setDatasource(favorisDataSource datasource)
    {
        this.datasource = datasource;
    }*/

    public ArrayList<musique> getListeMusiques()
    {
        return listeMusiques;
    }

    public void setList(ArrayList<musique> listeMusiques)
    {
        this.listeMusiques=listeMusiques;
    }

    public void setPositionMusique(int positionMusique)
    {
        this.positionMusique = positionMusique;
    }

    public int getPositionMusique()
    {
        return positionMusique;
    }

    public boolean getPlayBackPause()
    {
        return playBackPause;
    }

    public void setPlayBackPause(boolean playBackPause)
    {
        this.playBackPause = playBackPause;
    }

    public void setMusicStarted(boolean musicStarted)
    {
        this.musicStarted = musicStarted;
    }

    public boolean getMusicStarted()
    {
        return musicStarted;
    }

    public MediaPlayer getPlayer()
    {
        return player;
    }

    public void setPlayer(MediaPlayer player)
    {
        this.player = player;
    }

    public boolean getShuffle()
    {
        return shuffle;
    }

    public boolean getFavorisEnCour()
    {
        return favorisEnCour;
    }

    public void setFavorisEnCour(boolean favorisEnCour)
    {
        this.favorisEnCour = favorisEnCour;
    }

    public String getNomTheme()
    {
        return  nomTheme;
    }

    public void setNomTheme(String theme)
    {
        this.nomTheme = theme;
    }

    @Override
    public void onCreate()//Creation du service
    {
        Log.i("DICJ","Creation du service");


        //datasource = new favorisDataSource(this);
        Log.i("DICJ","Ouverture du datasource(de la BD)");
        //datasource.open();
        //create the service
        positionMusique = 0;

        player = new MediaPlayer();//Je cree un objet mediaPlayer

        initialiseMusicPlayer();//initialise le lecteur de musique

        random = new Random();

        //AudioManager audioManager = new AudioManager()
    }

    public void initialiseMusicPlayer()
    {
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        /*player.setOnPreparedListener(this);/*associe un écouteur d'événements dont la méthode onPrepared(MediaPlayer) est appelée lorsque
        le MediaPlayer est prêt*/

        player.setOnCompletionListener(this);

        player.setOnErrorListener(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {

    }


    public class MusicBinder extends Binder//declaration de la classe MusicBinder
    {
        musicService getService()
        {
            return musicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)//appele apres le onCreate() du service
    {
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mp)//se declenche quand une piste se termine
    {
        Log.i("DICJ","Lecture de la musique en cour terminée.");
        //((bibliotheque)context).setControleur();

        /*if(player.getCurrentPosition()== 0)
        {
            mp.reset();
            playNext();

        }*/
            //mp.stop();
            //mp.reset();
            //mp.release();
            //Log.i("DICJ","Arret de la musique.");
            //player.stop();
            Log.i("DICJ","Lancement de la musique suivante.");
            playNext();

    }

    @Override
    public void onDestroy()//s'execute a la destruction du service
    {
        Log.i("DICJ","Arret du service");
        //datasource.close();
        super.onDestroy();
        stopForeground(true);//enleve le service du premier plan et le rend succeptible d'etre detruit
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        Log.i("DICJ","Erreur de lecture du mediaPlayer." );
        mp.reset();
        //playSong();
        return false;
    }

   /* @Override
    public void onPrepared(MediaPlayer mp)//execute quand le media est pret pour la lecture
    {
        Log.i("DICJ","onPrepared");
        Log.i("DICJ","Preparation du media player, debut de la lecture, et envoi de la notification." );

        //mp.reset();
        Log.i("DICJ","Player Ready");
        playerReady = true;

        mp.start();//lancement

        player.start();

        if(player.getDuration() >= 5000)
        {
            Log.i("DICJ","Duree superieure a 5 secondes");
            //player.start();
            //debut de la musique
        }

        Intent notIntent = new Intent(this, bibliotheque.class);//creation de la notification
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        /*builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(titreMusique)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(titreMusique);
        Notification notification = builder.build();

        startForeground(NOTIFY_ID, notification);

    }*/

    @Override
    public boolean onUnbind(Intent intent)//avant l'arret du service
    {
        player.stop();//arret du player
        player.release();//libere les resources du player
        Log.i("DICJ","Unbind du service" );
        return false;
    }

    public void playSong()
    {
        playBackPause = false;

        player.reset();//reinitialisation du player

        musique playSong = listeMusiques.get(positionMusique);//recupere une musique a une position precise qui se trouve dans le tag du textView cliqué


        titreMusique=playSong.getTitreMusique();


        long currSong = playSong.getIdMusique();//j'obtiens l'id de la musique choisie

        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);//Uri de la musique à lire


        try
        {
            Log.i("DICJ","setDataSource");
            player.setDataSource(getApplicationContext(), trackUri);//lancement de la musique
        }
        catch (IOException e)
        {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        Log.i("DICJ","prepare");
        try
        {
            Log.i("DICJ","prepare player");
            player.prepare(); //prepare le player a lecture. Declenche le onPrepare
        }
        catch (IOException e)
        {
            Log.e("MUSIC SERVICE", "Error to prepare player", e);
        }

        Log.i("DICJ","playerReady");
        //playerReady = true;

        Log.i("DICJ","playerStart");
        player.start();



    }

    /*public void playSong()
    {

        playBackPause = false;

        player.reset();//reinitialisation du player

        musique playSong = listeMusiques.get(positionMusique);//recupere une musique a une position precise qui se trouve dans le tag du textView cliqué


        titreMusique=playSong.getTitreMusique();


        long currSong = playSong.getIdMusique();//j'obtiens l'id de la musique choisie

        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);//Uri de la musique à lire

        try
        {
            Log.i("DICJ","setDataSource");
            player.setDataSource(getApplicationContext(), trackUri);//lancement de la musique
        }
        catch(Exception e)
        {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        Log.i("DICJ","prepareAsync");
        player.prepare(); //prepare le player a lecture. Declenche le onPrepare

        //Log.i("DICJ","Duration :" + player.getDuration());
    }*/

    public int getPosition()
    {
        return player.getCurrentPosition();//position de la lecture actuelle
    }

    public int getDuration()
    {
        return player.getDuration();
    }

    public boolean isPlaying()
    {
        return player.isPlaying();
    }

    public void pausePlayer()
    {

        Log.i("DICJ","pause du mediaPlayer");
        Log.i("DICJ","Current position :" + player.getCurrentPosition());
        Log.i("DICJ","Duration :" + player.getDuration());
        playBackPause = true;

        player.pause();
    }

    public void seek(int pos)
    {
        Log.i("DICJ","Positionement de la musique a un instant t precis.");
        player.seekTo(pos);
    }

    public void start()
    {
        Log.i("DICJ","Start du mediaPlayer");
        player.start();
        playBackPause = false;
    }

    public void playPrev()
    {
        Log.i("DICJ","Lecture musique precedente.");
        if(shuffle)
        {
            int newSong = positionMusique;
            while(newSong==positionMusique)
            {
                newSong=random.nextInt(listeMusiques.size());
            }
            positionMusique=newSong;
        }
        else
        {
            positionMusique--;

            if(positionMusique < 0)//si on sort de la plage de musique
            {
                positionMusique=listeMusiques.size()-1;
            }
        }

        playSong();

    }

    public void playNext()
    {
        Log.i("DICJ","Lecture musique suivante.");

        //((bibliotheque)context).setControleur();
        if(shuffle)
        {
            int newSong = positionMusique;
            while(newSong==positionMusique)
            {
                newSong=random.nextInt(listeMusiques.size());
            }
            positionMusique=newSong;
        }
        else
        {
            positionMusique++;

            if(positionMusique > listeMusiques.size()-1)//si on sort de la plage de musique
            {
                positionMusique=0;
            }

        }

        playSong();


    }

    public void updateTheme(RelativeLayout layout)
    {
        switch (nomTheme)
        {
            case "bleu":
                //getApplication().setTheme(R.style.bleuBackground);
                //Log.i("DICJ","BLEU CLIQUÉ");
                layout.setBackgroundColor(getResources().getColor(R.color.bleu));
                break;

            case "jaune":

                //Log.i("DICJ","JAUNE CLIQUÉ");
                layout.setBackgroundColor(getResources().getColor(R.color.jaune));
                break;

            case "vert":

                //Log.i("DICJ","VERT CLIQUÉ");
                layout.setBackgroundColor(getResources().getColor(R.color.vert));
                break;

            case "rouge":

                //Log.i("DICJ","VERT CLIQUÉ");
                layout.setBackgroundColor(getResources().getColor(R.color.rouge));
                break;

            case "rose":

                //Log.i("DICJ","VERT CLIQUÉ");
                layout.setBackgroundColor(getResources().getColor(R.color.rose));
                break;

            case "bleuClair":

                //Log.i("DICJ","VERT CLIQUÉ");
                layout.setBackgroundColor(getResources().getColor(R.color.bleuClair));
                break;

            case "dore":

                //Log.i("DICJ","VERT CLIQUÉ");
                layout.setBackgroundColor(getResources().getColor(R.color.dore));
                break;

            case "orange":
                layout.setBackgroundColor(getResources().getColor(R.color.orange));
                break;

            case "capuccine":
                layout.setBackgroundColor(getResources().getColor(R.color.capuccine));
                break;

            case "marron":
                layout.setBackgroundColor(getResources().getColor(R.color.marron));
                break;

            case "saumon":
                layout.setBackgroundColor(getResources().getColor(R.color.saumon));
                break;

            case "magenta":
                layout.setBackgroundColor(getResources().getColor(R.color.magenta));
                break;

        }
    }

   /* public void afficheAllEnregistrementFavoris()
    {
        List<enregistrementFavoris> listeEnregistrementFavoris = datasource.getAllEnregistrements();

        for (enregistrementFavoris enregistrement:listeEnregistrementFavoris )
        {
            Log.i("DICJ","Enregistrement : "+enregistrement.getId()+", indiceMusique : " + enregistrement.getIndiceMusique());
        }

    }*/

    /*public boolean estUnFavoris(int indiceMusique)
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
    }*/

    public void setShuffle()
    {
        if(shuffle)
            shuffle=false;
        else
            shuffle=true;
    }


}
