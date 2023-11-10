package com.example.pruebaasteroides;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Grafico {
	private Drawable drawable;
	private double posX, posY;
	private double incX,incY;  //velocidad de desplazamiento
	private int angulo,rotacion; //angulo y velocidad
	private int ancho,alto;

	private double radioCol;
	private int radioColision;
	private View view;
	public static final int MAX_VELOCIDAD=5;
	
	public Grafico(View view, Drawable drawable) {
		this.view=view;
		this.drawable=drawable;
		ancho=drawable.getIntrinsicWidth();
		alto=drawable.getIntrinsicHeight();
		radioCol = Math.sqrt(Math.pow(ancho / 2, 2) + Math.pow(alto / 2, 2));
		radioColision=(int) Math.round(radioCol);
	}
	
	public void dibujaGrafico(Canvas canvas){
		canvas.save();
		int x=(int) (posX+ancho/2);
		int y=(int) (posY+alto/2);
		canvas.rotate((float)angulo, (float)x, (float)y);
		drawable.setBounds((int)posX, (int)posY, (int)posX+ancho, (int)posY+alto);
		drawable.draw(canvas);
		canvas.restore();
		int rInval= (int)Math.hypot(ancho, alto)/2+MAX_VELOCIDAD;
		view.invalidate(x-rInval, y-rInval, x+rInval, y+rInval);
	}
	
	public void incrementaPos(double factor){
		posX=posX+incX+factor;
		//Si salimos de la pantalla correguimos la posicion
		if (posX<-ancho/2){
			posX=view.getWidth()-ancho/2;
		}
		if (posX>view.getWidth()-ancho/2){  
			posX=-ancho/2;
		}
		posY=posY + incY+factor;
		if (posY<-alto/2){ 
			posY=view.getHeight()-alto/2;
		}
		if (posY>view.getHeight()-alto/2){
			posY=-alto/2;
		}
		angulo = (int) (angulo + rotacion*factor); //Actualizamos �ngulo
	}
	
	public double distancia(Grafico g){
		return Math.hypot(posX-g.posY, posY-g.posX);
	}
	
	public boolean verificarColsion(Grafico g){
		double distanciaCuadrada = Math.pow(posX - g.posX, 2) + Math.pow(posY - g.posY, 2);
		double sumaRadiosCuadrada = Math.pow(radioColision + g.radioColision, 2);
		return distanciaCuadrada < sumaRadiosCuadrada;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public double getIncX() {
		return incX;
	}

	public void setIncX(double incX) {
		this.incX = incX;
	}

	public double getIncY() {
		return incY;
	}

	public void setIncY(double incY) {
		this.incY = incY;
	}

	public int getAngulo() {
		return angulo;
	}

	public void setAngulo(int angulo) {
		this.angulo = angulo;
	}

	public int getRotacion() {
		return rotacion;
	}

	public void setRotacion(int rotacion) {
		this.rotacion = rotacion;
	}

	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public int getRadioColision() {
		return radioColision;
	}

	public void setRadioColision(int radioColision) {
		this.radioColision = radioColision;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getMaxVelocidad() {
		return MAX_VELOCIDAD;
	}


}
