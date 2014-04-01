package bfh.ti.shakytable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.View;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TableView extends View {

	private int window_width, window_height;
	private ScheduledExecutorService scheduleTaskExecutor;
	private Ball ball;

	private Point acceleration = new Point();

	final static float led_velocity_sensivity = 40;
	final static float frequency = 50f;

	private I2CMotionSensor motion;

	private SysfsFileGPIO led1;
	private SysfsFileGPIO led2;
	private SysfsFileGPIO led3;
	private SysfsFileGPIO led4;

	private SysfsFileGPIO button1;
	private SysfsFileGPIO button2;
	private SysfsFileGPIO button3;
	private SysfsFileGPIO button4;

	public TableView(Context context) {
		super(context);

		// initialize LEDs
		led1 = new SysfsFileGPIO(SysfsFileGPIO.LED_L1);
		led2 = new SysfsFileGPIO(SysfsFileGPIO.LED_L2);
		led3 = new SysfsFileGPIO(SysfsFileGPIO.LED_L3);
		led4 = new SysfsFileGPIO(SysfsFileGPIO.LED_L4);

		led1.set_direction_out();
		led2.set_direction_out();
		led3.set_direction_out();
		led4.set_direction_out();

		// initialize Buttons
		button1 = new SysfsFileGPIO(SysfsFileGPIO.BUTTON_T1);
		button2 = new SysfsFileGPIO(SysfsFileGPIO.BUTTON_T2);
		button3 = new SysfsFileGPIO(SysfsFileGPIO.BUTTON_T3);
		button4 = new SysfsFileGPIO(SysfsFileGPIO.BUTTON_T4);

		button1.set_direction_in();
		button2.set_direction_in();
		button3.set_direction_in();
		button4.set_direction_in();

		// initialize I2C
		motion = new I2CMotionSensor();
	}

	public void onStop() {
		scheduleTaskExecutor.shutdown();
		try {
			scheduleTaskExecutor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		led1.write_value(1);
		led2.write_value(1);
		led3.write_value(1);
		led4.write_value(1);
		// led1.close();
		// led2.close();
		// led3.close();
		// led4.close();
		// button1.close();
		// button2.close();
		// button3.close();
		// button4.close();
		motion.close();
		Log.d("view", "onStop done");
	}

	public void onRestart() {
		start_scheduler();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		window_width = MeasureSpec.getSize(widthMeasureSpec);
		window_height = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(window_width, window_height);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// first call of onMeasure?
		if (ball == null) {
			init_ball();
		}
	}

	private void init_ball() {
		ball = new Ball(frequency);

		// since we only get the parents view dimension by the first call of
		// "onMeasure",
		// we weren't able to set the balls size yet:
		ball.radius = Ball.relative_size
				* Math.max(window_height, window_width);

		// And also we weren't able to set the balls initial position to the
		// center:
		ball.set_position(window_width / 2, window_height / 2);

		start_scheduler();
	}

	private void start_scheduler() {
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				acceleration = motion.getTemp();
				Log.d("new acceleration", "ax = " + acceleration.x + "\tay = "
						+ acceleration.y);
				handle_buttons(); // --> causes the GC to execute every ~ 50ms
				handle_leds(); // --> causes the GC to execute every ~ 250ms
				trigger_physics_engine(acceleration.x - 2, acceleration.y);
			}
		}, 0, (int) (1000 / frequency), TimeUnit.MILLISECONDS);
	}

	public void handle_buttons() {
		// process buttons - choose between different predefined masses
		// beware: the buttons are wired low-active!
		if (button1.read_value() == 0)
			ball.set_mass(Ball.masses[0]);
		else if (button2.read_value() == 0)
			ball.set_mass(Ball.masses[1]);
		else if (button3.read_value() == 0)
			ball.set_mass(Ball.masses[2]);
		else if (button4.read_value() == 0)
			ball.set_mass(Ball.masses[3]);
	}

	public void handle_leds() {
		// process leds - show absolute velocity
		// beware: the leds are wired low-active!
		float abs_velocity = (float) Math.sqrt(ball.vx * ball.vx + ball.vy
				* ball.vy);
		if (abs_velocity > led_velocity_sensivity * 9f) {
			led1.write_value(0);
			led2.write_value(0);
			led3.write_value(0);
			led4.write_value(0);
		} else if (abs_velocity > led_velocity_sensivity * 6f) {
			led1.write_value(1);
			led2.write_value(0);
			led3.write_value(0);
			led4.write_value(0);
		} else if (abs_velocity > led_velocity_sensivity * 3f) {
			led1.write_value(1);
			led2.write_value(1);
			led3.write_value(0);
			led4.write_value(0);
		} else if (abs_velocity > led_velocity_sensivity * 1f) {
			led1.write_value(1);
			led2.write_value(1);
			led3.write_value(1);
			led4.write_value(0);
		} else {
			led1.write_value(1);
			led2.write_value(1);
			led3.write_value(1);
			led4.write_value(1);
		}
	}

	public void trigger_physics_engine(float x, float y) {
		// calculate new position
		ball.set_acceleration(x, y, window_width, window_height);

		// tell view to redraw
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		ball.paint_ball(canvas);
	}
}