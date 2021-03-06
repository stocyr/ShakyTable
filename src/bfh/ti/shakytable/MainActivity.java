package bfh.ti.shakytable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private TableView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		tv = new TableView(this);
		tv.setBackgroundColor(0xFF4F4F57);

		setContentView(tv);
	}

	@Override
	public void onStop() {
		Log.d("mainActivity", "onStop trigger");
		tv.onStop();
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		tv.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onRestart() {
		Log.d("mainActivity", "onRestart trigger");
		super.onRestart();
		tv.onRestart();
	}
}