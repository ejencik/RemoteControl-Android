package remotehouseholdcontrol;

import android.graphics.ColorSpace;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataFragment extends Fragment {

	private TableLayout table;
	private LayoutInflater inflater;
	private HandleDownload h = new HandleDownload(this);

	@Override
	public View onCreateView(LayoutInflater i, ViewGroup container,
	  Bundle savedInstanceState) {
	  View v = i.inflate(R.layout.table, container, false);
	  table = v.findViewById(R.id.table_temperature);
	  inflater = i;
	  Data.setOnDownload(h);
	  return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		Data.setOnDownload(h);
	}

	protected void displayData(SimpleArrayMap<String, String> m) {
    if (m.isEmpty())
      return;
    table.removeAllViews();

		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < m.size(); i++)
		{
			names.add(m.keyAt(i));
		}
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);

      for (int i = 0; i < m.size(); i++)
        {
          View row = inflater.inflate(R.layout.table_row_status, null);
          ((TextView)row.findViewById(R.id.row_key)).setText(names.get(i));
          ((TextView)row.findViewById(R.id.row_val)).setText(m.get(names.get(i)));
          table.addView(row);
        }

    table.setBackgroundColor(0xff000000);
  }

	class ValueComparator implements Comparator<String> {SimpleArrayMap<String, Double> base;

		public ValueComparator(SimpleArrayMap<String, Double> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
	private static class HandleDownload extends Handler {
		private final WeakReference<DataFragment> frag;
		public HandleDownload(DataFragment frag) {
			this.frag = new WeakReference<DataFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Data.MSG_DOWNLOADED) {
				SimpleArrayMap<String, String> m = Data.get();
				DataFragment f = frag.get();
				if (m != null && f != null)
					f.displayData(m);
			}
		}
	}
}