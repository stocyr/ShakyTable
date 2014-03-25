package bfh.ti.shakytable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Ball {
	private final static float wall_bounce_factor = 0.8f;
	public final static float relative_size = 0.1f;
	//private final static float unrecognizable_velocity = 20;
	private final static float friction = 0.99f;
	public float radius;
	private float inertia = 40f;
	public final static float[] masses = {0.25f, 0.75f, 2, 5};
	private float mass = masses[1];
	private float dt;
	private Paint paint;

	private float ax, ay; // acceleration
	public float vx, vy; // velocity
	private float x, y; // position (center of the ball!)

	public Ball(float update_frequency) {
		// initialize variables
		dt = 1.0f / update_frequency;

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
	}

	public void set_position(float position_x, float position_y) {
		x = position_x;
		y = position_y;
	}

	public void set_acceleration(float acceleration_x, float acceleration_y,
			int window_width, int window_height) {
		// set acceleration values
		ax = acceleration_x;
		ay = acceleration_y;

		// calculate resulting speed and position
		vx += inertia * dt * ax / mass;
		vy += inertia * dt * ay / mass;
		vx *= friction;
		vy *= friction;

		// check for bounce on a wall
		if (x + radius > window_width) {
			x = window_width - radius;
//			if(vx < unrecognizable_velocity)
//				vx = 0;
			vx *= -wall_bounce_factor;
		} else if (x - radius < 0) {
			x = radius;
//			if(vx < -unrecognizable_velocity)
//				vx = 0;
			vx *= -wall_bounce_factor;
		}

		if (y + radius > window_height) {
			
			y = window_height - radius;
//			if(vy < unrecognizable_velocity)
//				vy = 0;
			vy *= -wall_bounce_factor;
		} else if (y - radius < 0) {
			y = radius;
//			if(vy < -unrecognizable_velocity)
//				vy = 0;
			vy *= -wall_bounce_factor;
		}

		// calculate current position
		x += dt * vx;
		y += dt * vy;
	}
	
	public void set_mass(float mass) {
		this.mass = mass;
	}

	public void paint_ball(Canvas canvas) {
		canvas.drawCircle(x, y, radius, paint);
		//Log.d("scheduler", "drawing at " + x + " | " + y + ".");
		// optional: ein heller Spiegelpunkt, der sich proportional zur Position
		// im
		// Feld auf dem Ball verschiebt - erweckt die Illusion, dass der Ball 3D
		// ist.
	}
}