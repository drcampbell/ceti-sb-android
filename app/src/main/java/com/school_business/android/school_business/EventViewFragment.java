package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventViewFragment.onEventViewInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventViewFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String JSON_RESPONSE = "json_response";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	private String event;
	private String event_id;
	private RelativeLayout mLayout;
	private onEventViewInteractionListener mListener;

	/*
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment EventFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static EventViewFragment newInstance(JSONObject response) {
		EventViewFragment fragment = new EventViewFragment();
		Bundle args = new Bundle();
		args.putString(JSON_RESPONSE, response.toString());
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public EventViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//renderEvent(new JSONObject(getIntent().getStringExtra(JSON_RESPONSE)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_event_view, container, false);
		//mLayout = (RelativeLayout) view.findViewById(R.id.layout_fragment_event);
		String id;
		String event_owner;
		Boolean displayClaims = false;
		if (getArguments() != null) {
			try {
				JSONObject obj = new JSONObject(getArguments().getString(JSON_RESPONSE));
				renderEvent(view, obj);
				id = obj.getString("id");
				event_owner = obj.getString("user_id");
				if (obj.getString("speaker").equals("TBA")){
					displayClaims = true;
				}
			} catch (JSONException e) {
				// TODO Handle E
				id = "nil";
				event_owner = "0";
			}
			//mParam1 = getArguments().getString(JSON_RESPONSE);
			//mParam2 = getArguments().getString(ARG_PARAM2);
			if (displayClaims && event_owner.equals(SchoolBusiness.getUserAttr("id"))) {
				mListener.getClaims(id);
			} else {
				view.findViewById(R.id.ll_claims).setVisibility(View.GONE);
			}
		}

		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onEventViewInteraction(uri);
		}
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (onEventViewInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onEventViewInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_location:
				mListener.onEventViewInteraction((String) view.findViewById(R.id.tv_location).getTag(), "schools");
				break;
			case R.id.tv_name:
				mListener.onEventViewInteraction((String) view.findViewById(R.id.tv_name).getTag(), "users");
				break;
			case R.id.tv_speaker:
				mListener.onEventViewInteraction((String) view.findViewById(R.id.tv_speaker).getTag(), "users");
				break;
			case R.id.edit_button:
				mListener.onCreateEvent(true, event);
				break;
			case R.id.claim_button:
				view.findViewById(R.id.claim_button).setClickable(false);
				mListener.onClaimEvent(event);
				break;
			case R.id.delete_button:
				mListener.onDeleteEvent(event_id);
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
	public interface onEventViewInteractionListener {
		// TODO: Update argument type and name
		public void onEventViewInteraction(String id, String direction);
		public void onCreateEvent(Boolean edit, String event);
		public void onClaimEvent(String event);
		public void onDeleteEvent(String id);
		public void getClaims(String id);
	}

	public String get_id(int res) {
		switch (res) {
			case R.id.tv_location:
				return "loc_id";
			case R.id.tv_name:
				return "user_id";
			case R.id.tv_speaker:
				return "speaker_id";
			default:
				return "";
		}
	}

	public void renderEvent(View view, JSONObject response) {
		try {
			event_id = response.getString("id");
			String str;
			String link = "";
			int[] resource = {R.id.tv_title, R.id.tv_speaker, R.id.tv_start,R.id.tv_end,R.id.tv_location,R.id.tv_name,R.id.tv_content};
			String[] name = {"title", "speaker", "event_start", "event_end", "loc_name", "user_name", "content"};
			TextView tv;

			event = response.toString();
			Log.d("WHATISEVENT", event);
			if (!response.getString("user_id").equals(SchoolBusiness.getUserAttr("id"))){
				((Button) view.findViewById(R.id.edit_button)).setVisibility(View.GONE);
				((Button) view.findViewById(R.id.delete_button)).setVisibility(View.GONE);
				if (response.getString("speaker_id").equals(SchoolBusiness.getUserAttr("id"))) {
					((Button) view.findViewById(R.id.claim_button)).setVisibility(View.GONE);
				} else if (response.getBoolean("claim")) {
					((Button) view.findViewById(R.id.claim_button)).setText("Claimed: Pending Approval");
					((Button) view.findViewById(R.id.claim_button)).setBackgroundColor(getResources().getColor(R.color.InactiveButton));
					((Button) view.findViewById(R.id.claim_button)).setClickable(false);
				} else {
					((Button) view.findViewById(R.id.claim_button)).setOnClickListener(EventViewFragment.this);
				}
			} else {
				((Button) view.findViewById(R.id.edit_button)).setOnClickListener(EventViewFragment.this);
				((Button) view.findViewById(R.id.delete_button)).setOnClickListener(EventViewFragment.this);
				((Button) view.findViewById(R.id.claim_button)).setVisibility(View.GONE);
			}
			for (int i = 0; i < resource.length; i++) {
				tv = (TextView) view.findViewById(resource[i]);

				link = get_id(resource[i]);
				if (!link.equals("")) {
					link = response.getString(link);
					tv.setOnClickListener(EventViewFragment.this);
					tv.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
				} else {
					link = "0";
				}
				str = name[i];
				if (str.contains("event")) {
					str = response.getString(str);
				} else {//if (str.equals("school_name")) {
					str = SchoolBusiness.toDisplayCase(response.getString(str));
				}
				tv.setTag(link);
				tv.setText(str);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
}
