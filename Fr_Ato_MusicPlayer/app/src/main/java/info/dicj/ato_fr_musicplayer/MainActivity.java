package info.dicj.ato_fr_musicplayer;

import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import info.dicj.ato_fr_musicplayer.adapter.slidingMenuAdapter;
import info.dicj.ato_fr_musicplayer.fragment.acceuil;
import info.dicj.ato_fr_musicplayer.fragment.bibliotheque;
import info.dicj.ato_fr_musicplayer.fragment.favoris;
import info.dicj.ato_fr_musicplayer.fragment.theme;
import info.dicj.ato_fr_musicplayer.model.itemSlideMenu;

public class MainActivity extends AppCompatActivity {

    private List<itemSlideMenu> listeItems;//liste de slidingDeMenu donc d'item
    private slidingMenuAdapter adaptateur;//L'adaptateur qui affiche chaque item de mon menu de slide
    private ListView listeViewItems;//liste de vues du layout "Main_Activity"
    private DrawerLayout ecranPrincipal;
    //private RelativeLayout mainContent;
    private ActionBarDrawerToggle actionBarDrawerToggle;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            listeViewItems = (ListView) findViewById(R.id.listeMenuSlide);
            ecranPrincipal = (DrawerLayout) findViewById(R.id.ecranPrincipal);//je recupere tout mon affichage principal
            //mainContent = (RelativeLayout) findViewById(R.id.main_content);

            listeItems = new ArrayList<>();



            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Acceuil"));
            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Bibliotheque"));//Dans ma liste contenant les item j'ajoute un item avec l'id de son image et son titre
            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Favoris"));
            listeItems.add(new itemSlideMenu(R.drawable.tulips, "Themes"));

            adaptateur = new slidingMenuAdapter(this, listeItems);//je cree mon adaptateur en lui passant en parametre ma liste de slide
            listeViewItems.setAdapter(adaptateur);//la liste de vues reference a toutes les items de mon menu


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            listeViewItems.setItemChecked(0, true);

            ecranPrincipal.closeDrawer(listeViewItems);

            replaceFragment(0);


            listeViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() /*Je cree un evenement sur chacun de mes items*/ {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    setTitle(listeItems.get(position).getTitreImage());
                    if(position == 0)
                    {
                        setTitle("EasyMusic");
                    }

                    listeViewItems.setItemChecked(position, true);

                    replaceFragment(position);

                    ecranPrincipal.closeDrawer(listeViewItems);
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

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
            getMenuInflater().inflate(R.menu.menu,menu);
            return true;
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


        private void replaceFragment(int pos)
        {
            Fragment fragment = null;

            switch (pos)
            {
                case 0:
                    fragment = new acceuil();
                    break;
                case 1:
                    fragment = new bibliotheque();
                    break;
                case 2:
                    fragment = new favoris();
                    break;
                case 3:
                    fragment = new theme();
                    break;
                default:
                    fragment = new acceuil();
                    break;
            }

            if(fragment != null)
            {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.contenuPrincipal,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        }

}
