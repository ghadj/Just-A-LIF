# Simulation of leaky Integrate-and-Fire neuron.
#
# George Hadjiantonis
# 18-03-2020

from brian2 import *
from numpy import *

N = 1               # number of neurons
tau_m = 30 * ms     # membrane time constant
v_r = -65 * mV      # reset potential
v_th = -50 * mV     # threshold potential
v_rest = -65*mV     # rest potential
Rm_Ie =  90 * mV    # constant input current
duration = 1*second # duration of the simulation

eqs = '''
 dv/dt = (v_rest - v + Rm_Ie)/tau_m : volt (unless refractory)
 '''

lif = NeuronGroup(N, model=eqs, threshold='v>v_th', reset='v=v_r', refractory=2*ms, method='exact')
lif.v = -67*mV

state_monitor = StateMonitor(lif, ['v'], record=True)
spike_monitor = SpikeMonitor(lif)

run(duration)

print('First spike at', spike_monitor.t[0])
print('Number of spikes during the simulation', spike_monitor.num_spikes)
print('Mean ISI', duration/spike_monitor.num_spikes)

figure()
plot(state_monitor.t/ms, state_monitor[0].v/mV)
show()
