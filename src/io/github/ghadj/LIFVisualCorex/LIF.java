package io.github.ghadj.LIFVisualCorex;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of generic Leaky Integrate-and-Fire model.
 * 
 * @author George Hadjiantonis
 * @since 18-03-2020
 */
public class LIF {
	private double tau_m; // membrane time constant (msec)
	private double Vreset; // (mV)
	private double Vrest; // aka reversal potential of the leakage (E_L) (mV)
	private double Vth; // (mV)
	private double Rm; // membrane resistance (MΩ)
	private double Trefract; // absolute refractory period (msec)

	private ArrayList<Double> spikes = null; // time of each spike
	private ArrayList<Double> potential = null; // potential at a current time

	/**
	 * Constructor of a LIF instance.
	 * 
	 * @param tau_m    membrane time constant.
	 * @param Vreset   reset membrane potential.
	 * @param Vrest    rest membrane potential.
	 * @param Vth      threshold in mV.
	 * @param Rm       membrane resistance in MΩ.
	 * @param Trefract absolute refractory period in msec.
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
	 * Run simulation. Membrane potential is defined as:
	 * 
	 * V(t) = E_L + R_m * I_e + (V(0) − E_L − R_m * I_e ) * exp(−t/τ_m )
	 * 
	 * If V(t) reaches the threshold at time t = t(f), we interrupt the dynamics
	 * during an absolute refractory time Trefract and restart the integration at
	 * time t(f) + Treftact with the new initial condition Vreset.
	 * 
	 * @param V0       initial membrane potential condition in mV.
	 * @param Ie       constant current injection in nA.
	 * @param duration of the simulation in msec.
	 * @param dt       time between two consecutive time points in msec.
	 * @throws InvalidParameterException in case dt has negative value.
	 */
	public void run(double V0, double Ie, double duration, double dt) throws InvalidParameterException {
		if (dt < 0)
			throw new InvalidParameterException("dt must have positive value.");
		spikes = new ArrayList<Double>((int) Math.ceil(duration / dt));
		potential = new ArrayList<Double>((int) Math.ceil(duration / dt));
		double Vt = V0;
		double integrationTime = 0;
		for (double t = 0; t < duration; t += dt) {
			potential.add(Vt);
			Vt = Vrest + (Rm * Ie) + (V0 - Vrest - (Rm * Ie)) * Math.exp(-(integrationTime / tau_m));
			integrationTime += dt;
			if (Vt >= Vth) {
				integrationTime = 0; // reset integration time
				Vt = Vreset; // reset membrane potential
				V0 = Vreset; // reset initial membrane condition

				spikes.add(t); // add time the spike occurs
				potential.add(0.0); // high value to show spike
				double refractPeriod = t + Trefract;
				for (t += dt; t < refractPeriod; t += dt)
					potential.add(Vt);
			}
		}
	}

	/**
	 * Return the time in msec of the spikes.
	 * 
	 * @return list of times of each spike.
	 */
	public List<Double> getSpikes() {
		return spikes;
	}

	/**
	 * Return the membrane potential in mV for the duration of the simulation.
	 * 
	 * @return membrane potential.
	 */
	public List<Double> getPotential() {
		return potential;
	}

	/**
	 * Return the number of spikes for the duration of the simulation. In case the
	 * simulation did not run, returns -1.
	 * 
	 * @return the number of spikes for the simulation, otherwise if the simulation
	 *         did not run -1.
	 */
	public int numSpikes() {
		if (spikes != null)
			return spikes.size();
		return -1;
	}
}