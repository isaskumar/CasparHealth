package caspars.qa.base.lib;

import java.util.concurrent.TimeUnit;

public class TimeWatch {
	private long endTime;
	private long startTime;

	public TimeWatch() {
		startTime = System.nanoTime();
		endTime = 0;
	}


	public long getElapsedTime() {
		long elapsed = 0;
		if (endTime != 0) {
			long elapsedNano = endTime - startTime;
			elapsed = TimeUnit.MILLISECONDS.convert(elapsedNano, TimeUnit.NANOSECONDS);
		}
		return elapsed;
	}


	public long getEndTime() {
		return endTime;
	}


	public long getStartTime() {
		return startTime;
	}


	public void stop() {
		endTime = System.nanoTime();
	}
}