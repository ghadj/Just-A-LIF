package io.github.ghadj.LIFVisualCorex;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Driver for LIF class.
 * 
 * Compile from LIF-Visual-Cortex/ directory javac -d ./bin
 * ./src/io/github/ghadj/LIFVisualCortex/*.java
 * 
 * Run from Simple-Neural-Network/ directory java -cp ./bin
 * io.github.ghadj.LIFVisualCortex.LIFNetworkDriver
 * 
 * @author George Hadjiantonis
 * @since 18-03-2020
 */
public class LIFDriver {
	// Set Constants
	public static final double tau_m = 30; // membrane time constant (msec)
	public static final double Vreset = -65; // reset potential - mV
	public static final double Vrest = Vreset; // aka reversal potential of the leakage (EL) (mV)
	public static final double Vth = -50; // threshold potential (mV)
	public static final double V0 = -67; // Membrane potential at time 0 (mV)
	public static final double Rm = 90; // membrane resistance (MΩ)
	public static final double Ie = 1.0; // constant injected current (nA)
	public static final double Trefract = 2; // absolute refractory period (msec)
	public static final double dt = 0.0015; // Δt - time between two consecutive points (msec)
	public static final double duration = 1000; // duration of the simulation (msec)

	public static final double[] listIe = { 0.5, 1.0, 1.5, 2.0 }; // constant injected current (nA)

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FileWriter writerPotential = new FileWriter("./output/potential.csv");
		FileWriter writerSpikes = new FileWriter("./output/spikes.csv");
		FileWriter writerRisi = new FileWriter("./output/risi.csv");

		LIF model = new LIF(tau_m, Vreset, Vrest, Vth, Rm, Trefract);
		model.run(V0, Ie, duration, dt);

		List<Double> potential = model.getPotential();
		if (potential != null)
			writerPotential.write(potential.toString().replaceAll("\\[|\\]", "").replaceAll(",", "\n"));

		List<Double> spikes = model.getSpikes();
		if (spikes != null && !spikes.isEmpty()) {
			System.out.printf("First spike at %.2f msec.\n", spikes.get(0));
			System.out.printf("Number of spikes during the simulation %d.\n", model.numSpikes());
			System.out.printf("Mean ISI %.2f msec.\n", duration / model.numSpikes());

			writerSpikes.write(spikes.toString().replaceAll("\\[|\\]", "").replaceAll(",", "\n"));
		}

		double[] risi = new double[listIe.length];
		for (int i = 0; i < listIe.length; i++) {
			model.run(V0, listIe[i], duration, dt);
			risi[i] = model.numSpikes() / (duration / 1000); // interspike-interval firing rates (Hz)
			System.out.printf("Interspike-interval firing rate for Ie=%.2fnA is %.2fHz.\n", listIe[i], risi[i]);
		}

		writerRisi.write(Arrays.toString(risi).replaceAll("\\[|\\]", "").replaceAll(",", "\n"));

		writerPotential.close();
		writerSpikes.close();
		writerRisi.close();
	}
}