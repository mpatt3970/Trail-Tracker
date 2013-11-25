package mines.edu.fragments;



import mines.edu.activities.MainActivity;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;


// Thank you to DialogFragmentDemo
public class NameFragment extends DialogFragment {
	/**
	 * Takes 3(or 2) arguments: dialogID is a unique number,
	 * edit defines if this dialog is editing an existing name or creating a new one,
	 * prompt is the initial text of the input field
	 */
	
	public static final String PREF = "MyPrefsFile"; //filename for the shared preferences file
	private SharedPreferences settings;
	
	private EditText input;
	private int dialogID;
	private String prompt;
	private boolean edit;
	private String confirm;

	// had to make it public since it's used outside the package. I don't know why protected wasn't making it visible, but it wasn't
	public interface Listener {
		void onInputDone(int dialogID, String input, boolean edit);
		void onInputCancel(int dialogID);
	}



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Set default values.
		this.dialogID = -1;
		this.prompt = getString(R.string.name_prompt_default);
		this.edit = false;

		//get provided arguments, if any
		Bundle args = getArguments();
		if (args != null) {
			this.dialogID = args.getInt("dialogID");
			this.edit = args.getBoolean("edit");
			if (edit) {
				this.prompt = args.getString("prompt");
			}
		}
		// Create an input field.
		input = new EditText( getActivity() );
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		// set text for edit when the name is passed in for prompt, set hint if not edit
		if (!edit) {
			input.setHint(prompt);
			confirm = getResources().getString(R.string.start);
		} else {
			input.setText(prompt);
			confirm = getResources().getString(R.string.save_name);
		}
		return new AlertDialog.Builder(getActivity())
		.setTitle(R.string.app_name)
		.setPositiveButton(confirm,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(((MainActivity)getActivity()).checkName(input.getText().toString())) {
					((NameFragment.Listener)getActivity()).onInputDone(dialogID, input.getText().toString(), edit);
				} else {
					((MainActivity)getActivity()).showMessage("Name is the same as another Trail. Please enter a unique name.");
					
					/*getActivity().runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				            Toast.makeText(getActivity(), "Name is the same as another Trail. Please enter a unique name.", Toast.LENGTH_SHORT).show();
				        }
				    });*/				}
			}
		}
				)
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((NameFragment.Listener)getActivity()).onInputCancel(dialogID);
					}
				}
						)
						.setView(input)
						.create();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
}
