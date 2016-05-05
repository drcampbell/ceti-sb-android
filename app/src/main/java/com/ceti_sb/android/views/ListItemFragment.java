package com.ceti_sb.android.views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;
import com.ceti_sb.android.volley.NetworkVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListItemInteractionListener}
 * interface.
 */
public class ListItemFragment extends Fragment implements AbsListView.OnItemClickListener,
															View.OnClickListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	//private static final ArrayList<Java.Lang.String> ARG_PARAM1 = new ArrayList<>();
	private static final String ARG_IDS_PARAM = "ids";
	private static final String ARG_TITLES_PARAM = "titles";
	private static final String ARG_AUX_PARAM = "starts";
	private static final String ARG_AUX_ID_PARAMS = "aux_ids";
	private static final String ARG_READ_PARAMS = "read_states";
	private static final String ARG_MODEL_PARAM = "model";
	private static final String ARG_DATA = "data";
	private static final String ARG_ID = Constants.ID;
//	private static final ArrayList<String> ARG_PARAM2 = new ArrayList<>();
//	private static final ArrayList<String> ARG_PARAM3 = new ArrayList<>();
	private static final String TAG = "ListItem";


	// TODO: Rename and change types of parameters
	private ArrayList<String> idParams = new ArrayList<>();
	private ArrayList<String> titleParams = new ArrayList<>();
	private ArrayList<String> auxParams = new ArrayList<>();
	private ArrayList<String> auxIdParams = new ArrayList<>();
	private boolean[] readParams;
	private String idParam;

	private List<Map<String, String>> mData;
	private String mModel;
	private String mId;
	private String mDelim;
	private int mPage;
	private boolean mLock;

	private static String[] events = {Constants.ID, Constants.EVENT_TITLE, Constants.EVENT_START};
	private static String[] schools = {Constants.ID, Constants.SCHOOL_NAME, "city_state"};
	private static String[] users = {Constants.ID, Constants.NAME, "association"};
	private static String[] claims = {"claim_id", Constants.USER_NAME, Constants.BUSINESS};

	private OnListItemInteractionListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private SimpleAdapter mAdapter;

	private static String[] modelSwitch(String model){
		switch (model){
			case Constants.EVENTS:
				return events;
			case Constants.SCHOOLS:
				return schools;
			case Constants.USERS:
				return users;
			case Constants.CLAIMS:
				return claims;
			default:
				return events;
		}
	}
	/* When calling newInstance, don't include '?' or '/' as a prefix to the id.  This is handled by
	 * the create function based upon what strings the id contains (user_id, string_id) */
	public static ListItemFragment newInstance(JSONObject response, String model, String id){
		String[] key = modelSwitch(model);
		Log.d(TAG, "newInstance called: "+id);
		List<Map<String, String>> tData = processJSON(response, model);
		ListItemFragment fragment = new ListItemFragment();
		Bundle args = new Bundle();
		/* Switching to Serializable */
		args.putSerializable(ARG_DATA, (Serializable) tData);
		args.putString(ARG_ID, id);
		Log.d("ListItem", model);
		if (model.equals(Constants.NOTIFICATIONS)){
			args.putString(ARG_MODEL_PARAM, Constants.NOTIFICATIONS);
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
			mData = (List<Map<String, String>>) getArguments().getSerializable(ARG_DATA);
//			idParams = getArguments().getStringArrayList(ARG_IDS_PARAM);
//			titleParams = getArguments().getStringArrayList(ARG_TITLES_PARAM);
//			auxParams = getArguments().getStringArrayList(ARG_AUX_PARAM);
//			auxIdParams = getArguments().getStringArrayList(ARG_AUX_ID_PARAMS);
//			readParams = getArguments().getBooleanArray(ARG_READ_PARAMS);
			mModel = getArguments().getString(ARG_MODEL_PARAM);
			mId = getArguments().getString(ARG_ID);
			mPage = 2;
			mLock = false;
			if (mId.contains(Constants.SEARCH) || mId.contains(Constants.SCHOOL_ID) || mId.contains(Constants.USER_ID)){
				mDelim = "?";
			} else if (mId.isEmpty()) {
				mDelim = Constants.NULL;
			} else {
				mDelim = "/";
			}
			Log.d(TAG, "onCreate: "+mId);
			//titleParams = getArguments().getString(ARG_PARAM2);
		}

		mAdapter = new SimpleAdapter(getActivity(),
										mData,
										android.R.layout.simple_expandable_list_item_2,
										new String[] {Constants.TITLE, Constants.DATA},
										new int[] {android.R.id.text1, android.R.id.text2})
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
				TextView text2 = (TextView) view.findViewById(android.R.id.text2);
				if (!Boolean.parseBoolean(mData.get(position).get(Constants.READ))){//readParams[position]){
					view.setBackgroundColor(getResources().getColor(R.color.UnreadColor));
				} else {
					view.setBackgroundColor(getResources().getColor(R.color.tw__solid_white));
				}
				text1.setText(mData.get(position).get(Constants.TITLE));//titleParams.get(position));
				text2.setText(mData.get(position).get(Constants.DATA));//auxParams.get(position));

				return view;
			}
		};
//		mAdapter = new ArrayAdapter<String>(getActivity(),
//				android.R.layout.simple_list_item_2, android.R.id.text1, titleParams){
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent){
//				View view = super.getView(position, convertView, parent);
//				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//				TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//				if (!Boolean.parseBoolean(mData.get(position).get("read"))){//readParams[position]){
//					view.setBackgroundColor(getResources().getColor(R.color.UnreadColor));
//				} else {
//					view.setBackgroundColor(getResources().getColor(R.color.tw__solid_white));
//				}
//				text1.setText(mData.get(position).get("title"));//titleParams.get(position));
//				text2.setText(mData.get(position).get("data"));//auxParams.get(position));
//
//				return view;
//			}
//		};
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

		// Attach the scroll listener to the adapter
		mListView.setOnScrollListener(new InfiniteScrollListener(5) {
			@Override
			public void loadMore(int page, int totalItemsCount) {
				/* Don't call loader while loading */
				if (!mLock){
					mLock = true;
					loader();
				}
			}
		});
		/* CHANGE */
		if (mData.isEmpty()) {//idParams.isEmpty()){
			setEmptyText(view, "No "+mModel+" found");
		}
		/* Make Clear Notifications Button Visible and Active */
		if (mModel.equals(getString(R.string.notifications))){
			view.findViewById(R.id.clear_notifications_button).setVisibility(View.VISIBLE);
			view.findViewById(R.id.clear_notifications_button).setOnClickListener(this);
		}
		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mListener = (OnListItemInteractionListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnFragmentInteractionListener");
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
			case R.id.clear_notifications_button:
				mListener.markAllNotificationsRead();
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			view.setBackgroundColor(getResources().getColor(R.color.tw__solid_white));
			if (mModel.equals(getString(R.string.notifications))){
				if (mData.get(position).get(Constants.N_TYPE).equals("4")){
					mListener.onGetAwardBadge(mData.get(position).get(Constants.ID));
				} else {
					mListener.onListItemSelected(mData.get(position).get(Constants.ID), getString(R.string.events));//idParams.get(position), getString(R.string.events));
				}
				mListener.onNotificationViewed(mData.get(position).get(Constants.AUX_ID));//auxIdParams.get(position));
			} else {
				mListener.onListItemSelected(mData.get(position).get(Constants.ID), mModel);//idParams.get(position), mModel);
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
		public void onGetAwardBadge(String event_id);
		public void onListItemSelected(String id, String model);
		public void onNotificationViewed(String id);
		public void markAllNotificationsRead();
	}

	public static String notification(String event_title, int n_type) {
		switch (n_type) {
			case 0:
				return "has claimed your event:" + event_title;
			case 1:
				return "has confirmed you as the speaker of event: " + event_title;
			case 2:
				return "has updated event: " + event_title;
			case 3:
				return "has sent you a message via email";
			case 4:
				return "Award them a badge!";
			case 5:
				return "has awarded you a badge!";
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

	public static List<Map<String, String>> processJSON(JSONObject response, String pModel){
		String[] key = modelSwitch(pModel);
		List<Map<String,String>> tData = new ArrayList<Map<String,String>>(){};
		try {
			JSONObject obj;
			JSONArray obj_arr = response.getJSONArray(pModel);
			for (int i = 0; i < obj_arr.length(); i++) {
				obj = (JSONObject) obj_arr.get(i);
				Map<String, String> map = new HashMap<>();
				switch (pModel) {
					case Constants.NOTIFICATIONS:
						int n_type = Integer.parseInt(obj.getString(Constants.N_TYPE));
						/* Map replacement */
						map.put(Constants.ID, obj.getString(Constants.EVENT_ID));
						map.put(Constants.TITLE, obj.getString("act_user_name"));
						if (n_type != 3 && n_type != 4) {
							map.put(Constants.DATA, notification(obj.getString(Constants.EVENT_TITLE), n_type));
						} else {
							map.put(Constants.DATA, notification(Constants.NULL, n_type));
						}
						map.put(Constants.AUX_ID, obj.getString(Constants.ID));
						map.put(Constants.READ, obj.getString(Constants.READ));
						map.put(Constants.N_TYPE, ""+n_type);
						break;
					default:
						map.put(Constants.ID, obj.getString(key[0]));
						map.put(Constants.TITLE, obj.getString(key[1]));
						map.put(Constants.DATA, obj.getString(key[2]));
						map.put(Constants.AUX_ID, Constants.SUCCESS);
						map.put(Constants.READ, "true");
						break;
				}
				tData.add(map);
			}
		} catch (JSONException e){
				e.printStackTrace();
//			    Toast.makeText(getActivity().getApplicationContext(),
//				"Error: " + e.getMessage(),
//			Toast.LENGTH_LONG).show();
		}
		return tData;
	}

	public boolean checkId(){
		return (mId.isEmpty()
				|| mId.equals(Constants.PENDING_CLAIMS)
				|| mId.equals(Constants.CONFIRMED)
				|| mId.equals(Constants.MY_EVENTS)
				|| mId.equals(Constants.PENDING_EVENTS));
	}

	public void loader(){
		Log.d(TAG, "Preloader: "+mId);
		String page;
		page = checkId() ? "?page=" : "&page=";
		String url = SchoolBusiness.getTarget() + mModel + mDelim + mId + page + mPage;
		Log.d(TAG, url);
		RequestQueue queue = NetworkVolley.getInstance(getActivity().getApplicationContext())
				.getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						if(SchoolBusiness.DEBUG){Log.d("JSON", "Response: " + response.toString());}
//						List<Map<String, String>> newData = processJSON(response, mModel);
						mData.addAll(processJSON(response, mModel));
						mAdapter.notifyDataSetChanged();
						mPage += 1;
						mLock = false;
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error){
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				Toast.makeText(getActivity().getApplicationContext(),
						error.getMessage(), Toast.LENGTH_LONG).show();
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
				return params;
			}
		};
		queue.add(jsonRequest);
	}
}
