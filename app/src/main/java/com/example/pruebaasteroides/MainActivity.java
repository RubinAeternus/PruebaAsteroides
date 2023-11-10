package com.example.pruebaasteroides;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static AlmacenPuntuaciones almacen;
	private TextView cabecera;
	private Animation animacion;
	private Button btnJugar,btnConf,btnAcer;
	MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		almacen=new AlmacenPuntuacionesArray();
		cabecera = (TextView) findViewById(R.id.tvCab);
		btnJugar = (Button) findViewById(R.id.btJug);
		btnConf = (Button) findViewById(R.id.btCon);
		btnAcer = (Button) findViewById(R.id.btAce);
		
		animacion = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
		cabecera.startAnimation(animacion);
		animacion = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_derecha);
		btnConf.startAnimation(animacion);
		animacion = AnimationUtils.loadAnimation(this, R.anim.aparecer);
		btnJugar.startAnimation(animacion);

		mp=MediaPlayer.create(this,R.raw.audio);

		SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		boolean musica = pref.getBoolean("musica",true);
		if (musica){
			mp.start();
		}else {
			mp.stop();
		}







	}

	public void configuracion(View view) {
		Intent i = new Intent(this, PreferenciasActivity.class);
		startActivity(i);


	}

	public void salir(View view) {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itAcercaDe:
			lanzarAcercaDe(null);
			break;

		case R.id.itConfig:
			configuracion(null);
			break;
		}
		return true;
	}*/

	public void lanzarAcercaDe(View view) {
		animacion = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_derecha);
		btnAcer.startAnimation(animacion);
		Intent i = new Intent(this, AcercaDeActivity.class);
		startActivity(i);
	}
	
	public void lanzarPuntuaciones(View view) {
		Intent i = new Intent(this, PuntuacionesActivity.class);
		startActivity(i);
	}
	
	public void lanzarJuego(View view) {
		Intent i = new Intent(this, JuegoActivity.class);
		startActivity(i);
	}

	@Override public void onRestart(){
		super.onRestart();
		recreate();
		mp.start();
	}

	@Override public void onStart(){
		super.onStart();
		mp.start();
	}

	@Override public void onPause(){
		super.onPause();
		mp.pause();

	}
}
