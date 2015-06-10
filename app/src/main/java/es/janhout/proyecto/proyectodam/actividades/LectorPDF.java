package es.janhout.proyecto.proyectodam.actividades;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class LectorPDF extends AppCompatActivity implements OnPageChangeListener {

    private Integer pageNumber;
    private Intent intentCompartir;
    private String fichero;

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onBackPressed() {
        borrarFicheroTmp();
        borrarFicheroFactura();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_pdf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        fichero = intent.getStringExtra("pdf");

        File f = new File(fichero);
        pageNumber = 1;
        PDFView pdfView = (PDFView) findViewById(R.id.pdfview);
        pdfView.fromFile(f)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .showMinimap(true)
                .enableSwipe(true)
                .load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lector_pdf, menu);
        MenuItem item = menu.findItem(R.id.action_compartir);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        crearIntentCompartir();
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(intentCompartir);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_guardar) {
            guardarFichero();
            return true;
        } else if (id == android.R.id.home) {
            borrarFicheroTmp();
            borrarFicheroFactura();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* *************************************************************************
     ******************* Interfaz OnPageChangedListener ************************
     *************************************************************************** */

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }

    /* *************************************************************************
     ******************************** Auxiliares *******************************
     *************************************************************************** */

    private void borrarFicheroTmp(){
        File f = new File(fichero);
        if(f.exists()) {
            f.delete();
        }
    }

    private void borrarFicheroFactura(){
        File f = new File(getExternalFilesDir(getString(R.string.carpeta_facturas)) + getString(R.string.fichero_enviar));
        if(f.exists()) {
            f.delete();
        }
    }

    private void crearIntentCompartir(){
        File from = new File(fichero);
        String f = fichero.substring(fichero.lastIndexOf("/")+1, fichero.lastIndexOf("."));
        File to = new File(getExternalFilesDir(getString(R.string.carpeta_facturas)) + getString(R.string.fichero_enviar));
        copy(from, to);
        intentCompartir = new Intent();
        intentCompartir.setAction(Intent.ACTION_SEND);
        intentCompartir.setType("application/pdf");
        intentCompartir.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(to));
    }

    private void copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst, false);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            Metodos.tostada(this, getString(R.string.factura_guardada));
        } catch (Exception e) {
            Metodos.tostada(this, getString(R.string.e_guardar_factura));
        }
    }

    private void guardarFichero() {
        File from = new File(fichero);
        String f = fichero.substring(fichero.lastIndexOf("/")+1, fichero.lastIndexOf("."));
        File to = new File(getExternalFilesDir(getString(R.string.carpeta_facturas)) + "/" + f + ".pdf");
        copy(from, to);
        //borrarFicheroTmp();
    }
}