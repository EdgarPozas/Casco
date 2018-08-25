package com.mycompany.casco;
import android.bluetooth.*;
import java.io.*;
import java.util.*;
import android.os.*;
import android.widget.*;
import android.telephony.gsm.*;
import android.app.*;

public class Manejo_Bluetooth
{
	private BluetoothSocket bsock;
	private OutputStream outstre;
	private InputStream instre;
	private byte[] readBuffer;
	private int numBytes;
	private TextView txt_acelerometro;
	private TextView txt_direccion;
	private TextView txt_velocidad;
	private TextView txt_estado;
	private ImageButton img;
	public String datos;
	public CountDownTimer timer;
	public Inicio inicio;
	
	public Manejo_Bluetooth(TextView t3,ImageButton im,Inicio ini){
		//txt_acelerometro=t1;
		//txt_direccion=t2;
		txt_estado=t3;
		img=im;
		this.inicio=ini;
		timer=new CountDownTimer(10000,1000){
			public void onTick(long milis){
				img.setBackground(inicio.getResources().getDrawable(R.drawable.sueloresbaladizo96));
				
				inicio.alertalanzada=true;
				txt_estado.setText("Alerta en "+milis/1000);
			}
			public void onFinish(){
				inicio.alertalanzada=false;
				txt_estado.setText("Lanzando alerta");
				inicio.alertar();
			}
		};//.start();
		//timer.cancel();
	}
	
	public boolean conectar(BluetoothDevice device){
		UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		try{
			bsock=device.createRfcommSocketToServiceRecord(uuid);
			bsock.connect();
			outstre=bsock.getOutputStream();
			instre=bsock.getInputStream();
			return true;
			//Toast.makeText(this,"Dispositivo listo",Toast.LENGTH_SHORT).show();
		}catch(Exception ex){
			Toast.makeText(inicio,"Error al conectar",Toast.LENGTH_SHORT).show();
			//ex.printStackTrace();
		}
		return false;
	}
	
	public void esperar()
	{
		/*new Thread(new Runnable()
			{
				public void run()
				{      */          
					readBuffer = new byte[1];
							/*new Handler(Looper.getMainLooper()).post(*/
							new Thread(new Runnable(){
								public void run(){
									while(true){
									try{
										
										numBytes = instre.read(readBuffer);
										String msg=new String(readBuffer,0,numBytes);
										//int endline=msg.indexOf(";");
										//System.out.println("gd");
										if(msg.equals("1")&&!inicio.alertalanzada){
											timer.start();
											inicio.vibrar();
											//System.out.println("aqui");
										}
										//System.out.println("mm");
										Thread.sleep(100);
									} catch (Exception e) {
										
									}
									}
								}
							}).start();
					/*}
			}).start();*/
	}
	

	public boolean enviar(String data) throws IOException
	{
		 outstre.write(data.getBytes());
		 return true;
	}

	public void terminar()
	{
		try{
			outstre.close();
			instre.close();
			bsock.close();
		}catch(Exception ex){
			
		}
	}
}
