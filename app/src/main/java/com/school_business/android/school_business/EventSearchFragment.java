package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.school_business.android.school_business.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventSearchFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	private static final String TAG = "EventSearch";
	private TextView mTextView;
	private ArrayList<Event> events;
	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment EventSearchFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static EventSearchFragment newInstance(String param1, String param2) {
		EventSearchFragment fragment = new EventSearchFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public EventSearchFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
		mTextView = (TextView) getActivity().findViewById(R.id.textViewEventSearch);
		events = new ArrayList<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_event_search, container, false);
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	class Event {
		String id;
		String title;
		String start;
		Event(String i, String et, String es){
			id = i; title = et; start = es;
		}
	}

	public void searchEvents(final String query){
		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/events";
		RequestQueue queue = NetworkVolley.getInstance(this.getActivity().getApplicationContext())
				.getRequestQueue();

		url = String.format(url+"?search="+query.replaceAll(" ","%20")+"&event=");

		//JSONObject obj = new JSONObject(params);
//		inner.put("password", password);
//		HashMap<String, HashMap<String, String>> outer = new HashMap<String, HashMap<String, String>>();
//		outer.put("user", inner);
//		JSONObject obj = new JSONObject(outer);

		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d("JSON", "Response: " + response.toString());
						try {
							JSONObject event_obj;
							JSONArray event_arr = response.getJSONArray("schools");
							Log.d(TAG, response.toString());
							Log.d(TAG, event_arr.toString());
							for (int i = 0; i < event_arr.length(); i++){

								event_obj = (JSONObject) event_arr.get(i);
								Event event = new Event(event_obj.getString("id"),
														event_obj.getString("event_title"),
														event_obj.getString("event_start"));
								mTextView.setText(event.title);
								events.add(event);
								//mCursor.addRow(new Object[] {id, school_name});
							}
							//getApplicationContext().getContentResolver().notifyChange(Schools.CONTENT_URI, null);
							//txtResponse.setText(schoollist);
							/*
							listArr = new String[events.size()];
							listArr = events.toArray(listArr);

							EventItemFragment.MyArrayAdapter adapter = new EventItemFragment.MyArrayAdapter(mContext, listArr);
							setListAdapter(adapter);
							//adapter.notifyDataSetChanged();
							*/
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getActivity().getApplicationContext(),
									"Error: " + e.getMessage(),
									Toast.LENGTH_LONG).show();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error){
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				Toast.makeText(getActivity().getApplicationContext(),
						error.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}){
			@Override
			public String getBodyContentType(){
				return "application/json";
			}

			@Override public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application/json");
				params.put("Accept", "application/json");
				params.put("X-User-Email", SchoolBusiness.getEmail());
				params.put("X-User-Token", SchoolBusiness.getUserAuth());
				params.put("search", query);
				Log.d(TAG,SchoolBusiness.getEmail()+" "+SchoolBusiness.getUserAuth());
				Log.d(TAG, params.toString());
				return params;
			}
			@Override
			public Map<String, String> getParams(){
				Map<String, String> params = new HashMap<String, String>();
				params.put("search", query);
				return params;
			}
		};
		queue.add(jsonRequest);
	}
}
