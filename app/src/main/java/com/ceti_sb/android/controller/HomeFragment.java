package com.ceti_sb.android.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;

import org.json.JSONObject;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragment.onWelcomeInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{//, FragmentTabHost.OnTabChangeListener
	private int tab;
//	private FragmentTabHost tabHost;

	private OnWelcomeInteractionListener mListener;

	public static HomeFragment newInstance(String param1, String param2) {
		HomeFragment fragment = new HomeFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public HomeFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
		}
		tab = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		if (SchoolBusiness.getRole().equals("Teacher") || SchoolBusiness.getRole().equals("Both")) {
			((Button) view.findViewById(R.id.create_event)).setOnClickListener(HomeFragment.this);
		} else {
			((Button) view.findViewById(R.id.create_event)).setVisibility(View.INVISIBLE);
		}
		mListener.onCreateTab(tab);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnWelcomeInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onWelcomeInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		mListener = null;
		super.onDetach();
	}

	@Override
	public void onClick(View view){
		switch (view.getId()){
			case R.id.create_event:
				JSONObject profile = SchoolBusiness.getProfile();
				if (profile != null) {
					String school = SchoolBusiness.getSchool();
					if(school == null || school.equals("Please Select A School") ){
						mListener.handleSchoolMissing();
						return ;
					}
				}
				mListener.onCreateEvent(false, Constants.NULL);
				break;
		}
	}

	@Override
	public void onPause(){
		mListener.clearTabs();
		super.onPause();
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
	public interface OnWelcomeInteractionListener {
		// TODO: Update argument type and name
		public void onCreateEvent(Boolean edit, String event);
		public void onCreateTab(int tab);
		public void clearTabs();
		public void handleSchoolMissing();
	}

    /*

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onWelcomeInteraction(uri);
		}
	}

	// Default Android Code for Fragments
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private String mParam1;
	private String mParam2;

	*/
}
