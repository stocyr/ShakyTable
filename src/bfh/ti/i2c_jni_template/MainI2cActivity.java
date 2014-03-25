/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        This sample program shows how to use the I2C library.
 *			The program reads the temperature from the MCP9802 sensor
 *			and show the value on the display  
 *
 *	        Only a minimal error handling is implemented.
 * \file    MainI2cActivity.java
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 ***************************************************************************
 */

package bfh.ti.i2c_jni_template;

import java.util.Timer;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Menu;
import android.widget.TextView;

public class MainI2cActivity extends Activity {
	/* Define widgets */
	TextView textViewTemperature;

	I2CTempSensor temp;
	Timer timer;
	myTimerTask myTimerT;
	Point acc;

	/* Temperature Degrees Celsius text symbol */
	private static final String DEGREE_SYMBOL = "\u2103";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_i2c);
		
		acc = new Point();
		
		temp = new I2CTempSensor();
		timer = new Timer();
		myTimerT = new myTimerTask(this);
		
 		timer.schedule(myTimerT, 0, 10);
		
		textViewTemperature = (TextView) findViewById(R.id.textViewTemperature);
		update();
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_i2c, menu);
		return true;
	}
	public void update(){
		try {
			acc = temp.getTemp();
		} catch (NullPointerException e) {
			textViewTemperature.setText("NullPointerExeption");
		}

		// TempC = 12;
		/* Display actual temperature */
		textViewTemperature.setText("X-Acceleration: "
				+ String.format("%3d", acc.x) +"\nY-Acceleration: " + String.format("%3d", acc.y));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	protected void onStop() {
		temp.close();
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}

		if (myTimerT != null) {
			myTimerT.cancel();
			myTimerT = null;
		}
		
		android.os.Process.killProcess(android.os.Process.myPid());
		finish();
		super.onStop();
	}
}


