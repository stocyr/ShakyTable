package com.example.shackytable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.View;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TableView extends View {
	private int window_width, window_height;
	private ScheduledExecutorService scheduleTaskExecutor;
	private Ball ball;

	private boolean dirac = true;
	
	final static float frequency = 50f;

	public TableView(Context context) {
		super(context);
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
				trigger_physics_engine();
			}
		}, 0, (int)(1000/frequency), TimeUnit.MILLISECONDS);
	}

	public void trigger_physics_engine() {
		// calculate new position
		if (dirac == true) {
			ball.set_acceleration(0, 9.81f, window_width, window_height);
			dirac = false;
		} else {
			ball.set_acceleration(0, 9.81f, window_width, window_height);
		}

		// tell view to redraw
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		ball.paint_ball(canvas);
	}
}