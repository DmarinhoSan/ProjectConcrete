package br.concrete.Desafio.git;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.net.HttpURLConnection;

import java.net.MalformedURLException;

import java.net.URL;

public class ConexClient {

	public static void main(String[] args) {
		
		try {

			URL url = new URL ("http://localhost:");
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setDoOutput(true);
			
			conn.setRequestMethod("POST");
			
			conn.setRequestProperty("Contet-Type", "application/json");
			
			OutputStream outputStream = conn.getOutputStream();
			
			String requestMessage = "{ "
     
					+ "\"email\":\"user1@mock.ws\","
        			+ "\"senha\":\"PWD1\"" + "}";
        
			outputStream.write(requestMessage.getBytes());
        
			outputStream.flush();

        
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        
			}
        
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        
			String output;
        
			while ((output = br.readLine()) != null) {
            
				System.out.println(output);
        
			}
        
			conn.disconnect();
    
		} catch (MalformedURLException e) {
        
			e.printStackTrace();
    
		} catch (IOException e) {
        
			e.printStackTrace();
    
		}	
	}	
}