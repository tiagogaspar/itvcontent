package br.dc.ufscar.lince.itvcontent.messaging;

import java.net.URISyntaxException;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;
import br.dc.ufscar.lince.itvcontent.config.iTVConfig;
import br.dc.ufscar.lince.itvcontent.logic.iTVLogic;

public class iTVMessaging {
	
	private final String TAG = "iTVMessaging";
	
	private String messagingTopic;
	private CallbackConnection connection;
	private iTVMessagingListener messagingListener;
	private static iTVMessaging instance = null;
	
	private MQTT mqtt;
	
	
	private iTVMessaging(){
	}
	
	public static iTVMessaging getInstance(){
		
		if(instance == null){
			instance = new iTVMessaging();
		}
		return instance;
	}
	
	public void setMessagingListener(iTVMessagingListener messagingListener){
		this.messagingListener = messagingListener;
	}

	
	public void connect(iTVMessagingListener mListener, String topic){
		
		this.messagingTopic = topic;
		this.messagingListener = mListener;

		mqtt = new MQTT();
		
		iTVLogic itvLogic = iTVLogic.getInstance();
		String host = "tcp://"+itvLogic.getMessagingHost()+":"+itvLogic.getMessagingPort();

		try{
			mqtt.setHost(host);
			Log.d(TAG, "Address set: " + host);
		}
		catch(URISyntaxException urise){
			Log.e(TAG, "URISyntaxException connecting to " + host + " - " + urise);
		}
		
		Log.d(TAG, "Topic set: [" + messagingTopic + "]");
		
		mqtt.setUserName(itvLogic.getMessagingUserName());
		Log.d(TAG, "UserName set: [" + itvLogic.getMessagingUserName() + "]");
		
		mqtt.setPassword(itvLogic.getMessagingPassword());
		Log.d(TAG, "Password set: [" + itvLogic.getMessagingPassword() + "]");
				
		this.connection = mqtt.callbackConnection();
		
		connection.listener(new Listener() {

		    public void onDisconnected() {
		    }
		    public void onConnected() {
		    }

		    public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
		        // You can now process a received message from a topic.
		        // Once process execute the ack runnable.
		        ack.run();
		        
				String messagePayLoad = payload.toString();
				
				int index = messagePayLoad.indexOf("{");
				
				if(index > 0){
					messagePayLoad = messagePayLoad.substring(index, messagePayLoad.length());
				}
				
				Log.d(TAG, "*****Message received: "+messagePayLoad);
				
				messagingListener.onMessage(messagePayLoad);				
			}
		    
		    public void onFailure(Throwable value) {
		        connection.disconnect(null); // a connection failure occured.
		    }
		});
		
		connection.connect(new Callback<Void>() {
			
		    public void onFailure(Throwable value) {
		        // If we could not connect to the server.
				Log.e(TAG, "Exception connecting Messaging Host - " + value);
				messagingListener.connectionFailed("MQTT = Connection Failed");
		    }

		    // Once we connect..
		    public void onSuccess(Void v) {

		    	messagingListener.connectionSucceeded();
		    	Log.e(TAG, "*** Connected to broker!");
		    	
		        // Subscribe to a topic
		        Topic[] topics = {new Topic(messagingTopic, QoS.AT_LEAST_ONCE)};
		        
		        connection.subscribe(topics, new Callback<byte[]>() {
		            public void onSuccess(byte[] qoses) {
		                // The result of the subcribe request.
		            }
		            public void onFailure(Throwable value) {
		            	connection.disconnect(null); // a connection failure occured.
		            }
		        });
		    }
		    
		});
	}
	
	public void disconnect(){
		
		//Unsubscribe from topic
		if(connection != null){
	        UTF8Buffer[] topics = {new UTF8Buffer(messagingTopic)};
	        
			connection.unsubscribe(topics, new Callback<Void>() {
	            public void onSuccess(Void v) {
	                // called once the connection is disconnected.
	  				Log.d(TAG, "***Unsubscriped from topic: "+messagingTopic);
	              }
	              public void onFailure(Throwable e) {
	                // Disconnects never fail.
	              	Log.e(TAG, "Exception disconnecting to Messaging Host - " + e);
	              }
	          });
		}

		
		// To disconnect..
        connection.disconnect(new Callback<Void>() {
            public void onSuccess(Void v) {
              // called once the connection is disconnected.
				Log.d(TAG, "***Disconected to Messaging Host!");
            }
            public void onFailure(Throwable e) {
              // Disconnects never fail.
            	Log.e(TAG, "Exception disconnecting to Messaging Host - " + e);
            }
        });
        
	}	
}
