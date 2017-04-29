package com.ceti_sb.android.events;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventCreateFragment.OnEventCreatorListener} interface
 * to handle interaction events.
 * Use the {@link EventCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventCreateFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "edit";
	private static final String ARG_PARAM2 = "param2";
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa z", Locale.US);
	Toast toast;
	// TODO: Rename and change types of parameters
	private Boolean mEdit;
	private String mEvent;
	private String mId;
	private OnEventCreatorListener mListener;
    private TimeZone timeZone = TimeZone.getTimeZone("EST");


    Spinner mSpinner;
	ArrayAdapter<String> idAdapter;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param edit Parameter 1.
	 * @return A new instance of fragment CreateEventFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static EventCreateFragment newInstance(Boolean edit, String event) {
		EventCreateFragment fragment = new EventCreateFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_PARAM1, edit);
		args.putString(ARG_PARAM2, event);
		fragment.setArguments(args);

		return fragment;
	}

	public EventCreateFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mEdit = getArguments().getBoolean(ARG_PARAM1);
			mEvent = getArguments().getString(ARG_PARAM2);
		}
		/* Just making a blank toast for the Toaster */
		toast = Toast.makeText(getActivity().getApplicationContext(), Constants.NULL, Toast.LENGTH_SHORT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_event_create, container, false);
		formatView(view);
        if (mListener != null) {
            mListener.onCancelSearchifExists();
        }
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onPostEvent(packageEvent(), mEdit);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnEventCreatorListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnEventCreatorListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onClick(View view){
		switch (view.getId()){
			case R.id.post_event_button:
				view.findViewById(R.id.post_event_button).setClickable(false);
				JSONObject jEvent = packageEvent();
				if (jEvent != null) {
					mListener.onPostEvent(jEvent, mEdit);
				} else {
					view.findViewById(R.id.post_event_button).setClickable(true);
				}
				break;
		}
	}
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnEventCreatorListener {
		// TODO: Update argument type and name
		public void onPostEvent(JSONObject event, Boolean edit);
        public void onCancelSearchifExists();
	}

	public JSONObject packageEvent(){
		try {
			JSONObject event = new JSONObject();
			if (mId != null){
				event.put(Constants.ID, mId);
			}
			EditText data;
			int[] resource = {R.id.ET_content, R.id.ET_title};//R.id.et_tags
			String[] headers = {"content", Constants.TITLE, "tag_list",};

			int[] start = {R.id.start_year, R.id.start_month, R.id.start_day, R.id.start_hour, R.id.start_minutes};
			int[] end = {R.id.end_year, R.id.end_month, R.id.end_day, R.id.end_hour, R.id.end_minutes};

			String event_start = createDate(getView().getRootView(), start, R.id.start_pm);
			String event_end = createDate(getView().getRootView(), end, R.id.end_pm);

			for (int i = 0; i < resource.length; i++) {
				data = (EditText) getActivity().findViewById(resource[i]);
				if (resource[i] == R.id.ET_title){
					event.put(headers[i], data.getText().toString().replace('\n',' ').trim());
				} else {
					event.put(headers[i], data.getText().toString().trim());
				}
			}

			/* Event Validation */
			if (event_start.isEmpty() || event_end.isEmpty()){
				toaster("Error: Event must have a start and end date");
				return null;
			}
			if (!compareDates(dateFormat.format(new Date()), event_start)){
				toaster("Error: Event must begin in the future");
				return null;
			}
			if (!compareDates(event_start, event_end)){
				toaster("Error: Can't Post\nEvent Finishes Before It Begins!");
				return null;
			}
			if (event.getString(Constants.TITLE).trim().length() == 0){
				toaster("Error: Title must consist of alphanumeric characters");
				return null;
			}
			if (event.getString(Constants.TITLE).length() > 255){
				toaster("Error: Title must be less than 255 characters");
				return null;
			}

			/* Event is valid, finish up */
			event.put("time_zone", mSpinner.getSelectedItem());
			event.put("event_start", event_start);
			event.put("event_end", event_end);
			event.put("loc_id", SchoolBusiness.getUserAttr("school_id"));
			return event;
		} catch (JSONException e){
			Log.d("OOPS", "Something went wrong with packaging an event");
			return new JSONObject();
		}
	}

	public void toaster(String message){
		toast.cancel();
		toast = Toast.makeText(getActivity(), message,
				Toast.LENGTH_LONG);
		toast.show();
	}

	public String createDate(View view, int[] resource, int cb){
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		int[] cal_res = {Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE};
		String[] name = {"Year", "Month", "Date", "Hour", "Minute"};
		int[] time_min = {2015, 1, 1, 1, 0 };
		int[] time_max = {2115, 12, 31, 12, 59};
		EditText et;
		Date date;
		Calendar cal = new GregorianCalendar(timeZone);
		date = cal.getTime();
		Log.d("test", date.toString());
		//TimeZone timeZone = cal.getTimeZone();
		int[] x = {0,0,0,0,0};
		for (int i = 0; i < 5; i++){
			et = (EditText) view.findViewById(resource[i]);
			try {
				x[i] = Integer.parseInt(et.getText().toString());
				if (x[i] < time_min[i] || x[i] > time_max[i]){
					toaster("Values for " + name[i] + " must be between " + time_min[i] + " and " + time_max[i]);
					return Constants.NULL;
				}
			} catch (NumberFormatException e){
				toaster("You must enter a valid value for " + name[i]);
				return Constants.NULL;
			}
			if (cal_res[i]==Calendar.MONTH){
				x[i] = (x[i]-1) % 12;
			}
			if (cal_res[i] == Calendar.HOUR_OF_DAY && ((CheckBox) view.findViewById(cb)).isChecked()){
				if (x[i] != 12) {
					x[i] = (x[i] + 12) % 24;
				}
			} else if (cal_res[i] == Calendar.HOUR_OF_DAY && x[i] == 12){
				x[i] = 0;
			}
		}
		cal.set(x[0],x[1],x[2],x[3],x[4],0);
		date = cal.getTime();
		String strdate = dateFormat.format(date);
		Log.d("test", strdate);
		Log.d("test", date.toString());
		return strdate;
	}

	public Date parseDate(String data){
		Date date;
		try {
			date = dateFormat.parse(data);
		} catch (ParseException e){
			Log.d("Parse Exception:", e.getMessage().toString());
			date = new Date();
		}
		return date;
	}

	public void formatView(View view) {
		int[] start_res = {R.id.start_minutes, R.id.start_hour, R.id.start_day, R.id.start_month, R.id.start_year};
		int[] end_res = {R.id.end_minutes, R.id.end_hour, R.id.end_day, R.id.end_month, R.id.end_year};
		String[] time_min = {"00", "1", "1", "1", "2015"};
		String[] time_max = {"59", "12", "31", "12", "2115"};
		int[] cal_res = {Calendar.MINUTE, Calendar.HOUR, Calendar.DATE, Calendar.MONTH, Calendar.YEAR};

		EditText et;

		Date date;

        Calendar cal = Calendar.getInstance(timeZone);

		/* Timezone spinna */
		//populate spinner with all timezones

		mSpinner = (Spinner) view.findViewById(R.id.TimeZoneEntry);
		String[] idArray = SchoolBusiness.time_zones; //TimeZone.getAvailableIDs();
		idAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
				idArray);
		idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(idAdapter);

		// now set the spinner to default timezone from the time zone settings
//		for(int i = 0; i < idAdapter.getCount(); i++) {
//			if(idAdapter.getItem(i).equals(TimeZone.getDefault().getID())) {
//				mSpinner.setSelection(i);
//			}
//		}
		mSpinner.setSelection(0);
		/* Timezone spinna */
		if (mEdit){
			try {
				JSONObject event = new JSONObject(mEvent);
				mId = event.getString(Constants.ID);
				((EditText) view.findViewById(R.id.ET_title)).setText(event.getString(Constants.TITLE));
				((EditText) view.findViewById(R.id.ET_content)).setText(event.getString("content"));
				//if (event.has("tags")){
				//	((EditText) view.findViewById(R.id.et_tags)).setText(event.getString("tags"));
				//}

				cal.setTime(parseDate(event.getString("event_start")));
				setETDate(cal, view, start_res, time_min, time_max, cal_res, R.id.start_pm);

				cal.setTime(parseDate(event.getString("event_end")));
				setETDate(cal, view, end_res, time_min, time_max, cal_res, R.id.end_pm);

				((Button) view.findViewById(R.id.post_event_button)).setText("Update Event");
			} catch (JSONException e){
				Log.d("JSONException", e.getMessage().toString());
			}
		} else {
			cal.setTime(parseDate(Constants.NULL));

			// Set start time
			cal.set(Calendar.HOUR_OF_DAY, (cal.get(Calendar.HOUR_OF_DAY) + 1 % 24));
			cal.set(Calendar.MINUTE, 0);
			setETDate(cal, view, start_res, time_min, time_max, cal_res, R.id.start_pm);
			// Set end time
			cal.set(Calendar.HOUR_OF_DAY, (cal.get(Calendar.HOUR_OF_DAY) + 1 % 24));
			setETDate(cal, view, end_res, time_min, time_max, cal_res, R.id.end_pm);
		}
		((Button) view.findViewById(R.id.post_event_button)).setOnClickListener(EventCreateFragment.this);
	}


	public void setETDate(Calendar cal, View view, int[] resource, String[] begin, String[] end, int[] cal_res, int cbid) {
		CheckBox cb = (CheckBox) view.findViewById(cbid);
		Log.d("time", cal.toString());
		for (int i = 0; i < resource.length; i++) {
			EditText et = (EditText) view.findViewById(resource[i]);
			//et.setFilters(new InputFilter[]{new InputFilterNumeric(begin[i], end[i])});
			if (cal_res[i] == Calendar.HOUR && cal.get(Calendar.HOUR) != cal.get(Calendar.HOUR_OF_DAY)) {
				cb.setChecked(true);
			}
			if (cal_res[i] == Calendar.MONTH){
				et.setText(Constants.NULL+(cal.get(cal_res[i])+1));
			} else if (cal_res[i] == Calendar.HOUR && cal.get(Calendar.HOUR) == 0) {
				et.setText("12");
			}else {
				et.setText(Constants.NULL + cal.get(cal_res[i]));
			}
		}
	}

	public class InputFilterNumeric implements InputFilter {

		private int min, max;

		public InputFilterNumeric(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterNumeric(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
		                           int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString() + source.toString());
				if (isInRange(min, max, input))
					return null;
			} catch (NumberFormatException nfe) {
				toast.cancel();
				toast = Toast.makeText(getActivity(),
						"Entered value must be between " + min + " and " + max,
						Toast.LENGTH_LONG);
				toast.show();
			}
			return Constants.NULL;
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}

	public boolean compareDates(String date1, String date2) {
		try {
			Date d1 = dateFormat.parse(date1);
			Date d2 = dateFormat.parse(date2);
			return d1.compareTo(d2)<0;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}




}
