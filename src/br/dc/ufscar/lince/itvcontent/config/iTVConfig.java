package br.dc.ufscar.lince.itvcontent.config;

public class iTVConfig {
	
	public static boolean DEBUG_MODE = true;
	public static String VERSION = "0.1";
	
	public static String PREFERENCE_FILE = "preferences";
	
	//Messaging
	//"tcp://192.168.1.106:1883"
	public static final String MESSAGING_DEFAULT_HOST = "192.168.98.138";
	public static final String MESSAGING_DEFAULT_PORT = "1883";
	public static final String MESSAGING_DEFAULT_USER = "admin";
	public static final String MESSAGING_DEFAULT_PWD = "admin";
	public static final String MESSAGING_DEFAULT_TOPIC = "presentation";
	public static final String MESSAGING_DEFAULT_SYNC_TOPIC = "control";
	
	public static final String PREF_HOST = "host";
	public static final String PREF_PORT = "port";
	public static final String PREF_TOPIC = "topic";
	public static final String PREF_USER_NAME = "user_name";
	public static final String PREF_PWD = "password";
	
	public static final int MEDIA_IMAGE = 2;
	public static final int MEDIA_TEXT = 3;
	public static final int MEDIA_AUDIO = 4;
	public static final int MEDIA_VIDEO = 5;
	

}
