package remotehouseholdcontrol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

public class StatusFragment extends Fragment {
	
	private ConnectionCredentialsManager cm;
	private LinearLayout layout;
	private LayoutInflater inflater;
	private OnDownload onDownload = new OnDownload(this);

  @Override
  public View onCreateView(LayoutInflater i, ViewGroup container,
    Bundle savedInstanceState) {
    View v = i.inflate(R.layout.status_fragment_layout, container, false);
    layout = v.findViewById(R.id.status_layout);
    inflater = i;
    Data.setOnDownload(onDownload);
    return v;
  }

  @Override
  public void onResume() {
    super.onResume();
      cm = new ConnectionCredentialsManager(getActivity());
    Data.setOnDownload(onDownload);
  }

  protected void updateView() {
    View frag = inflater.inflate(R.layout.status_fragment_layout, null);

    SimpleArrayMap<String, String> m = Data.get();
    if (m != null) {


        Button sezameBt = frag.findViewById(R.id.button8);
        sezameBt.setOnClickListener(listenerButt);
     //   ImageButton postACKNBt = frag.findViewById(R.id.imageButtonPostACKN);

		ImageView img_g = frag.findViewById(R.id.garage_View);
		String v = m.get(getString(R.string.env_garaz_vrata));
	    //   Toast.makeText(getActivity(), v, Toast.LENGTH_LONG).show();
        if (v != null) img_g.setImageResource(v.trim().equals(getString(R.string.status_close))
                ? R.drawable.garage_closed_65x65
                : R.drawable.garage_open_65x65);
        else {
            img_g.setImageResource(R.drawable.garage_unknown);
    	}

        v = m.get(getString(R.string.env_house_armed));
        if (v != null) {
            ImageView img = frag.findViewById(R.id.house_armed_View);
            img.setImageResource(v.trim().equals(getString(R.string.status_Armed))
                    ? R.drawable.house_armed_65x65
                    : R.drawable.house_unarmed_65x65);
        }

        ImageView img_h = frag.findViewById(R.id.heating_View);
        v = m.get(getString(R.string.env_kotel));
        if (v != null) {
            //   Toast.makeText(getActivity(), v, Toast.LENGTH_LONG).show();
            img_h.setImageResource(v.trim().equals(getString(R.string.status_off))
                    ? R.drawable.heating_off_65x65
                    : R.drawable.heating_on_65x65);
        } else {
            img_h.setImageResource(R.drawable.boiler_unknown);
        }

        ImageView img_b = frag.findViewById(R.id.boiler_View);
        v = m.get(getString(R.string.env_boiler));
        if (v != null) {
            //   Toast.makeText(getActivity(), v, Toast.LENGTH_LONG).show();
            img_b.setImageResource(v.trim().equals(getString(R.string.status_off))
                    ? R.drawable.boiler_off_65x65
                    : R.drawable.boiler_on_65x65);
        } else {
            img_b.setImageResource(R.drawable.boiler_unknown);
        }
        v = m.get(getString(R.string.env_tarif));
        if (v != null) {
            ImageView img = frag.findViewById(R.id.tarif_View);
            img.setImageResource(v.trim().equals(getString(R.string.status_low))
                    ? R.drawable.tarif_low_65x65
                    : R.drawable.tarif_high_65x65);
        }

        v = m.get(getString(R.string.env_tarif));
        if (v != null) {
            ImageView img = frag.findViewById(R.id.wifi_View);
            img.setImageResource(cm.getWifiHomeStatus()
                    ? R.drawable.wifi_home_65x65
                    : R.drawable.wifi_out_65x65);
        }

        v = m.get(getString(R.string.inf_post));
        if (v != null) {
            ImageButton postACKNBt = frag.findViewById(R.id.imageButtonPostACKN);
            postACKNBt.setOnClickListener(listener);
            if (v.trim().equals(getString(R.string.status_on)))
                postACKNBt.setVisibility(View.VISIBLE);
            else
                postACKNBt.setVisibility(View.INVISIBLE);
        }

        v = m.get(getString(R.string.info_kotel));
        if (v != null) {
            ImageView img = frag.findViewById(R.id.Kotel_Voda_View);
            if (v.trim().equals(getString(R.string.status_High)))
                img.setImageResource(R.drawable.water_level_40x40);
        }

        v = m.get(getString(R.string.temp_out));
        if (v != null) {
           TextView tv_tmp = frag.findViewById(R.id.textTempOut);
            tv_tmp.setText("Out "+v);
        }

        v = m.get(getString(R.string.temp_in));
        if (v != null) {
            TextView tv_tmp = frag.findViewById(R.id.textTempIn);
            tv_tmp.setText("In "+v);
        }

        v = m.get("date");
        if (v != null) {
            TextView tv_tmp = frag.findViewById(R.id.textView_LastUpdate);
            tv_tmp.setText("Last update: "+v);
        }

        v = m.get(getString(R.string.day_light));
        int i = v.length();
        String stttt = v.substring(0,v.length()-2);
        int day_ligh = Integer.parseInt(v.substring(0,v.length()-2));
        if (v != null) {
            ImageView img = frag.findViewById(R.id.dayLightView);
            if (day_ligh > 30000) {
                img.setImageResource(R.drawable.weather_sunny);
            } else if (day_ligh > 15000) {
                img.setImageResource(R.drawable.weather_cloudy_sun);
            } else if (day_ligh > 200) {
                img.setImageResource(R.drawable.weather_cloudy);
            } else {
                img.setImageResource(R.drawable.weather_night);
            }
        }
    }
    
    layout.removeAllViewsInLayout();
    layout.addView(frag);
  }

    Button.OnClickListener listenerButt = new Button.OnClickListener()
    {

        @Override
        public void onClick(View v) {
            // Build an AlertDialog
            cm = new ConnectionCredentialsManager(getActivity());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Kontrolní otázka Kefalín:");
            builder.setMessage("Fakt chceš otevřít?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AsyncTask< Integer, Void, Boolean> runPulseTask = new AsyncTask< Integer, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Integer ... params) {
                            Integer device = params[0];
                            CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
                            try {
                                return scriptCaller.callCGIScriptPulse(device);
                            } catch (IOException e) {
                                return false;
                            }
                        }
                    };
                    boolean result;
                    try {
                        result = runPulseTask.execute(getResources().getInteger(R.integer.rele_sezame)).get();
                        if (!result) {
                            Toast.makeText(getActivity(), "onClick CgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) { //collect all possible exceptions
                        Toast.makeText(getActivity(),"Exception PulseTask", Toast.LENGTH_LONG).show();
                        result = false;
                    }
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }



/*        @Override

        public void onClick(View v) {
            cm = new ConnectionCredentialsManager(getActivity());


            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Alert message to be shown");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();


            AsyncTask< Integer, Void, Boolean> runPulseTask = new AsyncTask< Integer, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Integer ... params) {
                    Integer device = params[0];
                    CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
                    try {
                        scriptCaller.callCGIScriptPulse(getResources().getInteger(R.integer.rele_vrata));
                        return scriptCaller.callCGIScriptPulse(getResources().getInteger(R.integer.rele_garaz));
                    } catch (IOException e) {
                        return false;
                    }
                }
            };
            boolean result;
            try {
                result = runPulseTask.execute(0).get();
                if (!result) {
                    Toast.makeText(getActivity(), "onClick CgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) { //collect all possible exceptions
                Toast.makeText(getActivity(),"Exception PulseTask", Toast.LENGTH_LONG).show();
                result = false;
            }
        }
  */  };

    ImageButton.OnClickListener listener = new ImageButton.OnClickListener()
	{

	    @Override
		public void onClick(View v) {
	        ImageButton postACKNBt = v.findViewById(R.id.imageButtonPostACKN);
            postACKNBt.setVisibility(View.INVISIBLE);

            cm = new ConnectionCredentialsManager(getActivity());
            AsyncTask< Integer, Void, Boolean> runPostACKNTask = new AsyncTask< Integer, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Integer ... params) {
					Integer device = params[0];
					CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
					try {
						return scriptCaller.callCGIScriptAndPostACKN(device);
					} catch (IOException e) {
											return false;
											}
				}
			};
			boolean result;
			try {
				result = runPostACKNTask.execute(0).get();
				if (!result) {   
	   				Toast.makeText(getActivity(), "onClick CgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
	   			}
 			} catch (Exception e) { //collect all possible exceptions
				Toast.makeText(getActivity(),"Exception PostACKNTask", Toast.LENGTH_LONG).show();					
				result = false;
			}
			}		
	};  
  
  private static class OnDownload extends Handler {
    private final WeakReference<StatusFragment> frag;

    private OnDownload(StatusFragment frag) {
      this.frag = new WeakReference<StatusFragment>(frag);
    }

    @Override
    public void handleMessage(Message msg) {
      if (msg.what == Data.MSG_DOWNLOADED) {
        StatusFragment f = frag.get();
        if (f != null)
          f.updateView();
      }
    }
  }
}