package br.dc.ufscar.lince.itvcontent.timer;

public interface TimerListener {
	public void timeTick(long time);
	public void timeEvent (long time);
}