package es.janhout.proyecto.proyectodam.util;

import android.widget.AbsListView;

public abstract class ScrollInfinito implements AbsListView.OnScrollListener {

    private int bajoLista = 5;
    private int paginaActual = 0;
    private int cargadosAnterior = 0;
    private boolean cargando = true;

    public ScrollInfinito(int bajoLista) {
        this.bajoLista = bajoLista;
    }

    public abstract void cargaMas(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
        // reseteamos el scroll si vemos que la lista tiene menos items que los cargados
        // en la ultima vez
        if (totalItemCount < cargadosAnterior) {
            this.paginaActual = 0;
            this.cargadosAnterior = totalItemCount;
            if (totalItemCount == 0) { this.cargando = true; }
        }
        // si el estado es cargando y el número de elementos actual es mayor que el número de elementos anterior,
        // es que ya hemos terminado la carga, por lo que cargando pasaría a false.
        if (cargando && (totalItemCount > cargadosAnterior)) {
            cargando = false;
            cargadosAnterior = totalItemCount;
            paginaActual++;
        }
        // si no está cargando, comprobamos si hemos llegado al límite(bajoLista) para ver si
        // tenemos que cargar más
        if (!cargando && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bajoLista)) {
            cargaMas(paginaActual, totalItemCount);
            cargando = true;
        }
    }
}

