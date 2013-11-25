/**
 * Description: A very basic activity to hold the settingsFragment
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.activities;

import mines.edu.fragments.SettingsFragment;
import android.app.Activity;
import android.os.Bundle;


public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

}
