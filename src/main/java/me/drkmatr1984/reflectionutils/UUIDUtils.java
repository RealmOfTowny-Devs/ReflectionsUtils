package me.drkmatr1984.reflectionutils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class UUIDUtils{
	
	public static OfflinePlayer getOfflinePlayerfromUUID(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	public static List<UUID> getUUIDListfromStringList(List<String> stringList){
		List<UUID> uuidList = new ArrayList<UUID>();
		if(stringList!=null && !stringList.isEmpty()){
			for(String s : stringList){
				uuidList.add(UUID.fromString(s));
			}
		}
		return uuidList;
	}
	
	public static List<String> getStringListfromUUIDList(List<UUID> uuidList){
		List<String> stringList = new ArrayList<String>();
		if(!uuidList.isEmpty() && uuidList!=null){
			for(UUID uuid : uuidList){
				stringList.add(uuid.toString());
			}
		}
		return stringList;
	}
	
	public static class UUIDFetcher {	
		/**
		 * @param player The player
		 * @return The UUID of the given player
		*/
		
		public static UUID getUUID(Player player) {
			return getUUID(player.getName());
		}
		
		public static UUID getUUID(OfflinePlayer player) {
			return getUUID(player.getName());
		}
		
		/**
		 * @param playername The name of the player
		 * @return The UUID of the given player
		 */
		public static UUID getUUID(String playername) {
			String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);
			
			StringBuilder result = new StringBuilder();
			
			readData(output, result);
			
			String u = result.toString();
			
			String uuid = "";
			
			for(int i = 0; i <= 31; i++) {
				uuid = uuid + u.charAt(i);
				if(i == 7 || i == 11 || i == 15 || i == 19) {
					uuid = uuid + "-";
				}
			}
			
			return UUID.fromString(uuid);
		}
		
		private static void readData(String toRead, StringBuilder result) {
			int i = 7;
			
			while(i < 200) {
				if(!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {
					
					result.append(String.valueOf(toRead.charAt(i)));
					
				} else {
					break;
				}
				
				i++;
			}
		}
		
		private static String callURL(String URL) {
			StringBuilder sb = new StringBuilder();
			URLConnection urlConn = null;
			InputStreamReader in = null;
			try {
				URL url = new URL(URL);
				urlConn = url.openConnection();
				
				if (urlConn != null) urlConn.setReadTimeout(60 * 1000);
				
				if (urlConn != null && urlConn.getInputStream() != null) {
					in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
					BufferedReader bufferedReader = new BufferedReader(in);
					
					if (bufferedReader != null) {
						int cp;
						
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
						
						bufferedReader.close();
					}
				}
				
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
	 
			return sb.toString();
		}
	}
	
	public static class NameFetcher {
	
		/**
		 * @param uuid The UUID of a player
		 * @return The name of the given player
		 */
		public static String getName(UUID uuid) {
			return getName(uuid.toString());
		}
	  
		/**
		 * @param uuid The UUID of a player (can be trimmed or the normal version)
		 * @return The name of the given player
		 */
		public static String getName(String uuid) {
			uuid = uuid.replace("-", "");
			
			String output = callURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
			
			StringBuilder result = new StringBuilder();
			
			int i = 0;
			
			while(i < 200) {
				
				if((String.valueOf(output.charAt(i)).equalsIgnoreCase("n")) && (String.valueOf(output.charAt(i+1)).equalsIgnoreCase("a")) && (String.valueOf(output.charAt(i+2)).equalsIgnoreCase("m")) && (String.valueOf(output.charAt(i+3)).equalsIgnoreCase("e"))) {
					
					int k = i+7;
					
					while(k < 100) {
						
						if(!String.valueOf(output.charAt(k)).equalsIgnoreCase("\"")) {
							
							result.append(String.valueOf(output.charAt(k)));
							
						} else {
							break;
						}
						
						k++;
					}
					
					break;
				}
				
				i++;
			}
			
			return result.toString();
		}
		
		private static String callURL(String URL) {
			StringBuilder sb = new StringBuilder();
			URLConnection urlConn = null;
			InputStreamReader in = null;
			try {
				URL url = new URL(URL);
				urlConn = url.openConnection();
				
				if (urlConn != null) urlConn.setReadTimeout(60 * 1000);
				
				if (urlConn != null && urlConn.getInputStream() != null) {
					in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
					BufferedReader bufferedReader = new BufferedReader(in);
					
					if (bufferedReader != null) {
						int cp;
						
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
						
						bufferedReader.close();
					}
				}
				
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
	 
			return sb.toString();
		}
	}
}