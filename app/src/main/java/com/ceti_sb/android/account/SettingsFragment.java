package com.ceti_sb.android.account;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ceti_sb.android.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnSettingsListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnSettingsListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param response Parameter 1.

	 * @return A new instance of fragment SettingsFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static SettingsFragment newInstance(JSONObject response) {
		SettingsFragment fragment = new SettingsFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, response.toString());
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public SettingsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			//mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		renderSettings(view);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onSaveSettings(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSettingsListener) activity;
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

	public void onClick(View view) {
		switch (view.getId()){
			case R.id.save_settings_button:
				mListener.onSaveSettings(getSettings(view));
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
	public interface OnSettingsListener {
		// TODO: Update argument type and name
		public void onSaveSettings(JSONObject settings);
	}

	public JSONObject getSettings(View view){
		JSONObject settings;
		try {
			JSONObject user = new JSONObject();
			settings = new JSONObject();
			user.put("set_updates", ((CheckBox) getActivity().findViewById(R.id.set_event_updates)).isChecked());
			user.put("set_confirm", ((CheckBox) getActivity().findViewById(R.id.set_confirmations)).isChecked());
			user.put("set_claims", ((CheckBox) getActivity().findViewById(R.id.set_event_claims)).isChecked());
			settings.put ("user", user);
		} catch (JSONException e){
			Log.d("Settings", e.toString());
			settings = null;
		}
		return settings;
	}

	public void renderSettings(View view){
		view.findViewById(R.id.save_settings_button).setOnClickListener(SettingsFragment.this);
		try {
			Log.d("Settings", mParam1);
			JSONObject settings = new JSONObject(mParam1);
			((CheckBox) view.findViewById(R.id.set_event_updates)).setChecked(settings.getBoolean("set_updates"));
			((CheckBox) view.findViewById(R.id.set_confirmations)).setChecked(settings.getBoolean("set_confirm"));
			((CheckBox) view.findViewById(R.id.set_event_claims)).setChecked(settings.getBoolean("set_claims"));
		} catch (JSONException e){
			Log.d("Settings", e.toString());
		}
	}
}
