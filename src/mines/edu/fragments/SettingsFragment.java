/**
 * Description: Shows the preferences xml.
 * Allows for configurable user settings.
 * Currently only accuracy
 * Future settings could include metric or english, satellite or street maps, and ...
 * 
 * @authors Michael Patterson, Thomas Powell
 */


package mines.edu.fragments;

import mines.edu.patterson_powell_trailtracker.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
