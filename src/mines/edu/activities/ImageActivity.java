package mines.edu.activities;

import mines.edu.patterson_powell_trailtracker.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		Intent intent = getIntent();
		byte[] picture = intent.getByteArrayExtra("image");
		
		ImageView image = (ImageView) findViewById(R.id.current_image);
		image.setImageBitmap(BitmapFactory.decodeByteArray(picture, 0, picture.length));
	}

}
