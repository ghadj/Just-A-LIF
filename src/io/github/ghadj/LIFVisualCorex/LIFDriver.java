package io.github.ghadj.LIFVisualCorex;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Driver for LIF class.
 * 
 * Compile from LIF-Visual-Cortex/ directory
 * javac -d ./bin ./src/io/github/ghadj/LIFVisualCortex/*.java
 * 
 * Run from Simple-Neural-Network/ directory
 * java -cp ./bin io.github.ghadj.LIFVisualCortex.LIFNetworkDriver
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
	public static final double Ie = 1; // constant injected current (nA)
	public static final double Trefract = 2; // absolute refractory period (msec)
	public static final double Dt = 0.15; // Δt - time between two consecutive points (msec)
	public static final double duration = 1000; // duration of the simulation (msec)

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		LIF model = new LIF(tau_m, Vreset, Vrest, Vth, Rm, Trefract);

		model.run(V0, Ie, duration, Dt);

		FileWriter writerPotential = new FileWriter("./potential.csv");
		FileWriter writerSpikes = new FileWriter("./spikes.csv");

		List<Double> potential = model.getPotential();
		if (potential != null)
			writerPotential.write(potential.toString().replaceAll("\\[|\\]", "").replaceAll(",", "\n"));

		List<Double> spikes = model.getSpikes();
		if (spikes != null && !spikes.isEmpty()) {
			System.out.printf("First spike at %.2f msec.\n", spikes.get(0));
			writerSpikes.write(spikes.toString().replaceAll("\\[|\\]", "").replaceAll(",", "\n"));

			System.out.printf("Number of spikes during the simulation %d.\n", model.numSpikes());
			System.out.printf("Mean ISI %.2f msec.\n", duration / model.numSpikes());
		}
		writerPotential.close();
		writerSpikes.close();
	}
}