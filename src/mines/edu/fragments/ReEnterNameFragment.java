package mines.edu.fragments;


import mines.edu.activities.MainActivity;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ReEnterNameFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String message = "";
		Bundle args = getArguments();
		if (args != null) {
			message = args.getString("message");
		}

		return new AlertDialog.Builder(getActivity())
		.setMessage(message)
		.setPositiveButton(R.string.dialog_dismiss,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				((MainActivity)getActivity()).startNew();
				dismiss();
			}
		}
				)
				.create();

	}
}