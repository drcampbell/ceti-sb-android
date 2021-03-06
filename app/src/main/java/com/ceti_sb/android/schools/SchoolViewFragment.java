package com.ceti_sb.android.schools;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SchoolViewFragment.OnSchoolViewInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SchoolViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchoolViewFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String JSON_RESPONSE = "response";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	private String id;


	private OnSchoolViewInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @return A new instance of fragment SchoolViewFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static SchoolViewFragment newInstance(JSONObject param1) {
		SchoolViewFragment fragment = new SchoolViewFragment();
		Bundle args = new Bundle();
		args.putString(JSON_RESPONSE, param1.toString());
		fragment.setArguments(args);
		return fragment;
	}

	public SchoolViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			//mParam1 = getArguments().getString(JSON_RESPONSE);
			//mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_school_view, container, false);
		renderSchool(view, getArguments().getString(JSON_RESPONSE));
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onSchoolViewInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSchoolViewInteractionListener) activity;
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
			case R.id.make_my_school_button:
				mListener.makeMySchool(id);
                SchoolBusiness.setSchool(id);
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
	public interface OnSchoolViewInteractionListener {
		// TODO: Update argument type and name
		public void createSchoolFeed(View view, JSONObject feed, String id);
		public void makeMySchool(String id);
	}

	public void renderSchool(View view, String str_response){
		TextView mTextView;
		try {
			JSONObject response = new JSONObject(str_response);
			JSONObject school = response.getJSONObject("school");
			id = school.getString(Constants.ID);
			 String schoolId = SchoolBusiness.getSchool();
			/* Check to make sure the school is selectable as a user's school */
			if (id.equals("1") || id.equals(Constants.NULL) || !id.equals(schoolId)){
				view.findViewById(R.id.make_my_school_button).setClickable(true);
				view.findViewById(R.id.make_my_school_button).setOnClickListener(this);

			} else {
				view.findViewById(R.id.make_my_school_button).setVisibility(View.INVISIBLE);
			}
			int[] res = {R.id.tv_name,R.id.tv_address,R.id.tv_city, R.id.tv_state, R.id.tv_zip, R.id.tv_phone};
			String[] get_str = {Constants.NAME, "address", "city", "state", "zip", "phone"};
			for(int i = 0; i < get_str.length; i++) {
				mTextView = (TextView) view.findViewById(res[i]);
				if (get_str[i].equals("phone")) {
					mTextView.setText(SchoolBusiness.phoneNumber(school.getString(get_str[i])));
				} else {
					mTextView.setText(SchoolBusiness.toDisplayCase(school.getString(get_str[i])));
				}
			}
			mListener.createSchoolFeed(view, response, id);
		} catch (JSONException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
}
