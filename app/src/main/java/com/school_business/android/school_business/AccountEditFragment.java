package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountEditFragment.OnEditAccountListener} interface
 * to handle interaction events.
 * Use the {@link AccountEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountEditFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	private String role;
	private OnEditAccountListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment AccountEditFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static AccountEditFragment newInstance(String param1, String param2) {
		AccountEditFragment fragment = new AccountEditFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public AccountEditFragment() {
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
		View view = inflater.inflate(R.layout.fragment_account_edit, container, false);
		renderAccount(view);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnEditAccountListener) activity;
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
		switch ( view.getId() ){
			case R.id.save_account_button:
				JSONObject account = collectAccount();
				mListener.onSaveAccount(account);
				break;
		}
	}

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
			case R.id.register_teacher:
				role = "Teacher";
				break;
			case R.id.register_speaker:
				role = "Speaker";
				break;
			case R.id.register_both:
				role = "Both";
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
	public interface OnEditAccountListener {
		// TODO: Update argument type and name
		public void onSaveAccount(JSONObject account);
	}
	public void renderAccount(View view){
		((Button) view.findViewById(R.id.save_account_button)).setOnClickListener(AccountEditFragment.this);
		// ((RadioButton)) view.findViewById(R.id.register_speaker)).
		String strprofile = SchoolBusiness.getProfile();
		try {
			JSONObject profile = new JSONObject(strprofile);
			((EditText) view.findViewById(R.id.et_name)).setText(profile.getString("name"));
			((EditText) view.findViewById(R.id.et_email)).setText(profile.getString("email"));
			Log.d("Test", strprofile);
			role = profile.getString("role");

			switch (role){
				case SchoolBusiness.TEACHER:
					((RadioButton) view.findViewById(R.id.register_teacher)).setChecked(true);
					break;
				case SchoolBusiness.SPEAKER:
					((RadioButton) view.findViewById(R.id.register_speaker)).setChecked(true);
					break;
				case SchoolBusiness.BOTH:
					((RadioButton) view.findViewById(R.id.register_both)).setChecked(true);
					break;
			}
		} catch (JSONException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
	public JSONObject collectAccount(){
		JSONObject account = new JSONObject();
		try {
			account.put("name", ((EditText) getActivity().findViewById(R.id.et_name)).getText());
			account.put("email", ((EditText) getActivity().findViewById(R.id.et_email)).getText());
			account.put("role", getRole(role));
			String new_password = ((EditText) getActivity().findViewById(R.id.new_password)).getText().toString();
			String confirm_password = ((EditText) getActivity().findViewById(R.id.confirm_password)).getText().toString();
			if (!new_password.isEmpty() && !confirm_password.isEmpty() && new_password.equals(confirm_password)){
				account.put("password", new_password);
				account.put("confirm_password", confirm_password);
			}
			account.put("current_password", ((EditText) getActivity().findViewById(R.id.password)).getText());
			JSONObject user = new JSONObject();
			user.put("user", account);
			return user;
		} catch (JSONException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			return null;
		}
	}
	public String getRole(String role){
		switch (role) {
			case SchoolBusiness.NONE:
				return "None";
			case SchoolBusiness.TEACHER:
				return "Teacher";
			case SchoolBusiness.SPEAKER:
				return "Speaker";
			case SchoolBusiness.BOTH:
				return "Both";
			default:
				return "";
		}
	}
}
