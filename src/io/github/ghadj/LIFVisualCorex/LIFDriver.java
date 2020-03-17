package io.github.ghadj.LIFVisualCorex;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Driver for LIF class.
 * 
 * @author George Hadjiantonis
 * @since 17-03-2020
 */
public class LIFDriver {
	// Set Constants
	public static final double tau_m = 30; // membrane time constant - msec
	public static final double Vreset = -65; // reset potential - mV
	public static final double Vrest = Vreset; // aka reversal potential of the leakage (E_L) - mV
	public static final double Vth = -50; // threshold potential - mV
	public static final double V0 = -67; // Membrane potential at time 0 - mV
	public static final double Rm = 90; // membrane resistance - MΩ
	public static final double Ie = 1; // constant injected current - nA
	public static final double Trefract = 2; // absolute refractory period - msec
	public static final double Dt = 0.25; // Δt - time between two consecutive points
	public static final double duration = 1000; // duration of the simulation - msec

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		LIF model = new LIF(tau_m, Vreset, Vrest, Vth, Rm, Trefract);

		model.run(V0, Ie, duration, Dt);
		System.out.println("spikes #" + model.numSpikes());

		FileWriter writer = new FileWriter("./ouput.csv");
		List<Double> potential = model.getPotential();
		if (potential != null) {
			String output = potential.toString().replaceAll("\\[|\\]", "").replaceAll(",", "\n");
			writer.write(output);
		}
		writer.close();
	}
}