package info.dicj.ato_fr_musicplayer.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import info.dicj.ato_fr_musicplayer.R;
import info.dicj.ato_fr_musicplayer.items.musique;


public class musiqueAdapter  extends BaseAdapter {

    private ArrayList<musique> listeMusiques;
    private Context context;


    public musiqueAdapter(Context context, ArrayList<musique> listeMusiques){
        this.listeMusiques=listeMusiques;
       this.context = context;
    }

    @Override
    public int getCount()
    {
        return listeMusiques.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            View v = View.inflate(context, R.layout.item_liste_musique,null);

            TextView titreMusique = (TextView)v.findViewById(R.id.titreMusique);
            TextView artisteMusique = (TextView)v.findViewById(R.id.artisteMusique);
            ImageView imageMusique = (ImageView)v.findViewById(R.id.imageMusique);//recupere l'image de l'item dans le layout item_sliding_menu

            musique musique = listeMusiques.get(position);

            titreMusique.setText(musique.getTitreMusique());
            artisteMusique.setText(musique.getArtisteMusique());
            imageMusique.setImageResource(musique.getIdImage());

            v.setTag(position);

            titreMusique.setGravity(Gravity.CENTER_VERTICAL);


            return v;
        }
}
