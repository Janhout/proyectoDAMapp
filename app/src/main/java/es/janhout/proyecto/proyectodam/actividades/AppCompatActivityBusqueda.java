package es.janhout.proyecto.proyectodam.actividades;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;

import es.janhout.proyecto.proyectodam.util.Temporizador;


public abstract class AppCompatActivityBusqueda extends AppCompatActivity implements Temporizador.OnTimerCompleteListener,
        SearchView.OnQueryTextListener {

    private String textoBusqueda;
    private Temporizador hebraTemporizador;

    private static final int TIEMPO_ESPERA = 800;

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public boolean onQueryTextChange(String text) {
        textoBusqueda = text;
        if (hebraTemporizador != null && !hebraTemporizador.isCancelled()) {
            hebraTemporizador.cancel(true);
            hebraTemporizador = null;
        }
        hebraTemporizador = new Temporizador(TIEMPO_ESPERA, this);
        hebraTemporizador.execute();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        textoBusqueda = text;
        busqueda(textoBusqueda);
        return true;
    }

    /* *************************************************************************
     ***************** Interfaz Temporizador Completado ************************
     *************************************************************************** */

    @Override
    public void temporizadorCompletado(boolean correcto) {
        if (correcto) {
            busqueda(textoBusqueda);
        }
    }

    /* *************************************************************************
     ************************* Métodos abstractos ******************************
     *************************************************************************** */

    protected abstract void busqueda(String textoBusqueda);
}
