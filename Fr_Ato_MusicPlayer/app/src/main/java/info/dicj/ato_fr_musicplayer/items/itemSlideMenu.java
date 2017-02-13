package info.dicj.ato_fr_musicplayer.items;

/**
 * Created by utilisateur on 30/01/2017.
 */
public class itemSlideMenu {

    private int idImage;
    private String titreImage;

    public itemSlideMenu(int idImage, String titreImage) {
        this.idImage = idImage;
        this.titreImage = titreImage;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public String getTitreImage() {
        return titreImage;
    }

    public void setTitreImage(String titreImage) {
        this.titreImage = titreImage;
    }



}
