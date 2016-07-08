package org.metaborg.tools.serfer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

public class Benchmark {

	private Callable<?> benchmark;
	private BenchmarkConfiguration config;
	private BenchmarkData data;

	public Benchmark(Callable<?> benchmark, BenchmarkConfiguration config) {
		this.benchmark = benchmark;
		this.config = config;
		this.data = new BenchmarkData();
	}

	public BenchmarkConfiguration getConfig() {
		return config;
	}

	public void writeToFile() throws IOException {
		if (!config.dataFile.exists())
			config.dataFile.createNewFile();

		FileOutputStream fis = new FileOutputStream(config.dataFile, true);
		data.flush(fis);
		fis.close();
	}

	public void execute() {
		execute(BenchmarkPhase.WARMUP, config.warmuprounds);
		execute(BenchmarkPhase.REPETITION, config.repetitions);
	}

	private void execute(BenchmarkPhase phase, int repetitions) {
		int repeatsleft = repetitions;
		while (repeatsleft > 0) {
			BenchmarkDatapoint dp = data.newMeasurement(phase);
			try {
				dp.start();
				benchmark.call();
			} catch (Exception ex) {
				dp.setIsFailed(ex.getMessage());
			} finally {
				dp.stop();
				repeatsleft--;
			}
		}
	}
}
