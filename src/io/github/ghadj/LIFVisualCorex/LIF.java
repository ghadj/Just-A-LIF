package io.github.ghadj.LIFVisualCorex;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of generic Leaky Integrate-and-Fire model.
 * 
 * @author George Hadjiantonis
 * @since 17-03-2020
 */
public class LIF {
	private double tau_m; // membrane time constant - msec
	private double Vreset; // mV
	private double Vrest; // aka reversal potential of the leakage (E_L) - mV
	private double Vth; // mV
	private double Rm; // membrane resistance - MΩ
	private double Trefract; // absolute refractory period - msec

	private ArrayList<Double> spikes = null; // time of each spike
	private ArrayList<Double> potential = null; // potential at a current time

	/**
	 * 
	 * @param tau_m
	 * @param Vreset
	 * @param Vrest
	 * @param Vth
	 * @param Rm
	 * @param Trefract
	 */
	public LIF(double tau_m, double Vreset, double Vrest, double Vth, double Rm, double Trefract) {
		this.tau_m = tau_m;
		this.Vreset = Vreset;
		this.Vrest = Vrest;
		this.Vth = Vth;
		this.Rm = Rm;
		this.Trefract = Trefract;
	}

	/**
	 * V(t) = E_L + R_m * I_e + (V(0) − E_L − R_m * I_e ) * exp(−t/τ_m )
	 * 
	 * @param V0
	 * @param Ie
	 * @param duration
	 * @param dt
	 */
	public void run(double V0, double Ie, double duration, double dt) {
		spikes = new ArrayList<Double>((int) Math.ceil(duration / dt));
		potential = new ArrayList<Double>((int) Math.ceil(duration / dt));
		double Vt = V0;
		for (double t = 0; t < duration; t += dt) {
			potential.add(Vt);
			Vt = Vrest + (Rm * Ie) + (V0 - Vrest - (Rm * Ie)) * Math.exp(-t / tau_m);
			if (Vt >= Vth) {
				spikes.add(t);
				potential.add(10.0); // high value to show spike
				Vt = Vreset;
				double refractPeriod = t + Trefract;
				for (t += dt; t < refractPeriod; t += dt)
					potential.add(Vt);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<Double> getSpikes() {
		return spikes;
	}

	/**
	 * 
	 * @return
	 */
	public List<Double> getPotential() {
		return potential;
	}

	/**
	 * 
	 * @return
	 */
	public int numSpikes() {
		if (spikes != null)
			return spikes.size();
		return -1;
	}
}
