package remotehouseholdcontrol;

import java.io.IOException;

import remotehouseholdcontrol.nettools.CgiScriptCaller;
import remotehouseholdcontrol.nettools.ConnectionCredentialsManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class ZaluzieFragment extends Fragment {

	private ConnectionCredentialsManager cm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,

							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.zaluzie_fragment_layout, container, false);

		ImageButton btup1= v.findViewById(R.id.imageButtonup1);
		btup1.setOnClickListener(listener);

		ImageButton btdw1= v.findViewById(R.id.imageButtondown1);
		btdw1.setOnClickListener(listener);

		ImageButton btup2= v.findViewById(R.id.imageButtonup2);
		btup2.setOnClickListener(listener);

		ImageButton btdw2= v.findViewById(R.id.imageButtondown2);
		btdw2.setOnClickListener(listener);

		ImageButton btup3= v.findViewById(R.id.imageButtonup3);
		btup3.setOnClickListener(listener);

		ImageButton btdw3= v.findViewById(R.id.imageButtondown3);
		btdw3.setOnClickListener(listener);

		ImageButton btup4= v.findViewById(R.id.imageButtonup4);
		btup4.setOnClickListener(listener);

		ImageButton btdw4= v.findViewById(R.id.imageButtondown4);
		btdw4.setOnClickListener(listener);

		ImageButton btup5= v.findViewById(R.id.imageButtonup5);
		btup5.setOnClickListener(listener);

		ImageButton btdw5= v.findViewById(R.id.imageButtondown5);
		btdw5.setOnClickListener(listener);

		ImageButton btup6= v.findViewById(R.id.imageButtonup6);
		btup6.setOnClickListener(listener);

		ImageButton btdw6= v.findViewById(R.id.imageButtondown6);
		btdw6.setOnClickListener(listener);

		ImageButton btup7= v.findViewById(R.id.imageButtonup7);
		btup7.setOnClickListener(listener);

        ImageButton btdw7= v.findViewById(R.id.imageButtondown7);
        btdw7.setOnClickListener(listener);

        ImageButton btstop= v.findViewById(R.id.imageButtonStop);
        btstop.setOnClickListener(listener);

        CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);

		return v;
	}

	ImageButton.OnClickListener listener = new ImageButton.OnClickListener()
	{
		@Override

		public void onClick(View v) {
			cm = new ConnectionCredentialsManager(getActivity());
            int rele_id = 0, action = 0;
			switch (v.getId()) {
                case  R.id.imageButtondown1: 	{rele_id = 7; action = 0; 	break;} 	//	Obyvak
                case  R.id.imageButtonup1: 	    {rele_id = 7; action = 1; 	break;}
                case  R.id.imageButtondown2: 	{rele_id = 2; action = 0; 	break;}		//	Kuchyne
                case  R.id.imageButtonup2: 	    {rele_id = 2; action = 1; 	break;}
                case  R.id.imageButtondown3: 	{rele_id = 4; action = 0; 	break;}		//	Pracovna
                case  R.id.imageButtonup3: 	    {rele_id = 4; action = 1; 	break;}
                case  R.id.imageButtondown4: 	{rele_id = 3; action = 0; 	break;}		//	Tyna
                case  R.id.imageButtonup4: 	    {rele_id = 3; action = 1; 	break;}
                case  R.id.imageButtondown5: 	{rele_id = 5; action = 0; 	break;}		// Chodba
                case  R.id.imageButtonup5: 	    {rele_id = 5; action = 1; 	break;}
                case  R.id.imageButtondown6: 	{rele_id = 6; action = 0; 	break;}		// Katka
                case  R.id.imageButtonup6: 	    {rele_id = 6; action = 1; 	break;}
                case  R.id.imageButtondown7: 	{rele_id = 1; action = 0; 	break;}		// Loznice
                case  R.id.imageButtonup7: 	    {rele_id = 1; action = 1; 	break;}
                case  R.id.imageButtonStop: 	{rele_id = 0; action = 2; 	break;}
			}

			AsyncTask< Integer, Void, Boolean> runUpDownTask = new AsyncTask< Integer, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Integer ... params) {
					Integer device = params[0];
                    Integer act = params[1];
					CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
					try {
						return scriptCaller.callCGIScriptAndSetUpDown(act,device);
					} catch (IOException e) {
						return false;
					}
				}
			};
			boolean result;
			try {
                result = runUpDownTask.execute(rele_id,action).get();
				if (!result) {
					Toast.makeText(getActivity(), "onClick CgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) { //collect all possible exceptions
				Toast.makeText(getActivity(),"Exception UpDownTask", Toast.LENGTH_LONG).show();
				result = false;
			}
		}
	};
}