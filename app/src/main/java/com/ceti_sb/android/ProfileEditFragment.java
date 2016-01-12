package com.ceti_sb.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileEditFragment.OnProfileEditListener} interface
 * to handle interaction events.
 * Use the {@link ProfileEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileEditFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnProfileEditListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment ProfileEditFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ProfileEditFragment newInstance(String param1, String param2) {
		ProfileEditFragment fragment = new ProfileEditFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public ProfileEditFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
		renderProfile(view);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onSaveProfile(null);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnProfileEditListener) activity;
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
		switch (view.getId()) {
			case R.id.save_profile_button:
				mListener.onSaveProfile(collectProfile());
				break;
			case R.id.find_school_button:
				mListener.onFindMySchool();
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
	public interface OnProfileEditListener {
		// TODO: Update argument type and name
		public void onSaveProfile(JSONObject profile);
		public void onFindMySchool();
	}

	public void renderProfile(View view){
		((Button) view.findViewById(R.id.find_school_button)).setOnClickListener(ProfileEditFragment.this);
		((Button) view.findViewById(R.id.save_profile_button)).setOnClickListener(ProfileEditFragment.this);
		JSONObject profile = SchoolBusiness.getProfile();
		try {
			((TextView) view.findViewById(R.id.tv_name)).setText(profile.getString(Constants.NAME));
			((TextView) view.findViewById(R.id.tv_location)).setText(profile.getString("school_name"));
			((EditText) view.findViewById(R.id.et_grades)).setText(profile.getString("grades"));
			((EditText) view.findViewById(R.id.et_bio)).setText(profile.getString("biography"));
			((EditText) view.findViewById(R.id.et_business)).setText(profile.getString("business"));
			((EditText) view.findViewById(R.id.et_job)).setText(profile.getString("job_title"));

		} catch (JSONException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
	public JSONObject collectProfile(){
		try {
			JSONObject profile = SchoolBusiness.getProfile();

			// Rails handles roles in a stupid way.  (Takes as input alpha, provides numeric output)
			if (SchoolBusiness.isNumeric(profile.getString(Constants.ROLE))) {
				profile.put(Constants.ROLE, SchoolBusiness.translateRole(profile.getString(Constants.ROLE)));
			}
			profile.put("grades", ((EditText) getActivity().findViewById(R.id.et_grades)).getText());
			profile.put("biography", ((EditText) getActivity().findViewById(R.id.et_bio)).getText());
			profile.put("business", ((EditText) getActivity().findViewById(R.id.et_business)).getText());
			profile.put("job_title", ((EditText) getActivity().findViewById(R.id.et_job)).getText());
			return profile;
		} catch (JSONException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			return null;
		}
	}
}
