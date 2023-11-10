package com.example.pruebaasteroides;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Vector;

public class PuntuacionesActivity extends ListActivity {
	private Vector<String> puntuaciones;
	private MiAdaptador adaptador;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.puntuaciones);

		
		puntuaciones = MainActivity.almacen.listaPuntuaciones(10);
		adaptador=new MiAdaptador(this, puntuaciones);
		setListAdapter(adaptador);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Object o = getListAdapter().getItem(position);
		String cad = "Seleccionado: "+Integer.toString(position)+" - "+o.toString();
		Toast.makeText(this, cad, Toast.LENGTH_LONG).show();
	}
}
