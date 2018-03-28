package me.drkmatr1984.reflectionutils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class UUIDUtils{
	
	@SuppressWarnings("deprecation")
	public static UUID getUUIDfromPlayerName(String playerName){
		OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
		if(!op.equals(null)){
			return op.getUniqueId();
		}
		return null;
	}
	
	public static String getPlayerNamefromUUID(UUID uuid){
		OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
		if(!op.equals(null)){
			return op.getName();
		}
		return "";
	}
	
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
	
}