package es.janhout.proyecto.proyectodam.fragmentos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.util.SlidingTabLayout;

public class FragmentoContenedorListaFacturas extends Fragment {

    static class FragmentoListaFacturasItem {
        private boolean todas;
        private int idCliente;
        private int estadoFactura;
        private String query;

        FragmentoListaFacturasItem(boolean todas, int idCliente, int estadoFactura, String query) {
            this.todas = todas;
            this.idCliente = idCliente;
            this.estadoFactura = estadoFactura;
            this.query = query;
        }

        Fragment createFragment() {
            return FragmentoListaFacturas.newInstance(todas, query, idCliente, estadoFactura);
        }

        public int getEstadoFactura() {
            return estadoFactura;
        }
    }

    private List<FragmentoListaFacturasItem> mTabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int idCliente = getArguments().getInt("idCliente");
        String query = getArguments().getString("query");
        boolean todas = getArguments().getBoolean("todas");
        mTabs = new ArrayList<>();
        mTabs.add(new FragmentoListaFacturasItem (todas, idCliente, 2, query));

        mTabs.add(new FragmentoListaFacturasItem (todas, idCliente, 0, query));

        mTabs.add(new FragmentoListaFacturasItem (todas, idCliente, 1, query));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contenedor_lista_facturas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new AdaptadorPager(getChildFragmentManager()));

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                switch (mTabs.get(position).getEstadoFactura()) {
                    case 0:
                        return Color.RED;
                    case 1:
                        return Color.GREEN;
                    case 2:
                        return Color.YELLOW;
                    default:
                        return Color.BLUE;
                }
            }
        });
    }

    class AdaptadorPager extends FragmentPagerAdapter {

        AdaptadorPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String titulo;
            switch (mTabs.get(position).getEstadoFactura()){
                case 0:
                    titulo = getString(R.string.s_facturas_impagadas);
                    break;
                case 1:
                    titulo = getString(R.string.s_facturas_pagadas);
                    break;
                case 2:
                    titulo = getString(R.string.s_facturas_borradores);
                    break;
                default:
                    titulo = getString(R.string.s_facturas_otras);
                    break;
            }
            return titulo;
        }
    }
}