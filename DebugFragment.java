package remotehouseholdcontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import zbynek.remotehouseholdcontrol.nettools.ConnectionCredentialsManager;


public class DebugFragment extends Fragment {
	
	private TextView textView; 
	private ConnectionCredentialsManager cm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		textView = new TextView(getActivity());
	//	textView.setGravity(Gravity.CENTER);
		return textView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	      cm = new ConnectionCredentialsManager(getActivity());
	      
		StringBuilder sb = new StringBuilder();
		
		sb.append("Wifi status : "); sb.append(cm.getWifiName()); sb.append("\n");
		sb.append("URL : "); sb.append(cm.constructUrl("")); sb.append("\n"); 
		sb.append("result : "); sb.append(cm.constructUrlres("")); sb.append("\n");

		textView.setText(sb.toString());
		
	}


}