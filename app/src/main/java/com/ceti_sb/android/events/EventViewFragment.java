package com.ceti_sb.android.events;

import android.app.Activity;
import android.graphics.Paint;
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

import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;
import com.ceti_sb.android.views.ListItemFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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
	private String claim_id;

	private RelativeLayout mLayout;
	private onEventViewInteractionListener mListener;
	public final int CANCEL_EVENT = 0; public final int CANCEL_CLAIM = 1; public final int CANCEL_SPEAKER = 2;
	private int mode;

	JSONObject speakerNameArrayList = new JSONObject();
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
		Boolean displayClaims = true;
		if (getArguments() != null) {
			try {
				JSONObject obj = new JSONObject(getArguments().getString(JSON_RESPONSE));
				Boolean future_event = renderEvent(view, obj);
				id = obj.getString(Constants.ID);
				event_owner = obj.getString("user_id");
				if (future_event && obj.getString(Constants.SPEAKER).equals("TBA")){
					displayClaims = true;
				}
			} catch (JSONException e) {
				// TODO Handle E
				id = "nil";
				event_owner = "0";

			}
			//mParam1 = getArguments().getString(JSON_RESPONSE);
			//mParam2 = getArguments().getString(ARG_PARAM2);
			if (displayClaims && event_owner.equals(SchoolBusiness.getUserAttr(Constants.ID))) {
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
				//ListItemFragment listItemFragment = ListItemFragment.newInstance(speakerNameArrayList, "users", "?search=");
				mListener.onEventViewInteractionUserList(speakerNameArrayList, "users");
				//mListener.onEventViewInteraction((String) view.findViewById(R.id.tv_speaker).getTag(), "users");
				break;
			case R.id.edit_button:
				mListener.onCreateEvent(true, event);
				break;
			case R.id.claim_button:
				view.findViewById(R.id.claim_button).setClickable(false);
				mListener.onClaimEvent(event);
				break;
			case R.id.delete_button:
				switch (mode){
					case CANCEL_CLAIM:
						mListener.onCancelClaim(claim_id);
						break;
					case CANCEL_EVENT:
						mListener.onDeleteEvent(event_id);
						break;
					case CANCEL_SPEAKER:
						mListener.onCancelClaim(claim_id);
						break;
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
	public interface onEventViewInteractionListener {
		// TODO: Update argument type and name
		public void onEventViewInteraction(String id, String direction);
		public void onCreateEvent(Boolean edit, String event);
		public void onClaimEvent(String event);
		public void onDeleteEvent(String id);
		public void getClaims(String id);
		public void onCancelClaim(String claim_id);
		public void onEventViewInteractionUserList(JSONObject id, String direction);
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
				return Constants.NULL;
		}
	}

	public boolean renderEvent(View view, JSONObject response) {
		Boolean future_event;
		Boolean canceled;
		try {
			/* Was the Event Canceled? */
			canceled = !response.getBoolean("active") && !response.getBoolean("complete");
			event_id = response.getString(Constants.ID);
			claim_id = response.getString("claim_id");
			String str;
			String link = Constants.NULL;
			int[] resource = {R.id.tv_title, R.id.tv_speaker, R.id.tv_start,R.id.tv_end,R.id.tv_location,R.id.tv_name,R.id.tv_content};
			String[] name = {Constants.TITLE, Constants.SPEAKER, "event_start", "event_end", "loc_name", Constants.USER_NAME, "content"};
			TextView tv;

			event = response.toString();
			Log.d("WHATISEVENT", event);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa zzz", Locale.US);

			/* Is the event occurring in the future? */
			try {
				future_event = sdf.parse(response.getString(Constants.EVENT_START)).after(new Date());
			} catch (java.text.ParseException e){
				future_event = false;
			}

			/* Is the user not the owner of the event? */
			if (!response.getString("user_id").equals(SchoolBusiness.getUserAttr(Constants.ID))){
				((Button) view.findViewById(R.id.edit_button)).setVisibility(View.GONE);
				((Button) view.findViewById(R.id.delete_button)).setVisibility(View.GONE);
				if (future_event && !canceled) {
					/* Is user the speaker of the event? */
					if (response.getString("speaker_id").equals(SchoolBusiness.getUserAttr(Constants.ID))) {
						((Button) view.findViewById(R.id.claim_button)).setVisibility(View.GONE);
						((Button) view.findViewById(R.id.delete_button)).setVisibility(View.VISIBLE);
						((Button) view.findViewById(R.id.delete_button)).setText("Cancel");
						((Button) view.findViewById(R.id.delete_button)).setOnClickListener(EventViewFragment.this);
						mode = CANCEL_SPEAKER;
					/* Does user have a claim on event? " */
					} else if (Integer.parseInt(response.getString("claim_id")) > 0) {
						((Button) view.findViewById(R.id.claim_button)).setText("Claimed: Pending Approval");
						((Button) view.findViewById(R.id.claim_button)).setBackgroundColor(getResources().getColor(R.color.InactiveButton));
						((Button) view.findViewById(R.id.claim_button)).setClickable(false);
						((Button) view.findViewById(R.id.delete_button)).setVisibility(View.VISIBLE);
						((Button) view.findViewById(R.id.delete_button)).setText("Cancel Claim");
						((Button) view.findViewById(R.id.delete_button)).setOnClickListener(EventViewFragment.this);
						mode = CANCEL_CLAIM;
					} else {
						((Button) view.findViewById(R.id.claim_button)).setOnClickListener(EventViewFragment.this);
					}
				} else {
					view.findViewById(R.id.claim_button).setVisibility(View.GONE);
				}

			/* Owner View */
			} else {
				if (future_event && !canceled) {
					((Button) view.findViewById(R.id.edit_button)).setOnClickListener(EventViewFragment.this);
					((Button) view.findViewById(R.id.delete_button)).setOnClickListener(EventViewFragment.this);
				} else {
					((Button) view.findViewById(R.id.edit_button)).setVisibility(View.GONE);
					((Button) view.findViewById(R.id.delete_button)).setVisibility(View.GONE);
				}
				((Button) view.findViewById(R.id.claim_button)).setVisibility(View.GONE);
				mode = CANCEL_EVENT;
			}

			for (int i = 0; i < resource.length; i++) {
				tv = (TextView) view.findViewById(resource[i]);
				/* If Event is canceled strike the text through */
				if (canceled && resource[i] == R.id.tv_title){
					tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				}
				/* Assign a link to the TV if needed */
				link = get_id(resource[i]);
				if (!link.equals(Constants.NULL)) {
					link = response.getString(link);
					tv.setOnClickListener(EventViewFragment.this);
					tv.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

				} else {
					link = "0";
				}
				/* String Formatting - Everything besides event_start and end get display case */
				str = name[i];
				if (str.contains("event")) {
					str = response.getString(str);
				} else {//if (str.equals("school_name")) {
					if(str .equals("speaker")){
						if(!response.getString(str).equals("TBA")) {
							JSONArray speakerNameArr = new JSONArray(response.getString(str));

							str = " ";
							int count = 0;
							if(speakerNameArr.length() > 2) {
								count = 2;
							}else{
								count = speakerNameArr.length();
							}
							JSONObject speakerName = new JSONObject();
							for(i = 0;i < speakerNameArr.length(); i++){
								 speakerName = speakerNameArr.getJSONObject(i);
								if(i < count) {
									str += SchoolBusiness.toDisplayCase(speakerName.getString("name")) + ", ";
								}
							}

							speakerNameArrayList.accumulate("users", speakerNameArr);
							if(speakerNameArr.length() > 2) {
								str += SchoolBusiness.toDisplayCase(" more");
							}
							else if (str != null && str.length() > 0 && str.charAt(str.length() - 2) == ',') {
								str =  str.substring(0, str.length() - 2);
							}
						}else{
							str = SchoolBusiness.toDisplayCase(response.getString(str));
						}

					}else {
						str = SchoolBusiness.toDisplayCase(response.getString(str));
					}
				}
				tv.setTag(link);
				tv.setText(str);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			future_event = false;
		}
		return future_event;
	}
}
