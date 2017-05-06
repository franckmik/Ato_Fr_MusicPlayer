package info.dicj.ato_fr_musicplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.dicj.ato_fr_musicplayer.R;
import info.dicj.ato_fr_musicplayer.items.itemSlideMenu;
import info.dicj.ato_fr_musicplayer.items.musique;

/**
 * Created by utilisateur on 11/04/2017.
 */
public class musiqueAdapterFavoris extends BaseAdapter
{
    private ArrayList<musique> listeMusiquesFavoris;
    private Context context;


    public musiqueAdapterFavoris(Context context, ArrayList<musique> listeMusiquesFavoris){
        this.listeMusiquesFavoris=listeMusiquesFavoris;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return listeMusiquesFavoris.size();
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

    private class ViewHolder {

        TextView titreMusique;
        TextView artisteMusique;
        ImageView imageMenu;

    }

    ViewHolder holder = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /*musique musique = listeMusiquesFavoris.get(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_liste_musique_favoris, null);
            holder = new ViewHolder();
            holder.titreMusique = (TextView) convertView.findViewById(R.id.titreMusique);
            holder.artisteMusique = (TextView) convertView.findViewById(R.id.artisteMusique);
            holder.imageMenu = (ImageView) convertView.findViewById(R.id.imageMenuInflate);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titreMusique.setText(musique.getTitreMusique());
        holder.artisteMusique.setText(musique.getArtisteMusique());

        try
        {
            holder.imageMenu.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {


                    switch (v.getId())
                    {

                        case R.id.imageMenuInflate:

                            PopupMenu popup = new PopupMenu(context.getApplicationContext(), v);
                            popup.getMenuInflater().inflate(R.menu.menu_delete_favoris,
                                    popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                            {
                                @Override
                                public boolean onMenuItemClick(MenuItem item)
                                {

                                    switch (item.getItemId())
                                    {
                                        case R.id.deleteFavoris:

                                            Toast.makeText(context.getApplicationContext(), " Delete de la musique dans les favoris ", Toast.LENGTH_LONG).show();

                                        break;

                                        default:

                                        break;
                                    }

                                    return true;
                                }
                            });

                            break;

                            default:

                            break;
                    }


                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }

        return convertView;*/
        View v = View.inflate(context, R.layout.item_liste_musique_favoris,null);

        TextView titreMusique = (TextView)v.findViewById(R.id.titreMusique);
        TextView artisteMusique = (TextView)v.findViewById(R.id.artisteMusique);
        ImageView imageMusique = (ImageView)v.findViewById(R.id.imageMusique);//recupere l'image de l'item dans le layout item_sliding_menu
        ImageView imageDeleteFavoris = (ImageView)v.findViewById(R.id.imageDeleteFavoris);

        musique musique = listeMusiquesFavoris.get(position);

        titreMusique.setText(musique.getTitreMusique());
        artisteMusique.setText(musique.getArtisteMusique());
        imageMusique.setImageResource(musique.getIdImage());

        v.setTag(position);

        int[] positionIndice = new int[2];

        positionIndice[0]= position;
        positionIndice[1]= musique.getIdMusique();
        //imageDeleteFavoris.setTag(position);
        imageDeleteFavoris.setTag(positionIndice);

        titreMusique.setGravity(Gravity.CENTER_VERTICAL);

        return v;
    }
}
