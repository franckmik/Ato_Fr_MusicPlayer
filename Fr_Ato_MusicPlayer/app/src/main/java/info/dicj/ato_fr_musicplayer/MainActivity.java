package info.dicj.ato_fr_musicplayer;

import android.app.Fragment;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.dicj.ato_fr_musicplayer.adapter.slidingMenuAdapter;
import info.dicj.ato_fr_musicplayer.items.itemSlideMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private List<itemSlideMenu> listeItems;//liste de slidingDeMenu donc d'item
    private slidingMenuAdapter adaptateur;//L'adaptateur qui affiche chaque item de mon menu de slide
    private ListView listeViewItems;//liste de vues qui se trouve dans le layout "Main_Activity"
    private DrawerLayout ecranPrincipal;//ecran principal
    private ActionBarDrawerToggle actionBarDrawerToggle;//bar de navigation( menu de slide)
    public final static String EXTRA_MESSAGE = "labIntention.info.dicj.ato_fr_musicplayer.MESSAGE";
    TextView texteBibliotheque;


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
}
