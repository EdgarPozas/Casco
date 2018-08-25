package com.mycompany.casco;

import android.app.*;
import android.os.*;
import android.bluetooth.*;
import android.content.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import java.util.*;
import android.widget.AdapterView.*;
import java.lang.reflect.*;
import java.io.*;

public class Seleccion_Dispositivo extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener
{
	private ImageButton btn_actu;
	private ArrayList<Seleccion_Dispositivo_Item> items =new ArrayList<Seleccion_Dispositivo_Item>();
	private Seleccion_Dispositivo_Adapter adapter;
	private ListView listview;
	private BluetoothAdapter adapblue;

    final BroadcastReceiver receiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context p1, Intent p2)
		{
			if(BluetoothDevice.ACTION_FOUND.equals(p2.getAction())){
				BluetoothDevice dev=p2.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			    Seleccion_Dispositivo_Item it=new Seleccion_Dispositivo_Item(dev.getName(),dev.getAddress(),dev);
				for(Seleccion_Dispositivo_Item i : items){
					if(i.mac.equals(it.mac)){
						return;
					}
				}
				items.add(it);
				adapter.notifyDataSetChanged();
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccion_dispositivo);
		System.out.println("-----------------------------------------");
		btn_actu=(ImageButton) findViewById(R.id.btn_actualizar);
		btn_actu.setOnClickListener(this);
		adapter=new Seleccion_Dispositivo_Adapter(this,items);
        listview=(ListView) findViewById(R.id.lista_items);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		adapblue=BluetoothAdapter.getDefaultAdapter();
		if(adapblue==null){
			return;
		}
		if(!adapblue.isEnabled()){
			Intent enblue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enblue,1);
		}else{
			act_list();
		}
    }
	private void act_list(){
		//actualizar();
		items.clear();
		Set<BluetoothDevice> devs=adapblue.getBondedDevices();
		
		if(devs.size()>0){
			for(BluetoothDevice dev: devs){
				Seleccion_Dispositivo_Item it=new Seleccion_Dispositivo_Item(dev.getName(),dev.getAddress(),dev);
				it.nombre+="	PAREADO";
				it.pareado=true;
				items.add(it);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==1){
			if(adapblue.isEnabled()){
				Toast.makeText(this,"Bluetooth encendido",Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this,"Bluetooth no encendido",Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onClick(View p1)
	{
		if(R.id.btn_actualizar==p1.getId()){
			actualizar();
		}
	}
	
	private void actualizar(){
		if(adapblue.isDiscovering()){
			return;
		}
		act_list();
		items.clear();
		adapblue.startDiscovery();
		registerReceiver(receiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		if(items.size()==0)
			return;
		Seleccion_Dispositivo_Item it=items.get(p3);
	
		if(it==null)
			return;
		
		if(it.device==null)
			return;
		
		if(!it.mac.startsWith("98:D3")){
			Toast.makeText(this,"Debe ser un dispositivo compatible",Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(it.pareado){
			Intent intent= new Intent(this,Inicio.class);
			
			//if(man.conectar(it.device))
			//	Toast.makeText(this,"Conectado a dispositivo",Toast.LENGTH_SHORT).show();
			
			//man.esperar();
				
			Bundle b=new Bundle();
			b.putParcelable("device",it.device);
			intent.putExtras(b);
			
			startActivity(intent);
			return;
		}
		
		try{	
		    
		    Class c=Class.forName("android.bluetooth.BluetoothDevice");
            Method m=c.getMethod("createBond");
			Boolean b=(Boolean) m.invoke(it.device);
			if(b){
				Toast.makeText(this,"Tratando de parear",Toast.LENGTH_SHORT).show();
				act_list();
			}else{
				Toast.makeText(this,"Pareado incorrecto",Toast.LENGTH_SHORT).show();
			}
	    }catch(Exception ex){

	    } 
	}
}
