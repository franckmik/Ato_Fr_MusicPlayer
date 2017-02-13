package info.dicj.ato_fr_musicplayer.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.dicj.ato_fr_musicplayer.items.itemSlideMenu;
import info.dicj.ato_fr_musicplayer.R;

/**
 * Created by utilisateur on 23/01/2017.
 */
public class slidingMenuAdapter extends BaseAdapter {

    private Context context;
    private List<itemSlideMenu> listeItems;//liste d'objets de type itemSlideMenu.Dans le fond elle contient tous le item de mon menu de slide

    public slidingMenuAdapter(Context context, List<itemSlideMenu> listeItems) {
        this.context = context;
        this.listeItems = listeItems;
    }

    @Override
    public int getCount() {

        return listeItems.size();

    }

    @Override
    public Object getItem(int position) {
        return listeItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context, R.layout.item_menu_slide,null);//recupere la vue "item_sliding_menu" qui represente l'affichage d'un item.
        ImageView img = (ImageView)v.findViewById(R.id.imageItem);//recupere l'image de l'item dans le layout item_sliding_menu
        TextView tv = (TextView)v.findViewById(R.id.titreItem);//recupere le texte de l'item dans le layout item_sliding_menu

        itemSlideMenu item = listeItems.get(position);//je selectionne une item dans la liste des item
        img.setImageResource(item.getIdImage());//l'image de l'item du layout "itemSlidingMenu" contiendra celle de l'item de la liste
        tv.setText(item.getTitreImage());//de meme pour le texte

        tv.setGravity(Gravity.CENTER_VERTICAL);


        return v;
    }
}
