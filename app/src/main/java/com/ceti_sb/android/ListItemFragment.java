package com.ceti_sb.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.ceti_sb.android.R.color.DefButtonColor;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListItemInteractionListener}
 * interface.
 */
public class ListItemFragment extends Fragment implements AbsListView.OnItemClickListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	//private static final ArrayList<Java.Lang.String> ARG_PARAM1 = new ArrayList<>();
	private static final String ARG_IDS_PARAM = "ids";
	private static final String ARG_TITLES_PARAM = "titles";
	private static final String ARG_AUX_PARAM = "starts";
	private static final String ARG_AUX_ID_PARAMS = "aux_ids";
	private static final String ARG_READ_PARAMS = "read_states";
	private static final String ARG_MODEL_PARAM = "model";
//	private static final ArrayList<String> ARG_PARAM2 = new ArrayList<>();
//	private static final ArrayList<String> ARG_PARAM3 = new ArrayList<>();

	// TODO: Rename and change types of parameters
	private ArrayList<String> idParams = new ArrayList<>();
	private ArrayList<String> titleParams = new ArrayList<>();
	private ArrayList<String> auxParams = new ArrayList<>();
	private ArrayList<String> auxIdParams = new ArrayList<>();
	private boolean[] readParams;
	private String mModel;

	public static final String NOTIFICATIONS = "notifications";
	public static final String EVENTS = "events";
	private static String[] events = {"id", "event_title", "event_start"};
	private static String[] schools = {"id", "school_name", "city_state"};
	private static String[] users = {"id", "name", "association"};
	private static String[] claims = {"claim_id", "user_name", "business"};
	private OnListItemInteractionListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	private static String[] modelSwitch(String model){
		switch (model){
			case "events":
				return events;
			case "schools":
				return schools;
			case "users":
				return users;
			case "claims":
				return claims;
			default:
				return events;
		}
	}

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ListAdapter mAdapter;

	public static ListItemFragment newInstance(JSONObject response, String model){
		String[] key = modelSwitch(model);
		ArrayList<String> ids = new ArrayList<>();
		ArrayList<String> titles = new ArrayList<>();
		ArrayList<String> starts = new ArrayList<>();
		ArrayList<String> auxIds = new ArrayList<>();
		//Array readStates;// = new Array<Boolean>();
		boolean[] readStates;
        try {			
			JSONObject obj;
			JSONArray obj_arr = response.getJSONArray(model);
	        readStates = new boolean[obj_arr.length()];
	        Arrays.fill(readStates, true);
			for (int i = 0; i < obj_arr.length(); i++){
				obj = (JSONObject) obj_arr.get(i);
				switch (model){
					case EVENTS:
						ids.add   (obj.getString(key[0]));
						titles.add(obj.getString(key[1]));
						starts.add(obj.getString(key[2]));
						readStates[i] = true;
						//starts.add(SchoolBusiness.parseTime(obj.getString(key[2])));
						break;
					case NOTIFICATIONS:
						int n_type = Integer.parseInt(obj.getString("n_type"));
						ids.add(obj.getString("event_id"));
						titles.add(obj.getString("act_user_name"));
						readStates[i] = obj.getBoolean("read");
						if (n_type != 3 && n_type != 4) {
							starts.add(notification(obj.getString("event_title"), n_type));
						} else {
							starts.add(notification("", n_type));
						}
						auxIds.add(obj.getString("id"));
						break;
					default:
						ids.add   (obj.getString(key[0]));
						titles.add(obj.getString(key[1]));
						starts.add(obj.getString(key[2]));
						readStates[i] = true;
						break;
				}
			}
		} catch (JSONException e){
			e.printStackTrace();
//			Toast.makeText(getActivity().getApplicationContext(),
//					"Error: " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
	        return null;
		}
		ListItemFragment fragment = new ListItemFragment();
		Bundle args = new Bundle();
		args.putStringArrayList(ARG_IDS_PARAM, ids);
		args.putStringArrayList(ARG_TITLES_PARAM, titles);
		args.putStringArrayList(ARG_AUX_PARAM, starts);
		args.putStringArrayList(ARG_AUX_ID_PARAMS, auxIds);
		args.putBooleanArray(ARG_READ_PARAMS, readStates);
		Log.d("ListItem", model);
		if (model.equals(NOTIFICATIONS)){
			args.putString(ARG_MODEL_PARAM, NOTIFICATIONS);
		} else {
			args.putString(ARG_MODEL_PARAM, model);
		}
		fragment.setArguments(args);
		return fragment;
	}


	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ListItemFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			idParams = getArguments().getStringArrayList(ARG_IDS_PARAM);
			titleParams = getArguments().getStringArrayList(ARG_TITLES_PARAM);
			auxParams = getArguments().getStringArrayList(ARG_AUX_PARAM);
			auxIdParams = getArguments().getStringArrayList(ARG_AUX_ID_PARAMS);
			readParams = getArguments().getBooleanArray(ARG_READ_PARAMS);
			mModel = getArguments().getString(ARG_MODEL_PARAM);
			//titleParams = getArguments().getString(ARG_PARAM2);
		}


		mAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_2, android.R.id.text1, titleParams){
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
				TextView text2 = (TextView) view.findViewById(android.R.id.text2);
				if (!readParams[position]){
					view.setBackgroundColor(getResources().getColor(R.color.UnreadColor));
				} else {
					view.setBackgroundColor(getResources().getColor(R.color.tw__solid_white));
				}
				text1.setText(titleParams.get(position));
				text2.setText(auxParams.get(position));

				return view;
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_item, container, false);

		// Set the adapter
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		if (idParams.isEmpty()){
			setEmptyText(view, "No "+mModel+" found");
		}

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnListItemInteractionListener) activity;
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


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.

			if (mModel.equals(getString(R.string.notifications))){
				mListener.onListItemSelected(idParams.get(position), getString(R.string.events));
				Log.d("NOTIFICATIONs", "Sanity "+auxIdParams.get(position));
				mListener.onNotificationViewed(auxIdParams.get(position));
			} else {
				mListener.onListItemSelected(idParams.get(position), mModel);
			}
		}
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(View view, CharSequence emptyText) {
		TextView emptyView = (TextView) view.findViewById(R.id.search_fail);

		emptyView.setText(emptyText);
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
	public interface OnListItemInteractionListener {
		// TODO: Update argument type and name
		public void onListItemSelected(String id, String model);
		public void onNotificationViewed(String id);
	}

	public static String notification(String event_title, int n_type){
		switch(n_type){
			case 0:
				return "has claimed your event:" + event_title;
			case 1:
				return "has confirmed you as the speaker of event: " + event_title;
			case 2:
				return "has updated event: " + event_title;
			case 3:
				return "has sent you a message via email";
			case 4:
				return "has awarded you a badge!";
			case 5:
				return "Award them a badge!";
			case 6:
				return "has canceled their event";
			case 7:
				return "has chosen a different candidate";
			case 8:
				return "has canceled their claim";
			case 9:
				return "has canceled their speaking engagement";
			default:
				return event_title;
		}
	}
}
