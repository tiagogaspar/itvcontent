package br.dc.ufscar.lince.itvcontent.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class iTVUtils {
	
	public static String encondeURL(String mediaUrl){
		try {
			URL url = new URL(mediaUrl);
			URI uri = new URI(
				    "http", 
				    url.getHost(), 
				    url.getPath(),
				    null);
			url = uri.toURL();
			return url.toString();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return null;
	}
}
