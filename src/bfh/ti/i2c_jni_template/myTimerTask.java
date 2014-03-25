package bfh.ti.i2c_jni_template;

import java.util.TimerTask;
public class myTimerTask extends TimerTask {
	MainI2cActivity activity;
	
	public myTimerTask(MainI2cActivity _activity){
		activity = _activity;
	}
	@Override
	public void run() {
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				/*
				 * Update 
				 */
				activity.update();
			}
		});
		
	}


}


