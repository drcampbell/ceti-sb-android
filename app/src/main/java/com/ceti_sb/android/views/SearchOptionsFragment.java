package com.ceti_sb.android.views;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import android.content.Context;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchOptionsFragment.OnSearchInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchOptionsFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "MainActivity";

	private OnSearchInteractionListener mListener;
    private int searchType = 0;

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
                    searchType = 1;
				}
				break;
			case R.id.schools_checkBox:
				if (checked) {
					mListener.onSearchInteraction("schools");
					((CheckBox) view.findViewById(R.id.events_checkBox)).setChecked(false);
					((CheckBox) view.findViewById(R.id.user_checkBox)).setChecked(false);
                    searchType = 2;
				}
				break;
			case R.id.user_checkBox:
				if (checked) {
					mListener.onSearchInteraction("users");
					((CheckBox) view.findViewById(R.id.schools_checkBox)).setChecked(false);
					((CheckBox) view.findViewById(R.id.events_checkBox)).setChecked(false);
                    searchType = 3;
				}
				break;
		}
	}



    public void onSearchClick(View view){
        String query = null;
        EditText cmpSearchTxt = (EditText) view.findViewById(R.id.searchText);
        String searchText = cmpSearchTxt.getText().toString();
        String zip = ((EditText) view.findViewById(R.id.txtZip)).getText().toString();
        String radius = ((EditText) view.findViewById(R.id.txtRadius)).getText().toString();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View viewBtn = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(viewBtn.getWindowToken(), 0);
        if(searchType == 2){

            if((searchText) != null && !searchText.isEmpty()){
                //Search with zip and radius
                if((zip) != null && !zip.isEmpty()
                    && (radius) != null & !radius.isEmpty() ){

                    query = "schools/near_me?zip=" + zip + "&radius=" + radius +
                             "&commit=Near+Me&search=" + searchText;

                }
                else{
                    // Only search
                    //gBtnRadioValue = "events"
                    query = "schools?search=" + searchText;
                }
            }
            else{
                if((zip) != null && !zip.isEmpty()
                        && (radius) != null & !radius.isEmpty() ){

                    query = "schools?zip=" + zip + "&radius=" + radius +
                            "&search=" + searchText +
                            "&location=true";

                }
            }
        }
        else if(searchType == 1){


            if((searchText) != null && !searchText.isEmpty()){
                //Search with zip and radius

                if((zip) != null && !zip.isEmpty()
                        && (radius) != null & !radius.isEmpty() ){

                    query =  "events?zip=" + zip + "&radius=" + radius
                            + "&location=true&commit=Near+Me&search=" + searchText;


                }else{
                    // Only search
                    query =  "events?search=" + searchText;

                }

            }
            else{
                query =  "events?zip=" + zip + "&radius=" + radius + "&location=true";

            }

        }
        else{
            if((searchText) != null && !searchText.isEmpty()) {
                query = "users?search=" + searchText;
            }
        }
        Log.d(TAG, "Sending search request: query=" + query);

        mListener.onLocationSearchInteraction(query);
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
        public void onLocationSearchInteraction(String query);

	}

	/*
	Default Android Code for Fragments
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private String mParam1;
	private String mParam2;

	*/

}
