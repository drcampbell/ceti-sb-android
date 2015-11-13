package com.ceti_sb.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by david on 6/28/15.
 */
public class DeleteEventDialogFragment extends DialogFragment {

	public interface DeleteEventDialogListener {
		public void onDeleteEventPositiveClick(DialogFragment dialog);
		public void onDeleteEventNegativeClick(DialogFragment dialog);
	}

	DeleteEventDialogListener mListener;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			mListener = (DeleteEventDialogListener) activity;
		} catch (ClassCastException e){
			throw new ClassCastException(activity.toString()+" must implement ConfirmDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.delete_event)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						mListener.onDeleteEventPositiveClick(DeleteEventDialogFragment.this);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						mListener.onDeleteEventNegativeClick(DeleteEventDialogFragment.this);
					}
				});
		return builder.create();
	}
}