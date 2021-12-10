package remotehouseholdcontrol;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.IOException;
import zbynek.remotehouseholdcontrol.nettools.CgiScriptCaller;
import zbynek.remotehouseholdcontrol.nettools.ConnectionCredentialsManager;

public class ControlFragment extends Fragment {
	private SeekBar seekBar_timeout;
	private TextView textView_timeout;
	private ConnectionCredentialsManager cm;
	public int time_out_sprinkler=1;
	public Switch sprinklerSw;


	ImageButton.OnClickListener listener = new ImageButton.OnClickListener()
	{
		@Override
		public void onClick(View v) {
      	cm = new ConnectionCredentialsManager(getActivity());
		    int rele_id=0;
			int time_out=1;

			switch (v.getId()) {
			case  R.id.imageButtonVrata: 		{rele_id = getResources().getInteger(R.integer.rele_vrata); 									break;}
			case  R.id.imageButtonBranka: 		{rele_id = getResources().getInteger(R.integer.rele_branka); time_out=2;						break;}
			case  R.id.imageButtonGaraz: 		{rele_id = getResources().getInteger(R.integer.rele_garaz); 									break;}
			case  R.id.imageButtonSprinkler: 	{rele_id = getResources().getInteger(R.integer.rele_sprinkler); time_out=time_out_sprinkler*60;
				sprinklerSw.setChecked(true);
			break;}  // timed pulse
			}

			AsyncTask< Integer, Void, Boolean> runPulseTask = new AsyncTask< Integer, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Integer ... params) {
					int time_out_i  = params[0];
					int device_i 	= params[1];
					CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
					try { return scriptCaller.callCGIScriptPulseTimed(time_out_i,device_i);
					} catch (IOException e) {
											return false;
											}
				}
			};

			boolean result;
			try {
			//	Toast.makeText(getActivity(), "rele_id: " + rele_id, Toast.LENGTH_LONG).show();
				result = runPulseTask.execute(time_out, rele_id).get();
				if (!result) {
					Toast.makeText(getActivity(), "onClick CgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
	   			}
   			} catch (Exception e) { //collect all possible exceptions
				Toast.makeText(getActivity(),"Exception PulseTask", Toast.LENGTH_LONG).show();
				result = false;
			}
			}
	};

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton v, final boolean isChecked) {
			AsyncTask<Integer, Void, Boolean> t = new AsyncTask<Integer, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Integer... params) {
					Integer device = params[0];
					CgiScriptCaller sc = new CgiScriptCaller(
							new ConnectionCredentialsManager(getActivity().getApplicationContext()));
					try { return sc.callCGIScriptAndSetValue(isChecked, device);
					} catch (IOException e) {
            		return false;
					}
				}

				@Override
				protected void onPostExecute(Boolean res) {
					//   Toast.makeText(getActivity(), "Switch CgiScriptCaller result: " + res,Toast.LENGTH_LONG).show();
				}
			};

			int rele_id = 0;
			switch (v.getId()) {
				case R.id.sprinklerswitch: {
					rele_id = getResources().getInteger(R.integer.rele_sprinkler);
					break;
				}
		/*		case R.id.lightswitch: {
					rele_id = getResources().getInteger(R.integer.rele_light);
					break;
				}
		*/		case R.id.heatingswitch: {
						rele_id = 0;
						break;
				}
			}
			//	Toast.makeText(getActivity(),"Switch rele je "+rele_id, Toast.LENGTH_LONG).show();
			t.execute(rele_id);
		}
	};

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.control_fragment_layout, container, false);

		ImageButton garageBt = v.findViewById(R.id.imageButtonVrata);
    	    garageBt.setOnClickListener(listener);

		ImageButton brankaBt = v.findViewById(R.id.imageButtonBranka);
    	    brankaBt.setOnClickListener(listener);

		ImageButton garazBt = v.findViewById(R.id.imageButtonGaraz);
    	    garazBt.setOnClickListener(listener);

		ImageButton sprinklerBt = v.findViewById(R.id.imageButtonSprinkler);
		sprinklerBt.setOnClickListener(listener);

	//	final int[] progress = {50};

		textView_timeout = v.findViewById(R.id.textView_timeout);
		textView_timeout.setText("Timeout: 1 min");

		seekBar_timeout = v.findViewById(R.id.seekBar_timeout);
		seekBar_timeout.setMin(1);
		seekBar_timeout.setMax(30);
		seekBar_timeout.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
		//		progress[0] = progresValue;
				textView_timeout.setText("Timeout: " + progresValue + " min" );
				//Toast.makeText(getActivity(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				time_out_sprinkler =seekBar_timeout.getProgress();
				textView_timeout.setText("Timeout: " + time_out_sprinkler + " min");
			//	Toast.makeText(getActivity(), "Stopped tracking seekbar" + time_out_sprinkler, Toast.LENGTH_SHORT).show();
			}
		});

		CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
		//        Toast.makeText(getActivity(), scriptCaller.GetRelayMessage(rele_id), Toast.LENGTH_LONG).show();

		sprinklerSw = v.findViewById(R.id.sprinklerswitch);
		sprinklerSw.setOnCheckedChangeListener(changeListener);
		int rele_id = getResources().getInteger(R.integer.rele_sprinkler);
		sprinklerSw.setChecked(scriptCaller.GetRelayStatus(rele_id));
/*
		Switch lightSw = v.findViewById(R.id.lightswitch);
		lightSw.setOnCheckedChangeListener(changeListener);
		rele_id = getResources().getInteger(R.integer.rele_light);
		lightSw.setChecked(scriptCaller.GetRelayStatus(rele_id));
*/
		Switch heatingSw = v.findViewById(R.id.heatingswitch);
		heatingSw.setOnCheckedChangeListener(changeListener);
		heatingSw.setChecked(scriptCaller.GetHeatingStatus());

    	    return v;
		    }
}