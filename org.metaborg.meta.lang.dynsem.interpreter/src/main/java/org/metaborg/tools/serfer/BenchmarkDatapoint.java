package org.metaborg.tools.serfer;

public class BenchmarkDatapoint {

	private final BenchmarkPhase phase;

	private long startTime;
	private long endTime;

	private boolean hasRun;
	private boolean isRunning;
	private boolean success = true;
	private String failReason;

	public BenchmarkDatapoint(BenchmarkPhase phase) {
		this.phase = phase;
	}

	public synchronized void start() {
		assert !hasRun && !isRunning;

		startTime = System.nanoTime();
		isRunning = true;
	}

	public synchronized void stop() {
		assert isRunning && !hasRun;

		endTime = System.nanoTime();
		isRunning = false;
		hasRun = true;
	}

	public void setIsFailed(String reason) {
		success = false;
		failReason = reason;
	}

	public String getFailReason() {
		return failReason;
	}

	public boolean isSuccessful() {
		return isFinished() && success;
	}

	public boolean isFinished() {
		return hasRun && !isRunning;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public BenchmarkPhase getPhase() {
		return phase;
	}

}
