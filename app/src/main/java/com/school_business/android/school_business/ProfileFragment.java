package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.school_business.android.school_business.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mProfile;
	private String mParam2;

	private OnProfileInteractionListener mListener;

	/*
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment ProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ProfileFragment newInstance(JSONObject profile) {
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, profile.toString());
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mProfile = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		renderProfile(view);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onProfileInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnProfileInteractionListener) activity;
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
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.tv_school:
				TextView tv = (TextView) getActivity().findViewById(R.id.tv_school);
				mListener.onListItemSelected((String) tv.getTag(), "schools");
				break;
			case R.id.edit_account_button:
				mListener.onEditAccount();
				break;
			case R.id.edit_profile_button:
				mListener.onEditProfile();
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
	public interface OnProfileInteractionListener {
		// TODO: Update argument type and name
		public void onListItemSelected(String id, String model);
		public void onEditAccount();
		public void onEditProfile();
	}

	public void renderProfile(View view){
		String text;
		JSONObject profile;
		try {
			if (mProfile != null && !mProfile.isEmpty()) {
				profile = new JSONObject(mProfile);
			} else {
				if (!SchoolBusiness.getProfile().isEmpty()) {
					profile = new JSONObject(SchoolBusiness.getProfile());
				} else {
					return;
				}
			}
			int[] resource = {R.id.tv_name, R.id.tv_email, R.id.tv_school, R.id.tv_bio, R.id.tv_business, R.id.tv_job};
			String[] id = {"name", "email", "school_name", "biography", "business", "job_title"};
			((Button) view.findViewById(R.id.edit_profile_button)).setOnClickListener(ProfileFragment.this);
			((Button) view.findViewById(R.id.edit_account_button)).setOnClickListener(ProfileFragment.this);
			for (int i = 0; i < resource.length; i++){
					 text = SchoolBusiness.toDisplayCase(profile.getString(id[i]));
				((TextView) view.findViewById(resource[i])).setText(text);
				if (id[i].equals("school_name")){
					view.findViewById(resource[i]).setClickable(true);
					view.findViewById(resource[i]).setTag(profile.getString("school_id"));
					((TextView) view.findViewById(resource[i])).setTextColor(getResources()
							.getColor(android.R.color.holo_blue_dark));
				}
			}
			switch (profile.getString("role")) {
				case SchoolBusiness.NONE:
					((TextView) view.findViewById(R.id.tv_role)).setText("None");
					break;
				case SchoolBusiness.TEACHER:
					((TextView) view.findViewById(R.id.tv_role)).setText("Teacher");
					break;
				case SchoolBusiness.SPEAKER:
					((TextView) view.findViewById(R.id.tv_role)).setText("Speaker");
					break;
				case SchoolBusiness.BOTH:
					((TextView) view.findViewById(R.id.tv_role)).setText("Both");
					break;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}
}
