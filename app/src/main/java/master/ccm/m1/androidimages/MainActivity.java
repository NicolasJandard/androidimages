package master.ccm.m1.androidimages;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView maListView;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    //Déclaration d'un BroadcastReceiver pour recevoir les messages d'erreur du service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getStringExtra("stringMessage"), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Demande les autorisations de l'utilisateur pour le stockage externe (Android API > 23)
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        copyDefaultFile();
        //Initialise la ListView
        maListView = findViewById(R.id.maListeUrl);
        /*
         * @param this représente le context
         * @param layout Indique le layout utilisé pour une ligne de la listView
         * @param id id de la textView
         * @param maListeUrl La liste d'URLs récupérée du fichier
         *
         */
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, getContentsFromFile());
        maListView.setAdapter(adapter);

        /*
         * Définition d'un listener pour le clic d'un élément de la ListView
         * On charge le tableau d'URLs à la sélection d'un élément et on le
         * retire du tableau à la déselection
         */
        maListView.setOnItemClickListener((parent, view, position, id) -> {
            String url = maListView.getItemAtPosition(position).toString();

            //si l'élément est sélectionné
            if(maListView.isItemChecked(position)) {
                urls.add(url);
            }
            else {
                urls.remove(url);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("message"));
    }

    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    /**
     * Listener du bouton de téléchargement, créé un Intent et lui injecte
     * le tableau d'URLs, pui exécute le service de téléchargement
     * @param view
     */
    public void downloadSelection(View view) {
        Intent i = new Intent(MainActivity.this, IntentServiceImage.class);
        i.putStringArrayListExtra("urls", urls);
        startService(i);
    }

    public void addUrl(View view) {
        EditText editTextAdd = findViewById(R.id.editText_add_url);
        String contentToWrite = editTextAdd.getText().toString() + "\n";
        if(URLUtil.isValidUrl(contentToWrite)) {
            /*try {
                FileOutputStream outputStream = openFileOutput(getDefaultFilename(), Context.MODE_APPEND);
                outputStream.write(contentToWrite.getBytes());
                outputStream.close();
                refreshListView();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        else {
            Toast.makeText(this, "L'URL n'est pas valide", Toast.LENGTH_LONG).show();
        }
    }

    public void refreshListView() {
        adapter.clear();
        adapter.addAll(getContentsFromFile());
        adapter.notifyDataSetChanged();
    }

    /**
     * Récupère les données du fichier texte
     * @return String[] Tableau contenant les URLs du fichier texte
     */
    private ArrayList<String> getContentsFromFile() {
        int intCount = 0;
        File file = new File(this.getFilesDir(), getDefaultFilename());
        InputStream inputStreamCounter = this.getResources().openRawResource(R.raw.mon_fichier_liste_url);
        InputStream inputStreamLoader = this.getResources().openRawResource(R.raw.mon_fichier_liste_url);

        if(file.exists()) {
            try {
                inputStreamCounter = openFileInput(getDefaultFilename());
                inputStreamLoader = openFileInput(getDefaultFilename());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //Compte le nombre de lignes du fichier texte qui contient les URLs
        BufferedReader bufferedReaderCounter = new BufferedReader(new InputStreamReader(inputStreamCounter));
        //Charge les valeurs du fichier texte
        BufferedReader bufferedReaderLoader = new BufferedReader(new InputStreamReader(inputStreamLoader));

        //Compte le nombre de ligne dans le fichier
        try {
            //Tant qu'il y a des lignes non vide dans le fichier
            while (bufferedReaderCounter.readLine() != null) {
                intCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Créé un tableau ayant le nombre de lignes du fichier
        String[] maListeUrl = new String[intCount];

        //Charge les lignes du fichier dans le tableau de string
        try {
            for (int i = 0; i < intCount; i++) {

                maListeUrl[i] = bufferedReaderLoader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(Arrays.asList(maListeUrl));
    }

    private String getDefaultFilename() {
        TypedValue value = new TypedValue();
        getResources().getValue(R.raw.mon_fichier_liste_url, value, true);

        return value.string.toString().substring(value.string.toString().lastIndexOf("/") +1);
    }

    private void copyDefaultFile() {
        File file = new File(this.getFilesDir(), getDefaultFilename());
        InputStream is = this.getResources().openRawResource(R.raw.mon_fichier_liste_url);
        if(!file.exists()) {
            try {
                file.createNewFile();
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                OutputStream outStream = new FileOutputStream(file);
                outStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * APPLER LORSQUE L'UTILISATEUR CLIQUE SUR LE BOUTON AFFICHER LES IMAGES
     * @param view
     */
    public void afficherListImages(View view) {
        Intent i = new Intent(this, ImageListActivity.class);
        startActivity(i);
    }
}
