package br.dc.ufscar.lince.itvcontent.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import br.dc.ufscar.lince.itvcontent.iTVContentApplication;
import br.dc.ufscar.lince.itvcontent.config.iTVConfig;
import br.dc.ufscar.lince.itvcontent.utils.iTVUtils;

public class iTVLogic {
	
	private final String TAG = "iTVLogic";
	private static iTVLogic instance = null;
	
	private HashMap<String, String> filesUrl = new HashMap<String, String>();
	
	
	private JSONObject json = null;
	
	private iTVLogic(){
	}
	
	public static iTVLogic getInstance(){
		
		if(instance == null){
			instance = new iTVLogic();	
		}
		
		return instance;
	}
	
	public void parseMediaJSON(String jsonString){
		try{		
			this.json = new JSONObject(jsonString);
		}
		catch (JSONException e) {
			Log.e(TAG, "Exception parsing message JSON: " + e);
		}
	 }
	
	public String getProjectName(){
		
		String name = null;
		try{
			if (json != null){
				JSONObject project = json.getJSONObject("AnnotationProject");
				return project.getString("name");				
			}
			
		}
		catch (JSONException e) {
			Log.e(TAG, "Exception parsing message JSON: " + e);
		}
		return name;
	}
	
		
	public List<String[]> getFileNames(){
		ArrayList<String[]> files = new ArrayList<String[]>();
		
		try{		
			JSONArray annotations = json.getJSONObject("AnnotationProject").getJSONArray("annotations");
			for (int i = 0; i < annotations.length(); i++) {				
				JSONObject annotation = annotations.getJSONObject(i).getJSONObject("Annotation");
				JSONArray contents = annotation.getJSONObject("interaction").getJSONObject("ShowContent").getJSONArray("contents");
						
				for (int j = 0; j < contents.length(); j++) {
					JSONObject media = contents.getJSONObject(j).getJSONObject("Media");
					String media_name = media.getString("id");
					
					String media_url = iTVUtils.encondeURL(media.getString("url"));					
					filesUrl.put(media_name, media_url);
					
					Log.d(TAG, "Acrescentando chaves: ("+media_name+","+media_url+")");
					
					String type = media.getString("type");
											
					String[] mediaInfo = {media_name, media_url, type};
					files.add(mediaInfo);
				}
			}
			
			Log.d(TAG, "***Medias encontrada:"+files.size());
		
		}
		catch (JSONException e) {
			Log.e(TAG, "Exception parsing message JSON: " + e);
		}
		
		return files;
	}
	
	public String getMessagingHost(){
		SharedPreferences settings = iTVContentApplication.getContext().getSharedPreferences(iTVConfig.PREFERENCE_FILE, 0);
		return settings.getString(iTVConfig.PREF_HOST, iTVConfig.MESSAGING_DEFAULT_HOST);
	}

	public String getMessagingPort(){
		SharedPreferences settings = iTVContentApplication.getContext().getSharedPreferences(iTVConfig.PREFERENCE_FILE, 0);
		return settings.getString(iTVConfig.PREF_PORT, iTVConfig.MESSAGING_DEFAULT_PORT);
	}
	
	public String getMessagingTopic(){
		SharedPreferences settings = iTVContentApplication.getContext().getSharedPreferences(iTVConfig.PREFERENCE_FILE, 0);
		return settings.getString(iTVConfig.PREF_TOPIC, iTVConfig.MESSAGING_DEFAULT_TOPIC);
	}
	
	public String getMessagingUserName(){
		SharedPreferences settings = iTVContentApplication.getContext().getSharedPreferences(iTVConfig.PREFERENCE_FILE, 0);
		return settings.getString(iTVConfig.PREF_USER_NAME, iTVConfig.MESSAGING_DEFAULT_USER);
	}

	public String getMessagingPassword(){
		SharedPreferences settings = iTVContentApplication.getContext().getSharedPreferences(iTVConfig.PREFERENCE_FILE, 0);
		return settings.getString(iTVConfig.PREF_PWD, iTVConfig.MESSAGING_DEFAULT_PWD);
	}
	
	public byte[] getFileContent(Context context, String mediaId) {
		
		byte[] input = null;
		try {
			FileInputStream fis = context.openFileInput(mediaId);
			input = new byte[fis.available()];
			fis.read(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return input;
	}

	public String getFileURL (String key){
		return filesUrl.get(key);
	}

}
