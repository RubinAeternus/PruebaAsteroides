package com.example.pruebaasteroides;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class VistaJuego extends View implements SensorEventListener {


	private Vector<Grafico>Asteroides;
	private Grafico nave;
	private Grafico misil;
	private int numAsteroides=5; //numero inicial de asteroides
	private int numFragmentos;//fragmentos en que se divide
	private final static int MIN_DISTANCIA=6;  //Distancia menor de la qual no habr� movimiento
	private int giroNave,aceleracionNave;
	private boolean disparo;
	private boolean misilActivo=false;

	MediaPlayer mpDisparo, mpExplosion;
	
	//Thread encargado de procesar el juego
	private ThreadJuego thread = new ThreadJuego();
	//cada cuanto queremos procesar cambios (ms)
	private static int PERIODO_PROCESO=12;
	private static int PASO_VELOCIDAD_MISIL=12;
	private int tiempoMisil;
	//cuando se realizo el ultimo proceso
	private long ultimoProceso=0;
	private float mX=0,mY=0;


	public VistaJuego(Context context, AttributeSet attrs) {
		super(context, attrs);

		Drawable drawableNave,drawableAsteroide,drawableMisil =null;

		//SENSOR
		SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		List <Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (!listSensors.isEmpty()){
			Sensor orientationSensor = listSensors.get(0);
			mSensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
		}






		mpDisparo= MediaPlayer.create(context,R.raw.misil);
		mpExplosion= MediaPlayer.create(context,R.raw.explosion);
		
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
		boolean musica = pref.getBoolean("musica",false);
//		String graficos = pref.getString("graficos", "4");
		numAsteroides = Integer.parseInt(pref.getString("fragmentos", "3"));
		
		if (pref.getString("graficos","0").equals("2")){ //Vectorial
			Path pathNave = new Path();
			pathNave.moveTo(0.0F, 0.0F);
			pathNave.moveTo(1.0F, 0.5F);
			pathNave.moveTo(0.0F, 1.0F);
			pathNave.moveTo(0.0F, 0.0F);
			ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1.0F, 1.0F));
			dNave.getPaint().setColor(-1);
			dNave.getPaint().setStyle(Style.STROKE);
			dNave.setIntrinsicHeight(15);
			dNave.setIntrinsicWidth(20);
			drawableNave=dNave;

			Path pathAsteroide = new Path();
			pathAsteroide.moveTo(0.3F, 0.0F);
			pathAsteroide.moveTo(0.6F, 0.0F);
			pathAsteroide.moveTo(0.6F, 0.3F);
			pathAsteroide.moveTo(0.8F, 0.2F);
			pathAsteroide.moveTo(1.0F, 0.4F);
			pathAsteroide.moveTo(0.8F, 0.6F);
			pathAsteroide.moveTo(0.9F, 0.9F);
			pathAsteroide.moveTo(0.8F, 1.0F);
			pathAsteroide.moveTo(0.4F, 1.0F);
			pathAsteroide.moveTo(0.0F, 0.6F);
			pathAsteroide.moveTo(0.0F, 0.2F);
			pathAsteroide.moveTo(0.3F, 0.0F);
			ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1.0F, 1.0F));
			dAsteroide.getPaint().setColor(Color.RED);
			dAsteroide.getPaint().setStyle(Style.STROKE);
			dAsteroide.setIntrinsicHeight(50);
			dAsteroide.setIntrinsicWidth(50);
			drawableAsteroide=dAsteroide;
			 setBackgroundColor(-16777216);
		} 
		else {
			drawableAsteroide=context.getResources().getDrawable(R.drawable.asteroide1);
			drawableNave=context.getResources().getDrawable(R.drawable.nave);
			drawableMisil=context.getResources().getDrawable(R.drawable.misil1);
		}	
		Asteroides=new Vector<Grafico>();
		for (int i=0;i<numAsteroides;i++){
			Grafico asteroide=new Grafico(this,drawableAsteroide);
			asteroide.setIncX(Math.random()*4-2);
			asteroide.setIncY(Math.random()*4-2);
			asteroide.setAngulo((int)(Math.random()*360));
			asteroide.setRotacion((int)(Math.random()*8-4));
			Asteroides.add(asteroide);
		}
		nave = new Grafico(this,drawableNave);
		misil = new Grafico(this,drawableMisil);






	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int angulo,ancho,alto;
		int codigoAccion = event.getAction()& MotionEvent.ACTION_MASK;
		float x=event.getX();
		float y=event.getY();
		//Calculamos las distancias recorridas
		switch (codigoAccion){
		case MotionEvent.ACTION_MOVE:
			float dx=Math.abs(x-mX);
			float dy=Math.abs(y-mY);
			if (dy<MIN_DISTANCIA && dx>MIN_DISTANCIA){
				//Si tenemos una distancia horizontal(X) suficientemente grande(>MIN_DISTANCIA)
				//Y la distancia vertical no es muy grande: tocamos angulo nave
				//Para ver el grado del giro, tendremos que el maximo giro correspondera
				//a un desplazamiento por lo ancho de la pantalla
				giroNave=Math.round((x-mX) / 2); // a distancias mayores,mayores giros

				disparo=false;
			}else if (dx<MIN_DISTANCIA && dy>MIN_DISTANCIA){ //vertical
				aceleracionNave=Math.round((mY-y)/50);
				disparo=false;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			disparo=true;
			break;
		case MotionEvent.ACTION_UP:
			giroNave=0;
			aceleracionNave=0;
			if (disparo){
				ActivaMisil();
			}
			break;
		}
		mX=x;
		mY=y;
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (Grafico asteroide: Asteroides){
			asteroide.dibujaGrafico(canvas);
		}
		nave.dibujaGrafico(canvas);
		if (misilActivo)
			misil.dibujaGrafico(canvas);
	}
	
	@Override
	protected void onSizeChanged(int ancho, int alto, int ancho_ant, int alto_ant) {
		super.onSizeChanged(ancho, alto, ancho_ant, alto_ant);
		nave.setPosX(ancho/2-nave.getAncho()/2);
		nave.setPosY(alto/2-nave.getAlto()/2);
		//Una vez que conocemos nuestro ancho y alto
		boolean condX,condY=false;
		for (Grafico asteroide: Asteroides){
			do {
				asteroide.setPosX(Math.random()*(ancho-asteroide.getAncho()));
				asteroide.setPosY(Math.random()*(alto-asteroide.getAlto()));
				condX=asteroide.getPosX()>nave.getPosX()-80 && asteroide.getPosX()<nave.getPosX()+80;
				condY=asteroide.getPosY()>nave.getPosY()-80 && asteroide.getPosY()<nave.getPosY()+80;
			}while (condX || condY);
//			} while (nave.distancia(asteroide)>80);
		}
		thread.start();
		
	}
	
	protected void actualizaFisica(){
		long ahora=System.currentTimeMillis();

		if (ultimoProceso+PERIODO_PROCESO>ahora){
			return;
		}

		ultimoProceso = ahora;
		//Actualizamos 
		nave.setAngulo((int)(nave.getAngulo()+giroNave));
		double nIncX = nave.getIncX()+aceleracionNave*Math.cos(Math.toRadians(nave.getAngulo()));
		double nIncY = nave.getIncY()+aceleracionNave*Math.sin(Math.toRadians(nave.getAngulo()));

		if (Math.hypot(nIncX, nIncY)<=Grafico.getMaxVelocidad()) {
			nave.setIncX(nIncX);
			nave.setIncY(nIncY);
		}
		nave.incrementaPos(0);
		for (Grafico asteroide : Asteroides){
			asteroide.incrementaPos(0);
		}
		if (misilActivo){
			misil.incrementaPos(0);
			tiempoMisil--;
			if (tiempoMisil<0) {
				misilActivo=false;
			} else {
				if (Asteroides != null && misil != null) {
					for (int i = 0; i < Asteroides.size(); i++)
						if (misil.verificarColsion(Asteroides.elementAt(i))) {
							destruyeAsteroide(i);

							break;
						}
				}
			}
			
		}

	}

	public void verificarVictoria(Context context) {
		if (Asteroides.isEmpty()) {

			if (context != null) {
				Toast.makeText(context, "VICTORIA", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void destruyeAsteroide(int i){

		Asteroides.remove(i);
		misilActivo=false;
		mpExplosion.start();
	}
	
	private void ActivaMisil(){
		misil.setPosX(nave.getPosX()+nave.getAncho()/2-misil.getAncho()/2);
		misil.setPosY(nave.getPosY()+nave.getAlto()/2-misil.getAlto()/2);
		misil.setAngulo(nave.getAngulo());
		misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo()))*PASO_VELOCIDAD_MISIL);
		misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo()))*PASO_VELOCIDAD_MISIL);
		tiempoMisil = (int) Math.min(this.getWidth()/Math.abs(misil.getIncX()),
				                     this.getWidth()/Math.abs(misil.getIncY()))-2;
		misilActivo=true;
		mpDisparo.start();
	}

	private boolean hayValorInicial = false;
	private float valorInicial;

	private static final int FILTRO_TAMANO = 10; // Puedes ajustar este tamaño según sea necesario
	private float[] valoresFiltrados = new float[FILTRO_TAMANO];
	private int indiceFiltro = 0;
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		float valor = sensorEvent.values[0];

		if (!hayValorInicial) {
			valorInicial = valor;
			hayValorInicial = true;
		}

		// Aplica un filtro de media móvil
		valoresFiltrados[indiceFiltro] = valor - valorInicial;
		indiceFiltro = (indiceFiltro + 1) % FILTRO_TAMANO;

		// Calcula el promedio de los valores filtrados
		float suma = 0;
		for (float valorFiltrado : valoresFiltrados) {
			suma += valorFiltrado;
		}
		giroNave = (int) (suma / FILTRO_TAMANO) / 5; // Ajusta el divisor según sea necesario
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}



	class ThreadJuego extends Thread {
		private boolean pausa, corriendo;
		@Override
		public void run() {
			while(true && !Asteroides.isEmpty())
				actualizaFisica();
		}


		public void detener(){
			corriendo = false;
			interrupt();
		}
	}
	
	
}
