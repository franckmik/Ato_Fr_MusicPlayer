package info.dicj.ato_fr_musicplayer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.dicj.ato_fr_musicplayer.R;

/**
 * Created by utilisateur on 30/01/2017.
 */
public class bibliotheque extends Fragment {

    public bibliotheque()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) //retourne une vue a partir du layout "fragment1"
    {

        View rootView = inflater.inflate(R.layout.bibliotheque,container,false);

        return  rootView;

    }
}
