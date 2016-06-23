package org.metaborg.tools.serfer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class BenchmarkData {

	private final Queue<BenchmarkDatapoint> queue = new LinkedList<>();

	public synchronized BenchmarkDatapoint newMeasurement(BenchmarkPhase phase) {
		BenchmarkDatapoint dp = new BenchmarkDatapoint(phase);
		queue.add(dp);
		return dp;
	}

	public synchronized void flush(OutputStream output) throws IOException {
		while (queue.peek() != null) {
			BenchmarkDatapoint dp = queue.remove();
			assert dp.isFinished();
			output.write(toCSVRow(dp).getBytes());
		}
		output.flush();
	}

	private String toCSVRow(BenchmarkDatapoint dp) {
		return dp.getPhase().name() + "," + dp.getStartTime() + "," + dp.getEndTime() + ","
				+ (dp.getEndTime() - dp.getStartTime()) + ", " + (dp.isSuccessful() ? 1 : 0) + ","
				+ (dp.isSuccessful() ? "N/A" : dp.getFailReason()) + "\n";
	}
}
