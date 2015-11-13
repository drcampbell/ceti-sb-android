package com.ceti_sb.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserViewFragment.OnUserViewInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserViewFragment extends Fragment implements View.OnClickListener{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String JSON_RESPONSE = "response";
	private static final String ARG_PARAM2 = "param2";

	private String user_id;
	private String name;
	private String mParam1;
	private String mParam2;
	private OnUserViewInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @return A new instance of fragment UserViewFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static UserViewFragment newInstance(JSONObject param1) {
		UserViewFragment fragment = new UserViewFragment();
		Bundle args = new Bundle();
		args.putString(JSON_RESPONSE, param1.toString());
		fragment.setArguments(args);
		return fragment;
	}

	public UserViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(JSON_RESPONSE);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_user_view, container, false);
		if (getArguments() != null) {
			renderUser(view, getArguments().getString(JSON_RESPONSE));
		}
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onUserViewInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnUserViewInteractionListener) activity;
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
	public void onClick(View view){
		switch (view.getId()){
			case R.id.tv_location:
				mListener.onUserViewInteraction((String) view.findViewById(R.id.tv_location).getTag(), "schools");
				break;
			case R.id.contact_user_button:
				mListener.onContactUser(user_id, name);
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
	public interface OnUserViewInteractionListener {
		// TODO: Update argument type and name
		public void onUserViewInteraction(String id, String model);
		public void createUserFeed(JSONObject response);
		public void onContactUser(String id, String name);
	}

	private void renderUser(View view, String str_response){
		try {
			JSONObject response = new JSONObject(str_response);
			String str;
			String link = "";
			JSONObject user = response.getJSONObject("user");
			user_id = user.getString("id");
			name = user.getString("name");
			TextView tv;
			if (user_id.equals(SchoolBusiness.getUserAttr("id"))) {
				view.findViewById(R.id.contact_user_button).setVisibility(View.GONE);
			} else {
				view.findViewById(R.id.contact_user_button).setOnClickListener(this);
			}
			tv = (TextView) view.findViewById(R.id.tv_name);
			str = SchoolBusiness.toDisplayCase(name);
			tv.setTag(link);
			tv.setText(str);
			mListener.createUserFeed(response);
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

//	private void renderUser(View view, String str_response){
//		try {
//			JSONObject response = new JSONObject(str_response);
//			String str;
//			String link = "";
//			JSONObject user = response.getJSONObject("user");
//			user_id = user.getString("id");
//			name = user.getString("name");
//			int[] resource = {R.id.tv_name, R.id.tv_location, R.id.tv_grades,R.id.tv_job,R.id.tv_business,R.id.tv_role,R.id.tv_bio};
//			String[] name = {"name", "school_name", "grades", "job_title", "business", "role", "biography"};
//			TextView tv;
//			if (user_id.equals(SchoolBusiness.getUserAttr("id"))) {
//				view.findViewById(R.id.contact_user_button).setVisibility(View.GONE);
//			} else {
//				view.findViewById(R.id.contact_user_button).setOnClickListener(this);
//			}
//			if (user.getString("role").equals("Teacher")) {
//				((LinearLayout) view.findViewById(R.id.layout_user_business)).setVisibility(View.GONE);
//			} else if (user.getString("role").equals("Speaker")) {
//				((LinearLayout) view.findViewById(R.id.layout_user_teacher)).setVisibility(View.GONE);
//			}
//			for (int i = 0; i < resource.length; i++) {
//				tv = (TextView) view.findViewById(resource[i]);
//
//				link = get_id(resource[i]);
//				if (!link.equals("")) {
//					link = user.getString(link);
//					tv.setOnClickListener(UserViewFragment.this);
//					tv.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
//				} else {
//					link = "0";
//				}
//				str = name[i];
//				str = SchoolBusiness.toDisplayCase(user.getString(str));
//				tv.setTag(link);
//				tv.setText(str);
//			}
//			mListener.createUserFeed(response);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			Toast.makeText(getActivity().getApplicationContext(),
//					"Error: " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
//		}
//	}
}
