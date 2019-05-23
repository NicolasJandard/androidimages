package master.ccm.m1.androidimages;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class IntentServiceImage extends IntentService {
    private final static String TAG = "IntentServiceImage";
    private final static String PICTURES_DIR = "projetApp";

    private File baseDirectory;

    public IntentServiceImage() {
        super(TAG);
        this.baseDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + File.separator + PICTURES_DIR + File.separator);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Crée le dossier ou sont stockées les images que l'on télécharge (si le dossier existe il n'est pas recréé
        if(!baseDirectory.exists()) {
            baseDirectory.mkdir();
        }

        //Récupère l'url passé par l'activité au service et déduit le nom du fichier à partir de cette url
        ArrayList<String> urls = intent.getStringArrayListExtra("urls");

        urls.forEach(this::downloadImage);
    }

    private void downloadImage(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);

        //On teste si le fichier que l'on veut télécharger existe déjà
        File fileToDownload = new File(baseDirectory.getAbsolutePath() + File.separator + filename);

        if(!fileToDownload.exists()) {
            //Récupère le service de download d'Android
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            //Crée la requête de téléchargement selon l'url
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);

            /* Gère les paramètres relatifs au fichier :
             *   - Il est téléchargé dans le dossier "images" du système sous le dossier "projetApp"
             *   - Il a le nom que l'on à déduit depuis l'url
             *   - Une notification apparait lorsque le fichier est en cours de téléchargement et lorsqu'il est fini
             */
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + PICTURES_DIR + File.separator + filename)
                    .setTitle(filename);

            //Télécharge le fichier selon la requête demandée au dessus
            downloadManager.enqueue(request);
        }
        else {
            sendBroadcast("L'image de l'url "+url+" existe déjà");
        }

    }

    private void sendBroadcast(String message) {
        Intent intent = new Intent ("message");
        intent.putExtra("stringMessage", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
