package master.ccm.m1.androidimages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * CLass qui récupère les images dans déja téléchargées à l'emplacement choisit et les affiches dans la gridView
 */
public class ImageListActivity extends AppCompatActivity {
    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList;
    private final static String PICTURES_DIR = "projetApp";
    private final static  String cheminRepertoire = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + PICTURES_DIR + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        /**
         * fait référence à la grille d'image
         */
        this.imageGrid = (GridView) findViewById(R.id.gridview);

        /**
         * liste qui contient les image au format bitmap
         */
        this.bitmapList = new ArrayList<Bitmap>();


        /**
         * le chemein du réperoire qui continet les images
         */
        File directory = new File(cheminRepertoire);
        /**
         * la liste des images contenu dans le réperoire
         */
        File[] files = directory.listFiles();

        try {
            /**
             * chaque image récupérer on les transforme et les ajoutes dans la
             * bitmap
             */
            for (int i = 0; i < files.length; i++)
                {
                    Log.d("Files", "FileName:" + files[i].getName());
                    /** creer une image **/
                    File imgFile = new  File(cheminRepertoire + files[i].getName());
                if(imgFile.exists()){
                    //récpère la bitmap
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //ajoute la bitmap à la list
                    this.bitmapList.add(myBitmap);


                };
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * met à jour la grille d'image avec les les images récupérer dans la bitmap
         */
        this.imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList));
    }




}
