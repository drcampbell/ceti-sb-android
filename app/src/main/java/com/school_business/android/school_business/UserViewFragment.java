package com.school_business.android.school_business;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.school_business.android.school_business.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

	// TODO: Rename and change types of parameters
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
	}

	public String get_id(int res){
		switch (res){
			case R.id.TV_School:
				return "school_id";
			default:
				return "";
		}
	}

	private void renderUser(View view, String str_response){
		try {
			ArrayList<TVAttributes> attrs = new ArrayList<TVAttributes>();
			JSONObject response = new JSONObject(str_response);
			String role = response.getString("role");
			String link = "";
			attrs.add(new TVAttributes(R.id.TV_Name, "", "name"));
			if (role.equals("Teacher") || role.equals("Both")) {
				attrs.add(new TVAttributes(R.id.TV_School, "School:", "school_name"));
				attrs.add(new TVAttributes(R.id.TV_Grades, "Grades:", "grades"));
			}
			if (role.equals("Speaker") || role.equals("Both")) {
				attrs.add(new TVAttributes(R.id.TV_Job, "Job Title:", "job_title"));
				attrs.add(new TVAttributes(R.id.TV_Business, "Business:", "business"));
			}
			attrs.add(new TVAttributes(R.id.TV_Role, "Role:", "role"));
			attrs.add(new TVAttributes(R.id.TV_Bio, "Biography:", "biography"));

			createText(view, R.id.layout_user, attrs.get(0).res, "", response.getString(attrs.get(0).getStr),
					true, android.R.style.TextAppearance_Large, true);
			for (int i = 1; i < attrs.size(); i++) {
				link = get_id(attrs.get(i).res);
				if (!link.equals("")) {
					link = response.getString(link);
				} else {
					link = "0";
				}
				createText(view, R.id.layout_user, attrs.get(i).res, link, attrs.get(i).label, true,
						android.R.style.TextAppearance_Medium, false);
				createText(view, R.id.layout_user, attrs.get(i).res, link,
						response.getString(attrs.get(i).getStr),
						false, android.R.style.TextAppearance_Medium, false);
			}
		} catch (JSONException e){
				e.printStackTrace();
				Toast.makeText(getActivity().getApplicationContext(),
						"Error: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
		}
	}
	private void createText(View view,int layout, int tv, String id, String message, Boolean bold, int size, Boolean center){
		TextView title = new TextView(getActivity());

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layout_fragment_user);
		title.setId(tv);
		title.setText(message);
		title.setTag(id);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		title.setLayoutParams(params);
		title.setTextAppearance(getActivity(), size);
		if (bold) {
			title.setTypeface(null, Typeface.BOLD);
		}
		if (center) {
			title.setGravity(Gravity.CENTER);
		}
		if (!id.equals("0")){
			title.setOnClickListener(UserViewFragment.this);
		}
		linearLayout.addView(title);
	}
}
