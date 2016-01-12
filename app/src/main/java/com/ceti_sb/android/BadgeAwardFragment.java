package com.ceti_sb.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BadgeAwardFragment.AwardBadgeListener} interface
 * to handle interaction events.
 * Use the {@link BadgeAwardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BadgeAwardFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private static final String ARG_PARAM3 = "param3";
	// TODO: Rename and change types of parameters
	private String event_name;
	private String speaker_name;
	private int event_id;
	private String badge_url;
	private AwardBadgeListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * *@param event_name Parameter 1.
	 * *@param speaker_name Parameter 2.
	 * @return A new instance of fragment AwardBadgeFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static BadgeAwardFragment newInstance(Bundle args) {
		BadgeAwardFragment fragment = new BadgeAwardFragment();
//		Bundle args = new Bundle();
//		args.putString(ARG_PARAM1, event_name);
//		args.putString(ARG_PARAM2, speaker_name);
//		args.putString(ARG_PARAM3, event_id);
		fragment.setArguments(args);
		return fragment;
	}

	public BadgeAwardFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			event_name = getArguments().getString("event_name");
			speaker_name = getArguments().getString("speaker_name");
			event_id = Integer.parseInt(getArguments().getString(Constants.EVENT_ID));
			badge_url = getArguments().getString("badge_url");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_badge_award, container, false);
		((TextView) view.findViewById(R.id.tv_event)).setText(event_name);
		((TextView) view.findViewById(R.id.tv_name)).setText(speaker_name);
		LinearLayout display = (LinearLayout) view.findViewById(R.id.badge_display);
		ImageLoader imageLoader = NetworkVolley.getInstance(getActivity()
				.getApplicationContext()).getImageLoader();
		BadgeImageView badge = new BadgeImageView(getActivity().getApplicationContext());
		display.addView(badge);
		String badge_str = SchoolBusiness.AWS_S3 + badge_url;
		badge.setImageUrl(badge_str, imageLoader);
		badge.getLayoutParams().height = 512;
		badge.getLayoutParams().width = 512;
		view.findViewById(R.id.award_badge_button).setOnClickListener(BadgeAwardFragment.this);
		view.findViewById(R.id.reject_award_button).setOnClickListener(BadgeAwardFragment.this);
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
			mListener = (AwardBadgeListener) activity;
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
			case R.id.award_badge_button:
				mListener.awardBadge(true, event_id);
				break;
			case R.id.reject_award_button:
				mListener.awardBadge(false, event_id);
				break;
			default:
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
	public interface AwardBadgeListener {
		// TODO: Update argument type and name
		public void awardBadge(Boolean award, int event_id);
	}

}
