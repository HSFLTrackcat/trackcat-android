package de.mobcom.group3.gotrack;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.mobcom.group3.gotrack.InExport.Import;

public class ImportActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_activity);
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            try {
                File file = new File(getCacheDir(), "document");
                InputStream inputStream=getContentResolver().openInputStream(uri);
                Import.getImport().handleSend(this, file, inputStream);
                //Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            }
            catch (Exception ex) {
                Toast.makeText(this,
                        "Die Datei konnte nicht importiert werden", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
        else {
            Log.i("Import", "Der Intent war kein Import : " + action);
        }
        if (MainActivity.isActiv) {
            Toast.makeText(this,"GoTrack läuft bereits", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Intent start = new Intent(this, MainActivity.class);
            startActivity(start);
        }
    }
}
