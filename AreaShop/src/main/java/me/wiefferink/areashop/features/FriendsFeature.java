package me.wiefferink.areashop.features;

import me.wiefferink.areashop.events.askandnotify.AddedFriendEvent;
import me.wiefferink.areashop.events.askandnotify.DeletedFriendEvent;
import me.wiefferink.areashop.regions.GeneralRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;

public class FriendsFeature extends Feature {

	private GeneralRegion region;

	public FriendsFeature(GeneralRegion region) {
		this.region = region;
	}

	/**
	 * Add a friend to the region
	 * @param player The UUID of the player to add
	 * @param by     The CommandSender that is adding the friend, or null
	 * @return true if the friend has been added, false if adding a friend was cancelled by another plugin
	 */
	public boolean addFriend(UUID player, CommandSender by) {
		// Fire and check event
		AddedFriendEvent event = new AddedFriendEvent(region, Bukkit.getOfflinePlayer(player), by);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			plugin.message(by, "general-cancelled", event.getReason(), this);
			return false;
		}

		Set<String> friends = new HashSet<>(region.getConfig().getStringList("general.friends"));
		friends.add(player.toString());
		List<String> list = new ArrayList<>(friends);
		region.setSetting("general.friends", list);
		return true;
	}

	/**
	 * Delete a friend from the region
	 * @param player The UUID of the player to delete
	 * @param by     The CommandSender that is adding the friend, or null
	 * @return true if the friend has been added, false if adding a friend was cancelled by another plugin
	 */
	public boolean deleteFriend(UUID player, CommandSender by) {
		// Fire and check event
		DeletedFriendEvent event = new DeletedFriendEvent(region, Bukkit.getOfflinePlayer(player), by);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			plugin.message(by, "general-cancelled", event.getReason(), this);
			return false;
		}

		Set<String> friends = new HashSet<>(region.getConfig().getStringList("general.friends"));
		friends.remove(player.toString());
		List<String> list = new ArrayList<>(friends);
		if(list.isEmpty()) {
			region.setSetting("general.friends", null);
		} else {
			region.setSetting("general.friends", list);
		}
		return true;
	}

	/**
	 * Get the list of friends added to this region
	 * @return Friends added to this region
	 */
	public Set<UUID> getFriends() {
		HashSet<UUID> result = new HashSet<>();
		for(String friend : region.getConfig().getStringList("general.friends")) {
			try {
				UUID id = UUID.fromString(friend);
				result.add(id);
			} catch(IllegalArgumentException e) {
				// Don't add it
			}
		}
		return result;
	}

	/**
	 * Get the list of friends added to this region
	 * @return Friends added to this region
	 */
	public Set<String> getFriendNames() {
		HashSet<String> result = new HashSet<>();
		for(UUID friend : getFriends()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(friend);
			if(player != null) {
				result.add(player.getName());
			}
		}
		return result;
	}

	/**
	 * Remove all friends that are added to this region
	 */
	public void clearFriends() {
		region.setSetting("general.friends", null);
	}

}
