package com.mycompany.casco;

import android.app.*;
import android.os.*;
import android.bluetooth.*;
import android.content.*;
import android.widget.*;
import java.util.*;
import android.view.*;
import android.view.View.*;
import java.io.*;
import android.telephony.gsm.*;
import android.net.*;
import android.location.*;
import android.renderscript.*;
import android.app.AlertDialog.*;
import java.text.*;

public class Inicio extends Activity implements View.OnClickListener
{
	
	private String nombre_archivo="contacta.txt";
	private ImageButton btn_agregar;
	public ArrayList<Inicio_Contacto_Item> items;
	private Inicio_Contacto_Adapter adapter;
	private ListView listview;
	private AlertDialog dialog,dialog2;
	private EditText nom;
	private EditText tel;
	private EditText nom_mio;
	private int id_editar;
	private Button btn_aceptar;
	private TextView txt_estado;
	private TextView txt_usuario;
 	private TextView txt_actual;
	private TextView txt_distancia;
	private TextView txt_inicio;
	private TextView txt_iniciar;
	private float distancia;
	private Location locant;
	private double latitud,longitud;
	public Boolean alertalanzada,nogps;
	private boolean iniciado;
	private ImageButton btn_fin;
	private Vibrator vibrator;
	
	
	private final LocationListener loclis = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			nogps=false;
			//System.out.println(location);
			latitud=location.getLatitude();
			longitud=location.getLongitude();
			if(!iniciado)
				return;
			
			/*double disty=0;
			if(latitud!=0)
				disty=location.getLatitude()-latitud;
				
			double distx=0;
			if(longitud!=0)
				distx=location.getLongitude()-longitud;*/
			
			//System.out.println("ffbshd");
			if(txt_actual!=null)
				txt_actual.setText("("+new DecimalFormat("###.####").format( latitud)+","+new DecimalFormat("###.####").format(longitud)+")");
			
			if(locant!=null){
				distancia=(locant.distanceTo(location));
				//distancia=Math.abs(getdistancia(longitud,latitud,locant.getLongitude(),locant.getLatitude()));
				//System.out.println(dista);
				//if(dista<1){
				//	distancia=dista;
				//}
			}else{
				//System.out.println(txt_inicio);
				if(txt_inicio!=null)
					txt_inicio.setText("("+new DecimalFormat("###.####").format( latitud)+","+new DecimalFormat("###.####").format(longitud)+")");
				locant=location;
				//System.out.println("aqui");
			}
			if(txt_distancia!=null)
				txt_distancia.setText(new DecimalFormat("###.####").format( distancia)+" m");
			
		}

		@Override
		public void onStatusChanged(String p1, int p2, Bundle p3)
		{
			// TODO: Implement this method
		}

		@Override
		public void onProviderDisabled(String p1)
		{
			if(txt_actual!=null)
				txt_actual.setText("Active GPS");
			if(txt_inicio!=null)
				txt_inicio.setText("Active GPS");
			if(txt_distancia!=null)
				txt_distancia.setText("Active GPS");
			//nogps=true;
		}

		@Override
		public void onProviderEnabled(String p1)
		{
			// TODO: Implement this method
		}
		private double getdistancia(double lon1,double lat1,double lon2,double lat2){
			
			double R = 6378.137; // Radius of earth in KM
			double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
			double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
			double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
				Math.sin(dLon/2) * Math.sin(dLon/2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			double d = R * c;
			return d * 1000; // meters
		}
	};
	private LocationManager locman;
	private Manejo_Bluetooth man;
	
	public int contadoralerta;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);
		alertalanzada=false;
		contadoralerta=0;
		iniciado=false;
		locant=null;
		
		btn_agregar=(ImageButton) findViewById(R.id.btn_agregar);
		btn_agregar.setOnClickListener(this);
		listview=(ListView) findViewById(R.id.lista_contactos);
		
		items =new ArrayList<Inicio_Contacto_Item>();
		//System.out.println("------------------------------------------");
		adapter=new Inicio_Contacto_Adapter(this,items);
		listview.setAdapter(adapter);
		
		items.clear();
		act_contactos();
		adapter.notifyDataSetChanged();
		
		AlertDialog.Builder albu=new AlertDialog.Builder(this);
		View v=getLayoutInflater().inflate(R.layout.inicio_contacto_registrar, null);
		nom = (EditText)v.findViewById(R.id.txt_agregar_nombre);
		tel=(EditText) v.findViewById(R.id.txt_agregar_telefono);
		Button cancel=(Button) v.findViewById(R.id.btn_agregar_cancel);
		cancel.setOnClickListener(this);
		

		btn_aceptar=(Button) v.findViewById(R.id.btn_agregar_agregar);
		btn_aceptar.setOnClickListener(this);
		albu.setView(v);

		dialog=albu.create();
		
		AlertDialog.Builder albu2=new AlertDialog.Builder(this);
		View v_=getLayoutInflater().inflate(R.layout.inicio_poner_nombre,null);
		nom_mio=(EditText)v_.findViewById(R.id.txt_nombre_nombre);
		Button cancel2=(Button) v_.findViewById(R.id.btn_nombre_cancel);
		cancel2.setOnClickListener(this);

		Button btn_aceptar=(Button) v_.findViewById(R.id.btn_nombre_agregar);
		btn_aceptar.setOnClickListener(this);
		albu2.setView(v_);

		dialog2=albu2.create();
		
		id_editar=-1;
		
		locman=(LocationManager) getSystemService(LOCATION_SERVICE);
		locman.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,loclis);
		
		/*ViewGroup vim1=(ViewGroup) findViewById(R.id.it_1);
		ImageView im1=(ImageView)(vim1.getChildAt(0));
		im1.setImageDrawable(getResources().getDrawable(R.drawable.velocidad48));
		ViewGroup v1=((ViewGroup)((ViewGroup) findViewById(R.id.it_1)).getChildAt(1));
		TextView txt_nom_it_1=(TextView)(v1.getChildAt(0));
		txt_nom_it_1.setText("Velocidad");
	    txt_velocidad=(TextView)(v1.getChildAt(1));
		txt_velocidad.setText("0");
		
		ViewGroup vim2=(ViewGroup) findViewById(R.id.it_2);
		ImageView im2=(ImageView)(vim2.getChildAt(0));
		im2.setImageDrawable(getResources().getDrawable(R.drawable.avancerapido48));
		ViewGroup v2=((ViewGroup)((ViewGroup) findViewById(R.id.it_2)).getChildAt(1));
		TextView txt_nom_it_2=(TextView)(v2.getChildAt(0));
		txt_nom_it_2.setText("Aceleración");
	    txt_aceleracion=(TextView)(v2.getChildAt(1));
		txt_aceleracion.setText("0");
		
		ViewGroup vim3=(ViewGroup) findViewById(R.id.it_3);
		ImageView im3=(ImageView)(vim3.getChildAt(0));
		im3.setImageDrawable(getResources().getDrawable(R.drawable.geocerca40));
		ViewGroup v3=((ViewGroup)((ViewGroup) findViewById(R.id.it_3)).getChildAt(1));
		TextView txt_nom_it_3=(TextView)(v3.getChildAt(0));
		txt_nom_it_3.setText("Posición");
	    txt_posicion=(TextView)(v3.getChildAt(1));
		txt_posicion.setText("0");
		
		ViewGroup vim4=(ViewGroup) findViewById(R.id.it_4);
		ImageView im4=(ImageView)(vim4.getChildAt(0));
		im4.setImageDrawable(getResources().getDrawable(R.drawable.flechadireccionalhorizontal48));
		ViewGroup v4=((ViewGroup)((ViewGroup) findViewById(R.id.it_4)).getChildAt(1));
		TextView txt_nom_it_4=(TextView)(v4.getChildAt(0));
		txt_nom_it_4.setText("Direccional");
	    txt_direccional=(TextView)(v4.getChildAt(1));
		txt_direccional.setText("0");*/
		
		txt_usuario=(TextView) findViewById(R.id.txt_usuario_nombre);
		txt_usuario.setOnClickListener(this);
		
		btn_fin=(ImageButton) findViewById(R.id.btn_fin_alerta);
		btn_fin.setOnClickListener(this);
		
		txt_estado=(TextView)findViewById(R.id.txt_estado);	
		txt_inicio=(TextView)findViewById(R.id.txt_datos_inicio);
		txt_distancia=(TextView)findViewById(R.id.txt_datos_distancia);
		txt_actual=(TextView)findViewById(R.id.txt_datos_actual);
		txt_iniciar=(TextView)findViewById(R.id.txt_iniciar);
		txt_iniciar.setOnClickListener(this);
		
		man=new Manejo_Bluetooth(txt_estado,btn_fin,this);
		
		BluetoothDevice device=getIntent().getExtras().getParcelable("device");
		if(device!=null){
			if(man.conectar(device)){
				Toast.makeText(this,"Conectado a dispositivo",Toast.LENGTH_SHORT).show();
				man.esperar();
			}
		}
		vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
	
	public void alertar(){
		alertalanzada=true;
		for(Inicio_Contacto_Item it : items){
			SmsManager smsm=SmsManager.getDefault();
			smsm.sendTextMessage(it.telefono,null,"Alerta soy "+txt_usuario.getText()+" y me encuentro en : https://www.google.com/maps/@?api=1&map_action=map&center="+latitud+","+longitud,null,null);

			Toast.makeText(this,"Alerta enviada",Toast.LENGTH_SHORT).show();
		}
	}
	
	public void vibrar(){
		// Vibrate for 500 milliseconds
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10000,VibrationEffect.DEFAULT_AMPLITUDE));
		}else{
			//deprecated in API 26 
            vibrator.vibrate(10000);
		}
	}
	
	public void act_contactos(){
		items.clear();
		try {
			InputStream inputStream = this.openFileInput(nombre_archivo);
			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				while ( (receiveString = bufferedReader.readLine()) != null ) {
					String[] cad=receiveString.split(";");
					Inicio_Contacto_Item it=new Inicio_Contacto_Item(cad[0],cad[1]);
					items.add(it);
				}
				inputStream.close();
			}
		}
		catch (FileNotFoundException e) {
			//Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			//Log.e("login activity", "Can not read file: " + e.toString());
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View p1){
		
		switch(p1.getId()){
			case R.id.txt_iniciar:
				if(nogps)
					return;
				iniciado=!iniciado;
				txt_iniciar.setText(iniciado?"Detener":"Iniciar");
				if(!iniciado){
					locant=null;
				}
				//vibrar();
				break;
			case R.id.btn_fin_alerta:
				vibrator.cancel();
				if(!alertalanzada)
					return;
				if(contadoralerta<=0){
					
					btn_fin.setBackground(getResources().getDrawable(R.drawable.casco80));
					
					contadoralerta=0;
					alertalanzada=false;
					man.timer.cancel();
					//man.esperar();
					txt_estado.setText("Normal");
					Toast.makeText(this,"Alerta cancelada",Toast.LENGTH_SHORT).show();
					return;
				}
				//man.timer.start();
				Toast.makeText(this,"Presiona "+contadoralerta+" para terminar alerta",10).show();
				contadoralerta--;
				break;
			case R.id.txt_usuario_nombre:
				dialog2.show();
				break;
			case R.id.btn_nombre_cancel:
				dialog2.hide();
				break;
			case R.id.btn_nombre_agregar:
				txt_usuario.setText(nom_mio.getText().toString());
				dialog2.hide();
				break;
			case R.id.btn_agregar:
				btn_aceptar.setText("Agregar");
				id_editar=-1;
				dialog.show();
				break;
				
	   		case R.id.btn_agregar_cancel:
				dialog.cancel();
				break;
		 	case R.id.btn_agregar_agregar:
				
				if(nom.getText().toString().isEmpty() || tel.getText().toString().isEmpty()){
					Toast.makeText(this,"Completa los campos",Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(id_editar==-1){
					Inicio_Contacto_Item it2=new Inicio_Contacto_Item(nom.getText().toString(),tel.getText().toString());
					items.add(it2);
				}else{
					Inicio_Contacto_Item it2=items.get(id_editar);
					it2.nombre=nom.getText().toString();
					it2.telefono=tel.getText().toString();
				}
				
				escribir();
				act_contactos();
				dialog.dismiss();
				break;
					
		}
	}
    private void escribir(){
		try{
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(nombre_archivo, Context.MODE_PRIVATE));
			String data="";
			for(Inicio_Contacto_Item it: items){
				data+=it.nombre+";"+it.telefono+"\n";
			}
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		}catch (IOException e) {
			//Log.e("Exception", "File write failed: " + e.toString());
		}
		tel.setText("");
		nom.setText("");
	}
	public void eliminar_contacto(View v){
		
		int id=Integer.parseInt(v.getTag().toString());
		
		items.remove(id);
		escribir();
		act_contactos();
		Toast.makeText(this,"Contacto eliminado",Toast.LENGTH_SHORT).show();
		adapter.notifyDataSetChanged();
	}
	public void editar_contacto(View v){
		int id=Integer.parseInt(v.getTag().toString());
		id_editar=id;
		Inicio_Contacto_Item it=items.get(id);

		nom.setText(it.nombre);
		tel.setText(it.telefono);
		btn_aceptar.setText("Actualizar");
		
		dialog.show();
		
		adapter.notifyDataSetChanged();
	}
	public void alertar_contacto(View v){
		String telefono="";
		if(v!=null){
			int id=Integer.parseInt(v.getTag().toString());
			Inicio_Contacto_Item it=items.get(id);
			telefono=it.telefono;
		}
		SmsManager smsm=SmsManager.getDefault();
		smsm.sendTextMessage(telefono,null,"Alerta me encuentro en : https://www.google.com/maps/@?api=1&map_action=map&center="+latitud+","+longitud,null,null);

		Toast.makeText(this,"Alerta enviada",Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop()
	{
		//man.terminar();
		super.onStop();
	}
	
	
	
	
	
}
