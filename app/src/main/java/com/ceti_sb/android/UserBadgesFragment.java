package com.ceti_sb.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserBadgesFragment.UserBadgesListener} interface
 * to handle interaction events.
 * Use the {@link UserBadgesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBadgesFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String str_response;
	private String user_id;
	private String mParam2;

	private UserBadgesListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * #@param param2 Parameter 2.
	 * @return A new instance of fragment UserBadgesFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static UserBadgesFragment newInstance(String param1) {
		UserBadgesFragment fragment = new UserBadgesFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public UserBadgesFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			str_response = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_user_badges, container, false);
		render(view);
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
			mListener = (UserBadgesListener) activity;
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

	public void onClick(View view){
		String badge_id = (String) view.getTag();
		mListener.selectBadge(user_id, badge_id);
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
	public interface UserBadgesListener {
		// TODO: Update argument type and name
		//public void onBadgesLoad(View view, Map<Integer, String> badges);
		public void selectBadge(String user_id, String badge_id);
	}

	public void render(View view){
		try {
			JSONObject response = new JSONObject(str_response);
			user_id = response.getJSONObject("user").getString("id");
			JSONArray badges = response.getJSONArray("badges");
			HashMap<Integer, String> bmap = new HashMap<>();
			GridLayout display = (GridLayout) view.findViewById(R.id.badge_display);
			ImageLoader imageLoader = NetworkVolley.getInstance(getActivity()
					.getApplicationContext()).getImageLoader();
			for (int i = 0; i < badges.length(); i++){
				BadgeImageView badgeView = new BadgeImageView(getActivity());
				JSONObject badge = badges.getJSONObject(i);
				display.addView(badgeView, i);
				badgeView.setImageUrl(SchoolBusiness.AWS_S3 + badge.getString("badge_url"), imageLoader);
				badgeView.getLayoutParams().height = 96;
				badgeView.getLayoutParams().width = 96;
				badgeView.setClickable(true);
				badgeView.setTag(badge.getString("badge_id"));
				badgeView.setOnClickListener(this);
				//bmap.put(resource[i], badges.getString(i));
			}
			//mListener.onBadgesLoad(view, bmap);
		} catch (JSONException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
}