package remotehouseholdcontrol;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v4.util.SimpleArrayMap;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;

import remotehouseholdcontrol.nettools.CgiScriptCaller;
import remotehouseholdcontrol.nettools.ConnectionCredentialsManager;

public class GraphTankFragment extends Fragment {

//	private TableLayout table;
//	private LayoutInflater inflater;
//	private HandleDownload h = new HandleDownload(this);
	private View mChart;
	private ConnectionCredentialsManager cm;
	
	@Override
	public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
	  View v = i.inflate(R.layout.graph_two_fragment_layout, container, false);
//	  table = (TableLayout)v.findViewById(R.id.table);
//	  inflater = i;
	  
      cm = new ConnectionCredentialsManager(getActivity());
	//	    int sensor_id = getResources().getInteger(R.integer.sensor_OUT_tank);
		int sensor_id = getResources().getInteger(R.integer.sensor_OUT_tank);

			AsyncTask< Integer, Void, String> getGraphDataTask = new AsyncTask< Integer, Void, String>() {
				@Override
				protected String doInBackground( Integer ... params) {
					Integer device = params[0];
					CgiScriptCaller scriptCaller = new CgiScriptCaller(cm);
					try {
						return scriptCaller.callCGIScriptAndGetGraphDataWaterTank();
					} catch (IOException e) {return "";
											}
				}
			};
			String graph_values;
			try {
				graph_values = getGraphDataTask.execute(sensor_id).get();
				if (graph_values !="") {
	//			Toast.makeText(getActivity(), "GraphCgiScriptCaller result: " + result, Toast.LENGTH_LONG).show();
					String[] graph_valuesUpDown=graph_values.split("%");
					openChart(v,graph_valuesUpDown[0],graph_valuesUpDown[1],true);
					openChart(v,graph_valuesUpDown[2],graph_valuesUpDown[2],false);

					  return v;
	   			}
			} catch (Exception e) { //collect all possible exceptions
				Toast.makeText(getActivity(),"Exception GraphTask"+e, Toast.LENGTH_LONG).show();					
				graph_values = "";
  			  }	  
			  return v;
	}
	private void openChart(View v, String graph_values, String graph_values2, Boolean pos){
		String[] graph_data=graph_values.split("/");
		String[] data_x = graph_data[0].split(",");
		String[] data_y = graph_data[1].split(",");

			String[] graph_data2 = graph_values2.split("/");
			String[] data_y2 = graph_data2[1].split(",");

		// Creating an XYSeries for dataX
		XYSeries dataSeries 	= new XYSeries("");
		XYSeries dataSeries_neg = new XYSeries("");
		XYSeries dataSeries2 	= new XYSeries("");

		// Adding data to Series
		double min_val=0, max_val=0;
		int data_length=data_y.length>=data_y2.length?data_y.length:data_y2.length;
		for(int i=0;i<data_length;i++) {

			if (min_val > Double.parseDouble(data_y[i])) {min_val = Double.parseDouble(data_y[i]);}
			if (max_val < Double.parseDouble(data_y[i])) {max_val = Double.parseDouble(data_y[i]);}
			if (max_val < Double.parseDouble(data_y2[i])) {max_val = Double.parseDouble(data_y2[i]);}

			if (Double.parseDouble(data_y[i]) > 0) 	{
				dataSeries.add(i,Double.parseDouble(data_y[i]));
				dataSeries_neg.add(i,0);
			}
			else {
				dataSeries_neg.add(i,Double.parseDouble(data_y[i]));
				dataSeries.add(i,0);
			}

			if ((i < data_y2.length)&& (data_y2.length>0)) {
				dataSeries2.add(i, Double.parseDouble(data_y2[i]));
			}
			else {
				dataSeries2.add(i, Double.parseDouble(data_y2[data_y2.length-1])); // pokud nejsou data tak dopln posledni hodnotou
			}
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset(); 		// Creating a dataset to hold each series

		dataset.addSeries(dataSeries);							// Adding Income Series to the dataset
		XYSeriesRenderer dataRenderer = new XYSeriesRenderer();	// Creating XYSeriesRenderer to customize dataSeries
		dataRenderer.setColor(Color.GREEN); 						//color of the graph set to cyan
		dataRenderer.setFillPoints(false);
		dataRenderer.setLineWidth(1f);
		dataRenderer.setDisplayChartValues(false);
		dataRenderer.setDisplayChartValuesDistance(10);			//setting chart value distance
//		dataRenderer.setPointStyle(PointStyle.CIRCLE);			//setting line graph point style to circle
		dataRenderer.setStroke(BasicStroke.SOLID);				//setting stroke of the line chart to solid


		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXLabels(0);
		SimpleArrayMap<String, String> m = Data.get();
		multiRenderer.setChartTitle(!pos?m.get(getString(R.string.tank_temperature))+"°C":"");
		multiRenderer.setXTitle("h");
		multiRenderer.setYTitle(pos?"%":"m3");

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
		multiRenderer.setXLabelsAlign(Align.CENTER);	//setting x axis label align
		multiRenderer.setYLabelsAlign(Align.LEFT);		//setting y axis label to align
		multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);		//setting text style
		multiRenderer.setYLabels(10);					//setting no of values to display in y axis

		// setting y axis max value,
		// if you use dynamic values then get the max y value and set here
		multiRenderer.setYAxisMax(max_val);
		multiRenderer.setYAxisMin(min_val);
		multiRenderer.setXAxisMin(0);				//setting used to move the graph on xaxiz to .5 to the right
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

		if (pos) {		//	setting for upper  graph with water levels in %
			XYSeriesRenderer dataRenderer2 = new XYSeriesRenderer();    // Creating XYSeriesRenderer to customize dataSeries
			dataset.addSeries(dataSeries2);                            // Adding Income Series to the dataset
			dataRenderer2.setColor(Color.YELLOW);
			multiRenderer.addSeriesRenderer(dataRenderer2);

		}
		else
		{			//	setting for lower bar graph
			XYSeriesRenderer dataRenderer_neg = new XYSeriesRenderer();    // Creating XYSeriesRenderer to customize dataSeries
			dataset.addSeries(dataSeries_neg);                            // Adding Income Series to the dataset
			dataRenderer_neg.setColor(Color.RED);
			multiRenderer.addSeriesRenderer(dataRenderer_neg);
		}
		//this part is used to display graph on the xml
		//	LinearLayout chartContainer = v.findViewById(R.id.chart_up);
		LinearLayout chartContainer =pos? (LinearLayout) v.findViewById(R.id.chart_up):(LinearLayout) v.findViewById(R.id.chart_down);

		//remove any views before u paint the chart
		chartContainer.removeAllViews();
		//drawing bar chart

		if (pos) {
			mChart = ChartFactory.getLineChartView(getActivity(), dataset, multiRenderer);
		}
		else {
			mChart = ChartFactory.getBarChartView(getActivity(), dataset, multiRenderer, BarChart.Type.DEFAULT);
		}
		//adding the view to the linearlayout
		chartContainer.addView(mChart);
	}
}