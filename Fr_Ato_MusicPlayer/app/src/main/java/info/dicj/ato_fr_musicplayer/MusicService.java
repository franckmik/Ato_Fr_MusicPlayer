package info.dicj.ato_fr_musicplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.widget.Toast;

import info.dicj.ato_fr_musicplayer.items.musique;

/**
 * Created by utilisateur on 31/01/2017.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener{


    private MediaPlayer player; //song list

    private ArrayList<musique> listeMusiques;//liste des musiques

    private int positionMusique;//current position

    private final IBinder musicBind = new MusicBinder();

    private String titreMusique="";

    private static final int NOTIFY_ID=1;

    private boolean shuffle=false;

    private Random random;

    //Context context = getApplicationContext();

    /* MusicBinder est un canal de connexion avec le service*/

    public void setList(ArrayList<musique> listeMusiques)
    {
        this.listeMusiques=listeMusiques;
    }

    public void setPositionMusique(int positionMusique)
    {
        this.positionMusique = positionMusique;
    }

    @Override
    public void onCreate()//Creation du service
    {
        Log.i("DICJ","Creation du service");
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

        player.setOnPreparedListener(this);/*associe un écouteur d'événements dont la méthode onPrepared(MediaPlayer) est appelée lorsque
        le MediaPlayer est prêt*/

        player.setOnCompletionListener(this);

        player.setOnErrorListener(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }


    public class MusicBinder extends Binder//declaration de la classe MusicBinder
    {
        MusicService getService()
        {
            return MusicService.this;
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
        stopForeground(true);//enleve le service du premier plan et le rend succeptible d'etre detruit
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        Log.i("DICJ","Erreur de lecture du mediaPlayer." );
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp)//execute quand le media est pret pour la lecture
    {
        Log.i("DICJ","Preparation du media player, debut de la lecture, et envoi de la notification." );

        mp.start();//lancement

        player.start();

        if(player.getDuration() >= 5000)
        {
            Log.i("DICJ","Duree superieure a 5 secondes");
            //player.start();//debut de la musique

        }



        Intent notIntent = new Intent(this, bibliotheque.class);//creation de la notification
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(titreMusique)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(titreMusique);
        Notification notification = builder.build();

        startForeground(NOTIFY_ID, notification);


    }

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

        player.reset();//reinitialisation du player

        musique playSong = listeMusiques.get(positionMusique);//recupere une musique a une position precise qui se trouve dans le tag du textView cliqué


        titreMusique=playSong.getTitreMusique();


        long currSong = playSong.getIdMusique();//j'obtiens l'id de la musique choisie

        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);//Uri de la musique à lire

        try
        {
            player.setDataSource(getApplicationContext(), trackUri);//lancement de la musique
        }
        catch(Exception e)
        {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();//prepare le player a lecture. Declenche le onPrepare

        //Log.i("DICJ","Duration :" + player.getDuration());


    }

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

        player.pause();
    }

    public void seek(int posn)
    {
        Log.i("DICJ","Positionement de la musique a un instant t precis.");
        player.seekTo(posn);

    }

    public void start()
    {
        Log.i("DICJ","Start du mediaPlayer");
        player.start();

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

    public void setShuffle()
    {
        if(shuffle)
            shuffle=false;
        else
            shuffle=true;
    }





}
