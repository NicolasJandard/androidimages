package mater.ccm.projetandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ListUrl extends AppCompatActivity {


    //nom de la listView
    ListView maListeView;

    //compteur deligne
    int intCount = 0;

    //tableau qui contiendra laliste des urls récupérer par le fichier
    String[] maListeUrl;

    //compte le nombre de ligne dans le fichier text
    InputStream inputStreamCounter;
    BufferedReader bufferedReaderCounter;

    //charge les valeurs du fichier et les stocke dans l'array
    InputStream inputStreamLoader;
    BufferedReader bufferedReaderLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_url);

        //la liste view dans le fichier xml
        maListeView = (ListView) findViewById(R.id.maListeUrl);

        //compte le nombre de ligne du fichier

        inputStreamCounter = this.getResources().openRawResource(R.raw.mon_fichier_liste_url);
        bufferedReaderCounter = new BufferedReader(new InputStreamReader(inputStreamCounter));

        //charge les valeurs du fichier
        inputStreamLoader = this.getResources().openRawResource(R.raw.mon_fichier_liste_url);
        bufferedReaderLoader = new BufferedReader(new InputStreamReader(inputStreamLoader));

        //compte le nombre de ligne dans le fichier
        try {
            //tant qu'il ya des lignes non vide dans le fichier
            while (bufferedReaderCounter.readLine() != null) {
                intCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //crééer le tableau avec le nombre de ligne du fichier
        maListeUrl = new String[intCount];

        //charge les lignes du fichier dans le tableau de string
        try {
            for (int i = 0; i < intCount; i++) {

                maListeUrl[i] = bufferedReaderLoader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * @aram this représente le context
         * @param layout pour une ligne de la listeView
         * @param id id de la textView
         * @param maListeUrl la liste url récupérer du fichier
         *
         */
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, maListeUrl);

        maListeView.setAdapter(adapter);
    }
}