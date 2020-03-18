# Simulation of leaky Integrate-and-Fire neuron.
#
# George Hadjiantonis
# 18-03-2020

from brian2 import *

N = 1               # number of neurons
tau_m = 30 * ms     # membrane time constant
v_r = -65 * mV      # reset potential
v_th = -50 * mV     # threshold potential
v_rest = -65*mV     # rest potential
Rm_Ie =  90 * mV    # membrane resistance x constant input current
duration = 1*second # duration of the simulation

eqs = '''
 dv/dt = (v_rest - v + Rm_Ie)/tau_m : volt (unless refractory)
 '''

lif = NeuronGroup(N, model=eqs, threshold='v>v_th', reset='v=v_r', refractory=2*ms)
lif.v = -67 * mV

state_monitor = StateMonitor(lif, ['v'], record=True)
spike_monitor = SpikeMonitor(lif)

run(duration)

# simulation infos
print('First spike at', spike_monitor.t[0])
print('Number of spikes during the simulation', spike_monitor.num_spikes)
print('Mean ISI', duration/spike_monitor.num_spikes)

# plot of membrane potential in the duration of the simulation
figure(1)
plot(state_monitor.t/second, state_monitor[0].v/mV)
xlabel('Time (sec)')
ylabel('Potential (mV)')

list_Ie = [0.5, 1, 1.5, 2] # constant injected current
list_Rm_Ie = [i*90* mV for i in list_Ie] # multiply by membrane resistance (90mV)
r_isi = list()

for Rm_Ie in list_Rm_Ie:
    state_monitor = StateMonitor(lif, ['v'], record=True)
    spike_monitor = SpikeMonitor(lif)
    run(duration)
    r_isi.append(spike_monitor.num_spikes/duration)

figure()
plot(list_Ie, r_isi)
xlabel('I (nA)')
ylabel('r_isi (Hz)')
show()