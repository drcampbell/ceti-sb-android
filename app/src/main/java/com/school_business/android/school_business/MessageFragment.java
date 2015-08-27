package com.school_business.android.school_business;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.school_business.android.school_business.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnMessageListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String recipient_id;
	private String name;

	private OnMessageListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param id Parameter 1.
	 * @return A new instance of fragment MessageFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MessageFragment newInstance(String id, String name) {
		MessageFragment fragment = new MessageFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, id);
		args.putString(ARG_PARAM2, name);
		fragment.setArguments(args);
		return fragment;
	}

	public MessageFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			recipient_id = getArguments().getString(ARG_PARAM1);
			name = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_message, container, false);
		renderMessage(view);
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//mListener.onSendMessage(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnMessageListener) activity;
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
			case R.id.send_message_button:
				view.findViewById(R.id.send_message_button).setClickable(false);
				mListener.onSendMessage(recipient_id, getMessage(view));
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
	public interface OnMessageListener {
		// TODO: Update argument type and name
		public void onSendMessage(String id, JSONObject message);
	}

	public void renderMessage(View view){
		((TextView) view.findViewById(R.id.message_title)).setText("Send a Message to " + name);
		view.findViewById(R.id.send_message_button).setOnClickListener(this);
	}

	public JSONObject getMessage(View view){
		String text = ((EditText) getActivity().findViewById(R.id.message_content)).getText().toString();
		JSONObject message;
		try {
			message = new JSONObject();
			message.put("user_message", text);
		} catch (JSONException e) {
			message = null;
		}
		return message;
	}

}
