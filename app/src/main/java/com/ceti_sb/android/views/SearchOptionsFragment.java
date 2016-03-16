package com.ceti_sb.android.views;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchOptionsFragment.OnSearchInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchOptionsFragment extends Fragment implements View.OnClickListener {

	private OnSearchInteractionListener mListener;

	public static SearchOptionsFragment newInstance(String param1, String param2) {
		SearchOptionsFragment fragment = new SearchOptionsFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public SearchOptionsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_search_options, container, false);
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onSearchInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSearchInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSearchInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public void onClick(View view){

	}

	public void onCheckboxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();

		switch (view.getId()){
			case R.id.events_checkBox:
				if (checked){
					mListener.onSearchInteraction(Constants.EVENTS);
					((CheckBox) view.findViewById(R.id.schools_checkBox)).setChecked(false);
					((CheckBox) view.findViewById(R.id.user_checkBox)).setChecked(false);
				}
				break;
			case R.id.schools_checkBox:
				if (checked) {
					mListener.onSearchInteraction("schools");
					((CheckBox) view.findViewById(R.id.events_checkBox)).setChecked(false);
					((CheckBox) view.findViewById(R.id.user_checkBox)).setChecked(false);
				}
				break;
			case R.id.user_checkBox:
				if (checked) {
					mListener.onSearchInteraction("users");
					((CheckBox) view.findViewById(R.id.schools_checkBox)).setChecked(false);
					((CheckBox) view.findViewById(R.id.events_checkBox)).setChecked(false);
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
	public interface OnSearchInteractionListener {
		// TODO: Update argument type and name
		public void onSearchInteraction(String model);
	}

	/*
	Default Android Code for Fragments
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private String mParam1;
	private String mParam2;

	*/

}
