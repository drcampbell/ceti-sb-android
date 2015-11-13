package com.ceti_sb.android;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventProfileFragment.OnEventTabListener} interface
 * to handle interaction events.
 * Use the {@link EventProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {
	private FragmentTabHost mTabHost;
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_TAB = "param1";
	private static final String ARG_NAMES = "param2";
	private static final String ARG_DISPLAY = "param3";

	// TODO: Rename and change types of parameters
	private int tab;
	private String mParam2;
	private String[] tab_names;// = {"all", "approval", "claims", "confirmed"};
	private String[] tab_display;
	private OnEventTabListener mListener;
	private FragmentTabHost.OnTabChangeListener mTabListener;
	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param tab Parameter 1.
	 * @return A new instance of fragment EventProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static TabFragment newInstance(int tab, String[] tab_names, String[] tab_display) {
		TabFragment fragment = new TabFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TAB, tab);
		args.putStringArray(ARG_NAMES, tab_names);
		args.putStringArray(ARG_DISPLAY, tab_display);
		fragment.setArguments(args);
		return fragment;
	}

	public TabFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			tab = getArguments().getInt(ARG_TAB);
			tab_names = getArguments().getStringArray(ARG_NAMES);
			tab_display = getArguments().getStringArray(ARG_DISPLAY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		//View view = inflater.inflate(R.layout.fragment_home, container, false);
		mTabHost = new FragmentTabHost(getActivity());
		mTabHost.setup(getActivity(), getChildFragmentManager(),R.id.fragment_container);

		for (int i = 0; i < tab_names.length; i++){
			mTabHost.addTab(mTabHost.newTabSpec(tab_names[i]).setIndicator(tab_display[i]),
					BlankFragment.class, null);
		}
//		mTabHost.addTab(mTabHost.newTabSpec("all").setIndicator("All"),
//				BlankFragment.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec("approval").setIndicator("Approvals"),
//				BlankFragment.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec("claims").setIndicator("Claims"),
//				BlankFragment.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec("confirmed").setIndicator("Confirmed"),
//				BlankFragment.class, null);

		for (int i=0; i < tab_names.length; i++){
			TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			tv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Small);
			tv.setAllCaps(false);
			tv.setTextSize(12);
		}
		//mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.ta)
		mTabHost.setOnTabChangedListener(mTabListener);
		mTabHost.setCurrentTab(tab);
		mTabListener.onTabChanged(tab_names[tab]);
		return mTabHost;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTabHost = null;
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
			mListener = (OnEventTabListener) activity;
			mTabListener = (TabHost.OnTabChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mTabListener = null;
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
	public interface OnEventTabListener {
		// TODO: Update argument type and name
		public void onEventTabSelected(String string);
	}
}
