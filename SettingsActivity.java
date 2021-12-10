package remotehouseholdcontrol;

import remotehouseholdcontrol.nettools.ConnectionCredentialsManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	private EditText domain;
	private EditText port;
	private EditText domain_local;
	private EditText port_local;
	private EditText home_WIFI;
	private EditText dir;
	private EditText username;
	private EditText password;
	private Switch AutoconfigButton;
	private ConnectionCredentialsManager credManager;

	 private SeekBar seekBar;
	 private TextView textView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initializeVariables();
		domain 				= findViewById(R.id.editTextDomain);
		port 				= findViewById(R.id.editTextPort);
		domain_local		= findViewById(R.id.editTextDomain_local);
		port_local			= findViewById(R.id.editTextPort_local);
		home_WIFI			= findViewById(R.id.editTextWIFI_SSID);
		dir 				= findViewById(R.id.editTextDir);
		username 			= findViewById(R.id.editTextUsername);
		password 			= findViewById(R.id.editTextPassword);
		AutoconfigButton 	= findViewById(R.id.switchAutoconfig);
		credManager = new ConnectionCredentialsManager(this);

		// Initialize the textview with '0'.
		  textView.setText("Covered: " + seekBar.getProgress() + "/" + seekBar.getMax());
		  
		  seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			  int progress = 50;
			  
			  @Override
			  public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				  progress = progresValue;
	//			  Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
			  }
			
			  @Override
			  public void onStartTrackingTouch(SeekBar seekBar) {
		//		  Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
			  }
			
			  @Override
			  public void onStopTrackingTouch(SeekBar seekBar) {
				  textView.setText("Covered: " + progress + "/" + seekBar.getMax());
				  Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
			  }
		   });
	}
	
	// A private method to help us initialize our variables.
		 private void initializeVariables() {
			 seekBar = findViewById(R.id.seekBar1);
			 textView = findViewById(R.id.textView11);
		 }	 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		domain.setText(credManager.getDomain());
		port.setText(credManager.getPort());
		domain_local.setText(credManager.getDomain_local());
		port_local.setText(credManager.getPort_local());
		home_WIFI.setText(credManager.getHomeWifi());
		dir.setText(credManager.getDir());
		username.setText(credManager.getUsername());
		AutoconfigButton.setChecked(credManager.getAutoconfig()); 
		password.setText(credManager.getPassword());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		credManager.saveCredentials(
				domain.getText().toString(),
				port.getText().toString(),
				domain_local.getText().toString(),
				port_local.getText().toString(),
				home_WIFI.getText().toString(),
				dir.getText().toString(),
				username.getText().toString(), 
				password.getText().toString(),
				AutoconfigButton.isChecked());
	}
}