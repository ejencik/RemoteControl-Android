package remotehouseholdcontrol;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.widget.LinearLayout;
import java.io.IOException;
import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class SaunaFragment extends Fragment {
	private ConnectionCredentialsManager cm;
	View 			mChart;
	Switch 			switch_HDO;
	ToggleButton 	saunaBt;
	TextView 		tv_tmp;
	TextView 		tv_sts;
	TextView 		tv_safety;


	@Override
	public 	View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sauna_fragment_layout, container, false);
		cm = new ConnectionCredentialsManager(getActivity());

		int sensor_id = getResources().getInteger(R.integer.sensor_Sauna_temp);  // nacteni dat pro graf teploty v saune
		AsyncTask< Integer, Void, String> getGraphDataTask = new AsyncTask< Integer, Void, String>() {
			@Override
			protected String doInBackground( Integer ... params) {
				Integer device = params[0];
				CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
				try {
					return scriptCaller.callCGIScriptAndGetGraphData(device);
				} catch (IOException e) {return "";
				}
			}
		};
		String graph_values;
		try {
			graph_values = getGraphDataTask.execute(sensor_id).get();
			if (graph_values !="") {
				//			Toast.makeText(getActivity(), "GraphCgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
				openChart(v,graph_values);
			}

		} catch (Exception e) { //collect all possible exceptions
			Toast.makeText(getActivity(),"Exception GraphTask"+e, Toast.LENGTH_LONG).show();
		}


// initial Button and HDO settings
		SimpleArrayMap<String, String> m = Data.get();
		boolean sauna_status = !(m.get(getString(R.string.sauna_status))).equals(getString(R.string.status_idle));
		saunaBt = v.findViewById(R.id.toggleButtonSauna);
		saunaBt.setOnClickListener(listener);
		saunaBt.setChecked(sauna_status);

		boolean HDO_status = (m.get(getString(R.string.sauna_HDO_used))).equals(getString(R.string.status_on));
		switch_HDO		= (Switch) v.findViewById(R.id.switch_HDO);
		switch_HDO.setChecked(HDO_status);
		switch_HDO.setClickable(!sauna_status);			// disable change HDO settings when sauna is on


// show temperature & status
		if (m != null) {
			int Display_colour = sauna_status?Color.parseColor("#f22436")	:Color.parseColor("#68b466");
			String val = m.get(getString(R.string.sauna_temp));
			if (val != null) {
				tv_tmp = v.findViewById(R.id.textSaunaTemperature);
				tv_tmp.setTextColor(Display_colour);
				tv_tmp.setText(val);
			}

			val = m.get(getString(R.string.sauna_status_text));
			if (val != null) {
				tv_sts = v.findViewById(R.id.textSaunaStatusText);
				tv_sts.setTextColor(Display_colour);
				tv_sts.setText(val);
			}

			val = m.get(getString(R.string.sauna_safety));
			if (val != null) {
				tv_safety = v.findViewById(R.id.textSaunaSafety);
				tv_safety.setText(val+" min");
				tv_safety.setVisibility(sauna_status?View.VISIBLE:View.GONE);

			}
		}

		LinearLayout ll = v.findViewById(R.id.my_tarif_graf);
		int tarif_width=480;
		//     tarif_width=ll.getWidth();
		float tarif_part=tarif_width/23;

		Paint paint = new Paint();
		Bitmap bg = Bitmap.createBitmap(tarif_width , 150, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bg);

		int belt_y1	=25;
		int belt_y2	=55;

		paint.setColor(Color.parseColor("#99FFCC"));
		canvas.drawRect(0, 0, tarif_width,100, paint);

		paint.setColor(Color.parseColor("#21283D"));
		canvas.drawRect(0, belt_y1, tarif_width,belt_y2, paint);

// draw HDO intervals
		if (m != null) {
			String string_HDO = m.get(getString(R.string.HDO_text));
			if (string_HDO != null) {
				for (String interval_HDO : string_HDO.split("#")) {
					String[] time_HDO 	= interval_HDO.split("-");
					Float begin_HDO 	= Float.parseFloat(time_HDO[0].trim()) * tarif_part;
					Float end_HDO 		= Float.parseFloat(time_HDO[1].trim()) * tarif_part;
					paint.setColor(Color.parseColor("#cceae7"));
					canvas.drawRect(begin_HDO, belt_y1, end_HDO, belt_y2, paint);
				}
			}
		}

// draw actual time
		Calendar cal = Calendar.getInstance();
		float minute = cal.get(Calendar.MINUTE);
		float hourofday = cal.get(Calendar.HOUR_OF_DAY);
		float xpos= ((hourofday) + minute/60 )*tarif_part ;
		paint.setColor(Color.parseColor("#ff0000"));
		canvas.drawRect(xpos-1, belt_y1-3, xpos+1,belt_y2+3, paint);

// draw hours labels
		for (int i=1; i < 24; i++) {
			paint.setColor(Color.parseColor("#FFFFF0"));
			canvas.drawCircle((tarif_part)*i, belt_y1+12, 3, paint);
			String formatted = String.format("%02d", i);
			paint.setColor(Color.parseColor("#751947"));
			canvas.drawText(formatted  ,(tarif_part)*i-7, belt_y1+50, paint);
		}

		ll.setBackground(new BitmapDrawable(getResources(), bg));
		return v;
	}

	OnClickListener listener = new OnClickListener()
	{		@Override
	public void onClick(View v_listener) {
		cm = new ConnectionCredentialsManager(getActivity());
		Boolean isChecked = saunaBt.isChecked();
		Boolean isCheckedHDO = switch_HDO.isChecked();
		int Display_colour = isChecked?Color.parseColor("#f22436")	:Color.parseColor("#68b466");
		tv_tmp.setTextColor(Display_colour);
		tv_sts.setTextColor(Display_colour);
		switch_HDO.setClickable(!isChecked);		// disable change HDO settings when sauna is on

		runAsyncSaunaTask(isChecked, isCheckedHDO);

		SimpleArrayMap<String, String> m = Data.get();
		String val = m.get(getString(R.string.sauna_temp));
		if (val != null) {
			//tv_tmp = v.findViewById(R.id.textSaunaTemperature);
			tv_tmp.setTextColor(Display_colour);
			tv_tmp.setText(val);
		}

		val = m.get(getString(R.string.sauna_status_text));
		if (val != null) {
			tv_sts.setTextColor(Display_colour);
			tv_sts.setText(val);
		}

		val = m.get(getString(R.string.sauna_safety));
		if (val != null) {
		//	tv_safety.setTextColor(Display_colour);
			tv_safety.setText(val+" min");
			tv_safety.setVisibility(isChecked?View.VISIBLE:View.GONE);
		}
	}
	};

	private void runAsyncSaunaTask(Boolean isChecked, Boolean isChecked_HDO) {
		AsyncTask< Boolean, Void, Boolean> setSwitchStatusTask = new AsyncTask< Boolean,  Void, Boolean>() {

			@Override
			protected Boolean doInBackground( Boolean ... params) {
				Boolean isChecked = params[0];
				Boolean isChecked_HDO = params[1];

				CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
				try {
//					return scriptCaller.callCGIScriptSauna(isChecked,95);
					return scriptCaller.callCGIScriptSaunaSet(isChecked,isChecked_HDO);
				} 	catch (IOException e) {return false;
				}
			}

/*			@Override
			protected void onPostExecute(Boolean s)
//			protected void onPostExecute()
			{
		/*		//Show the result obtained from doInBackground
				SimpleArrayMap<String, String> m = Data.get();
				String val = m.get(getString(R.string.sauna_temp));
				if (val != null) {
					//tv_tmp = v.findViewById(R.id.textSaunaTemperature);
				//	tv_tmp.setTextColor(Display_colour);
					tv_tmp.setText(val);
				}

				val = m.get(getString(R.string.sauna_status_text));
				if (val != null) {
				//	tv_sts.setTextColor(Display_colour);
					tv_sts.setText(val);
				}

			}
*/






		};
		boolean result;
		try {
			result = setSwitchStatusTask.execute(isChecked,isChecked_HDO).get();
			if (!result) {
				//			Toast.makeText(getActivity(), "CgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) { //collect all possible exceptions
			Toast.makeText(getActivity(),"Exception Sauna Task:", Toast.LENGTH_LONG).show();
			result = false;
		}
	}

	private void openChart(View v, String graph_values){
		String[] graph_data=graph_values.split("/");
		String[] data_x = graph_data[0].split(",");
		String[] data_y = graph_data[1].split(",");

		// Creating an XYSeries for dataX
		XYSeries dataSeries = new XYSeries("Sauna");
		// Adding data to Series
		double min_val=0, max_val=0;
		for(int i=0;i<data_y.length;i++){
			if (min_val > Double.parseDouble(data_y[i])) {min_val = Double.parseDouble(data_y[i]);}
			if (max_val < Double.parseDouble(data_y[i])) {max_val = Double.parseDouble(data_y[i]);}

			dataSeries.add(i,Double.parseDouble(data_y[i]));
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset(); 		// Creating a dataset to hold each series

		dataset.addSeries(dataSeries);							// Adding Income Series to the dataset
		XYSeriesRenderer dataRenderer = new XYSeriesRenderer();	// Creating XYSeriesRenderer to customize dataSeries
		dataRenderer.setColor(Color.CYAN); 						//color of the graph set to cyan
		dataRenderer.setFillPoints(false);
		dataRenderer.setLineWidth(1f);
		dataRenderer.setDisplayChartValues(false);
		dataRenderer.setDisplayChartValuesDistance(10);			//setting chart value distance
//		dataRenderer.setPointStyle(PointStyle.CIRCLE);			//setting line graph point style to circle
		dataRenderer.setStroke(BasicStroke.SOLID);				//setting stroke of the line chart to solid

		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXLabels(0);
//		multiRenderer.setChartTitle(min_val+"/"+max_val);
		multiRenderer.setXTitle("h");
		multiRenderer.setYTitle("°C");

		//***	* Customizing graphs
		multiRenderer.setChartTitleTextSize(28); 		//setting text size of the title
		multiRenderer.setAxisTitleTextSize(24);			//setting text size of the axis title
		multiRenderer.setLabelsTextSize(24);			//setting text size of the graph lable
		multiRenderer.setZoomButtonsVisible(true);		//setting zoom buttons visiblity
		multiRenderer.setPanEnabled(false, false);		//setting pan enablity which uses graph to move on both axis
		multiRenderer.setClickEnabled(false);			//setting click false on graph
		multiRenderer.setZoomEnabled(false, false);		//setting zoom to false on both axis
		multiRenderer.setShowGridY(true);				//setting lines to display on y axis
		multiRenderer.setShowGridX(true);				//setting lines to display on x axis
		multiRenderer.setFitLegend(true);				//setting legend to fit the screen size
		multiRenderer.setShowGrid(true);				//setting displaying line on grid
		multiRenderer.setZoomEnabled(false);			//setting zoom to false
		multiRenderer.setExternalZoomEnabled(false);	//setting external zoom functions to false
		multiRenderer.setAntialiasing(true);			//setting displaying lines on graph to be formatted(like using graphics)
		multiRenderer.setInScroll(false);				//setting to in scroll to false
		multiRenderer.setLegendHeight(30);				//setting to set legend height of the graph
		multiRenderer.setXLabelsAlign(Paint.Align.CENTER);	//setting x axis label align
		multiRenderer.setYLabelsAlign(Paint.Align.LEFT);		//setting y axis label to align
		multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);		//setting text style
		multiRenderer.setYLabels(10);					//setting no of values to display in y axis

		// setting y axis max value,
		// if you use dynamic values then get the max y value and set here
		multiRenderer.setYAxisMax(max_val);
		multiRenderer.setXAxisMin(min_val);				//setting used to move the graph on xaxiz to .5 to the right
		multiRenderer.setXAxisMax(data_x.length);		//setting used to move the graph on xaxiz to .5 to the right
		//setting bar size or space between two bars
		//multiRenderer.setBarSpacing(0.5);
		multiRenderer.setBackgroundColor(Color.TRANSPARENT);		//Setting background color of the graph to transparent
		multiRenderer.setBackgroundColor(Color.BLACK);
		multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));		//Setting margin color of the graph to transparent
		multiRenderer.setApplyBackgroundColor(true);
		multiRenderer.setScale(2f);
		multiRenderer.setPointSize(4f);					//setting x axis point size
		multiRenderer.setMargins(new int[]{30, 30, 30, 30});		//setting the margin size for the graph in the order top, left, bottom, right
		for(int i=0; i< data_y.length;i++){		multiRenderer.addXTextLabel(i, data_x[i]);
		}

		// Adding dataRenderer and expenseRenderer to multipleRenderer
		// Note: The order of adding dataseries to dataset and renderers to multipleRenderer should be same
		multiRenderer.addSeriesRenderer(dataRenderer);

		//this part is used to display graph on the xml
		LinearLayout chartContainer = v.findViewById(R.id.chart_sauna);
		//remove any views before u paint the chart
		chartContainer.removeAllViews();
		//drawing bar chart
		mChart = ChartFactory.getLineChartView(getActivity(), dataset, multiRenderer);
		//adding the view to the linearlayout
		chartContainer.addView(mChart);
	}
}