/**
 * Description: A basic fragment dialog to display a message provided by the intent
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.fragments;

import mines.edu.patterson_powell_trailtracker.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MessageFragment extends DialogFragment {
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
				dismiss();
			}
		}
				)
				.create();

	}
}
