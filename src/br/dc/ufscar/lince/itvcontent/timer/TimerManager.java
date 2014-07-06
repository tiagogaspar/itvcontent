package br.dc.ufscar.lince.itvcontent.timer;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;

public class TimerManager {

	private static final String TAG = "TimeManager";

	private Handler handler;
	private long time = -1;
	private TimerListener timerListener;
	
	private Timer t;
	private TimerTask task;
	

	public TimerManager(long initialTime, Handler handler, TimerListener timerListener) {
		this.time = initialTime;
		this.handler = handler;
		this.timerListener = timerListener;
		
		// inicia o timer
		startTimer();
	}

	private void startTimer() {

		task = new TimerTask() {
			public void run() {

				time++;

				handler.post(new Runnable() {
					public void run() {
						timerListener.timeTick(time);
					}
				});

			}
		};

		t = new Timer();
		t.scheduleAtFixedRate(task, 0, 1000);
	}

}
