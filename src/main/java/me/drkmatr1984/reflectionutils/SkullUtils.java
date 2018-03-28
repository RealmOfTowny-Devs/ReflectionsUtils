package me.drkmatr1984.reflectionutils;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.drkmatr1984.reflectionutils.NMSUtils;
import me.drkmatr1984.reflectionutils.Reflections;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents some special mob heads, also support creating player skulls and custom skulls.
 *
 * @author xigsag, SBPrime
 */
public class SkullUtils extends NMSUtils{

    private static final Base64 base64 = new Base64();

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url skin url
     * @return itemstack
     */
    public static ItemStack getCustomSkull(String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        byte[] encodedData = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        head.setItemMeta(headMeta);
        
        return head;
    }
    
    public static String getSkullURL(Block b){       
        Skull skull = (Skull) b.getState();
        Object skullTile = NMSUtils.getTileEntity(skull.getLocation());
        GameProfile profile = getGameProfile(skullTile);
        Collection<Property> textures = profile.getProperties().get("textures");
        String text = "";
       
        for (Property texture : textures) {
              text = texture.getValue();
        }
       
        String decoded = Base64Coder.decodeString(text);
        String textureNumber = decoded.replace("{textures:{SKIN:{url:\"", "").replace("\"}}}", "").trim(); //.replace("http://textures.minecraft.net/texture/", "")
       
        return textureNumber;
    }
    
    public static GameProfile getGameProfile(Block b) {
    	Skull skull = (Skull) b.getState();
        Object skullTile = NMSUtils.getTileEntity(skull.getLocation());
        return getGameProfile(skullTile);
    }
    
    public static Object getSkullProfile(ItemMeta itemMeta)
    {
        Object profile = null;
        try {
            if (itemMeta == null || !class_CraftMetaSkull.isInstance(itemMeta)) return null;
            profile = class_CraftMetaSkull_profile.get(itemMeta);
        } catch (Exception ex) {

        }
        return profile;
    }
    
    public static String getSkullURL(ItemStack skull) {
        return getProfileURL(getSkullProfile(skull.getItemMeta()));
    }
    
    private static String getProfileURL(Object profile)
    {
        String url = null;
        if (profile == null) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Multimap<String, Object> properties = (Multimap<String, Object>)class_GameProfile_properties.get(profile);
            Collection<Object> textures = properties.get("textures");
            if (textures != null && textures.size() > 0)
            {
                Object textureProperty = textures.iterator().next();
                String texture = (String)class_GameProfileProperty_value.get(textureProperty);
                String decoded = Base64Coder.decodeString(texture);
                url = decoded.replace("{textures:{SKIN:{url:\"", "").replace("\"}}}", "").trim();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return url;
    }

    /**
     * Return a skull of a player.
     *
     * @param name player's name
     * @return itemstack
     */
	public static ItemStack getPlayerSkull(String name) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        if(UUIDUtils.getUUIDfromPlayerName(name)!=null){
        	meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUIDUtils.getUUIDfromPlayerName(name)));
        }else{
        	meta.setDisplayName("Steve");
        }        
        itemStack.setItemMeta(meta);
        return itemStack;
    }
	
	
	public static ItemStack getURLSkull(String url) {
        // The "MHF_Question" is here so serialization doesn't cause an NPE
        return getURLSkull(url, "MHF_Question", UUID.randomUUID(), null);
    }

    public static ItemStack getURLSkull(URL url) {
        // The "MHF_Question" is here so serialization doesn't cause an NPE
        return getURLSkull(url, "MHF_Question", UUID.randomUUID(), null);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getURLSkull(String url, String ownerName, UUID id, String itemName) {
        try {
            return getURLSkull(new URL(url), ownerName, id, itemName);
        } catch (MalformedURLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Malformed URL: " + url, e);
        }
        return new ItemStack(Material.SKULL_ITEM, 1, (short)0, (byte)3);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getURLSkull(URL url, String ownerName, UUID id, String itemName) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)0, (byte)3);
        if (itemName != null) {
            ItemMeta meta = skull.getItemMeta();
            if (itemName != null) {
                meta.setDisplayName(itemName);
            }
            skull.setItemMeta(meta);
        }

        try {
            skull = makeReal(skull);
            Object skullOwner = createNode(skull, "SkullOwner");
            setMeta(skullOwner, "Id", id.toString());
            setMeta(skullOwner, "Name", ownerName);
            Object properties = createNode(skullOwner, "Properties");

            Object listMeta = class_NBTTagList.newInstance();
            Object textureNode = class_NBTTagCompound.newInstance();

            String textureJSON = "{textures:{SKIN:{url:\"" + url + "\"}}}";
            String encoded = Base64Coder.encodeString(textureJSON);

            setMeta(textureNode, "Value", encoded);
            class_NBTTagList_addMethod.invoke(listMeta, textureNode);
            class_NBTTagCompound_setMethod.invoke(properties, "textures", listMeta);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return skull;
    }

    public static ItemStack getPlayerSkull(String playerName, String itemName)
    {
        return getPlayerSkull(playerName, UUID.randomUUID(), itemName);
    }

    public static ItemStack getPlayerSkull(String playerName, UUID uuid)
    {
        return getPlayerSkull(playerName, uuid, null);
    }

    public static ItemStack getPlayerSkull(String playerName, UUID uuid, String itemName)
    {
        return getURLSkull("http://skins.minecraft.net/MinecraftSkins/" + playerName + ".png", playerName, uuid, itemName);
    }

    public static ItemStack getPlayerSkull(Player player)
    {
        return getPlayerSkull(player, null);
    }

    public static ItemStack getPlayerSkull(Player player, String itemName)
    {
        return getPlayerSkull(player.getName(), player.getUniqueId(), itemName);
    }

    public static boolean setSkullProfile(ItemMeta itemMeta, Object data)
    {
        try {
            if (itemMeta == null || !class_CraftMetaSkull.isInstance(itemMeta)) return false;
            class_CraftMetaSkull_profile.set(itemMeta, data);
            return true;
        } catch (Exception ex) {

        }
        return false;
    }
    
    public static void setBlocktoSkull(String skinUrl, Block block) {
        block.setType(Material.SKULL);
        org.bukkit.block.Skull skullData = (org.bukkit.block.Skull) block.getState();
        skullData.setSkullType(SkullType.PLAYER);
        Object skullTile = NMSUtils.getTileEntity(skullData.getLocation());
        setGameProfile(skullTile, getNonPlayerProfile(skinUrl));
        block.getState().update(true);
    }
    
    public static GameProfile getNonPlayerProfile(String skinURL) {
        GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
        newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinURL + "\"}}}")));
        return newSkinProfile;
    }
    
    public static GameProfile getGameProfile(Object tileEntitySkull) {
    	class_TileEntitySkull = tileEntitySkull.getClass();
    	try {
			return (GameProfile) class_TileEntitySkull_getGameProfileMethod.invoke(class_TileEntitySkull);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }

    public static void setGameProfile(Object tileEntitySkull, GameProfile profile) {
    	class_TileEntitySkull = tileEntitySkull.getClass();
    	try {
			class_TileEntitySkull_setGameProfileMethod.invoke(profile);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static GameProfile getGameProfile(UUID uuid) {
    	if(UUIDUtils.getOfflinePlayerfromUUID(uuid).getPlayer()!=null) {
    		if(NMSUtils.getHandle(UUIDUtils.getOfflinePlayerfromUUID(uuid).getPlayer())!=null) {
    			class_CraftPlayer = NMSUtils.getHandle(UUIDUtils.getOfflinePlayerfromUUID(uuid).getPlayer()).getClass();
    			try {
    				return (GameProfile) class_CraftPlayer_getProfileMethod.invoke(class_CraftPlayer);
    			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}	
		return null;
	}
	
	public static GameProfile getGameProfile(OfflinePlayer player) {
		if(player.getPlayer()!=null) {
			if(NMSUtils.getHandle(player.getPlayer())!=null) {
				class_CraftPlayer = NMSUtils.getHandle(player.getPlayer()).getClass();
				try {
					return (GameProfile) class_CraftPlayer_getProfileMethod.invoke(class_CraftPlayer);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		return null;
	}
	
}
 