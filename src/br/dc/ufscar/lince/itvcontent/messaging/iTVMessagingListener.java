package br.dc.ufscar.lince.itvcontent.messaging;

public interface iTVMessagingListener {
	public void onMessage (String message);
	public void connectionSucceeded();
	public void connectionFailed(String message);

}
