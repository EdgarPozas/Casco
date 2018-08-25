package com.mycompany.casco;
import android.bluetooth.*;

public class Seleccion_Dispositivo_Item
{
	public String nombre,mac;
	public BluetoothDevice device;
	public Boolean pareado;
	public Seleccion_Dispositivo_Item(String nombre,String mac,BluetoothDevice device){
		super();
		this.nombre=nombre;
		this.mac=mac;
		this.device=device;
	}
}
