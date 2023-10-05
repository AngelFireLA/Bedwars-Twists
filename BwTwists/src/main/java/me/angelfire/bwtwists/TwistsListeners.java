package me.angelfire.bwtwists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.gameplay.TeamAssignEvent;
import com.andrei1058.bedwars.api.events.player.PlayerReSpawnEvent;

public class TwistsListeners implements Listener{

	   private final FileConfiguration config;
	   private final BwTwists plugin;

	   public TwistsListeners(FileConfiguration config, BwTwists plugin) {
	      this.config = config;
	      this.plugin = plugin;
	   }

    @EventHandler
    public void whenPlayerDamagePlayer(EntityDamageByEntityEvent event) {

    	if (event.getDamager() instanceof Player){
    		if(config.getString("twists.disabled-pvp").equalsIgnoreCase("true")) {

                if (event.getEntity() instanceof Player) {
                    event.setCancelled(true);
                }
            }
    		if(config.getString("twists.more-knockback").equalsIgnoreCase("true")) {
    			Entity p = event.getEntity();
    			Player damager = (Player) event.getDamager();
                p.setVelocity(damager.getLocation().getDirection().setY(0).normalize().multiply(2));
    		}
    	}
    }
    Map<String, Collection<PotionEffect>> effects = new HashMap<>();
    Map<String, Collection<PotionEffect>> speed_effects = new HashMap<>();

    @EventHandler
    public void whenPlayerDies(PlayerDeathEvent event) {
    	Player player = event.getEntity();
    	if(config.getString("twists.random-effects").equalsIgnoreCase("true")) {
    		effects.put(player.getName(), player.getActivePotionEffects());
    	}
    	if(config.getString("twists.speed-boost-on-death").equalsIgnoreCase("true")) {
    		speed_effects.put(player.getName(), player.getActivePotionEffects());
    	}
    }


    @EventHandler
    public void whenPlayerRespawn(PlayerReSpawnEvent event) {
    	Player player = event.getPlayer();
    	if(config.getString("twists.random-effects").equalsIgnoreCase("true")) {
        	int repeatingTask4 = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

    			@Override
				public void run() {
	        		if (effects.containsKey(player.getName())) {
	        		    //Get the stored effects from the map
	        		    Collection<PotionEffect> effectss = effects.get(player.getName());

	        		    //Loop through each effect
	        		    for (PotionEffect effect : effectss) {
	        		    	player.addPotionEffect(effect);
	        		    }
	        		    //Remove the player's UUID from the map
	        		    effects.remove(player.getName());
	        		  }
    			}
    		}, 25);

    	}
    	if(config.getString("twists.speed-boost-on-death").equalsIgnoreCase("true")) {
        	int repeatingTask5 = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

    			@Override
				public void run() {
	        		if (speed_effects.containsKey(player.getName())) {
	        		    //Get the stored effects from the map
	        		    Collection<PotionEffect> effectss = speed_effects.get(player.getName());

	        		    //Loop through each effect
	        		    for (PotionEffect effect : effectss) {
	        		      //Check if the effect is speed
	        		      if (effect.getType() == PotionEffectType.SPEED) {
	        		        //Get the current level of speed
	        		        int level = effect.getAmplifier();
	        		        //Increase the level by one
	        		        level++;
	        		        //Apply the new effect to the player
	        		        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level));
	        		      }
	        		      else {
	        		        //Apply the other effects to the player as they are
	        		        player.addPotionEffect(effect);
	        		      }
	        		    }
	        		    //Remove the player's UUID from the map
	        		    speed_effects.remove(player.getName());
	        		  }
    	        	if(player.hasPotionEffect(PotionEffectType.SPEED)) {
    	            	for (PotionEffect effect : player.getActivePotionEffects()) {
    	            		if(effect.getType().equals(PotionEffectType.SPEED)) {
    	            			int amplifier = effect.getAmplifier();
    	            			player.removePotionEffect(PotionEffectType.SPEED);
    	            			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier+1, false, false));
    	            		}
    	            	}
    	        	}
    	        	else {
    	        		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    	        	}
    			}
    		}, 30);
    	}
    }

    int repeatingTask1 = 0;
    int repeatingTask2 = 0;
    int repeatingTask3 = 0;
    Boolean teamsAssigned = false;
    @EventHandler
    public void whenTeamsAreAssigned(TeamAssignEvent event) {
    	if(!teamsAssigned) {
    		whenMatchStart();
    		teamsAssigned = true;
    	}
    }

    public void whenMatchStart() {
    	if(config.getString("twists.random-effects").equalsIgnoreCase("true")) {
            	repeatingTask1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

        			@Override
    				public void run() {
        				for(Player player : Bukkit.getOnlinePlayers()) {
        					PotionEffectType[] effects = PotionEffectType.values();
        					PotionEffectType random_effect = effects[new Random().nextInt(effects.length)];
        					player.sendMessage("Vous avez reçu un effet de potion aléatoire !");
        					player.addPotionEffect(new PotionEffect(random_effect, 6000,new Random().nextInt(3), false, false));
        				}

        			}
        		}, 2400, 2400);
    		}
    	if(config.getString("twists.random-mob-spawns").equalsIgnoreCase("true")) {
    		repeatingTask2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

    			@Override
				public void run() {
    				for (Player player : Bukkit.getOnlinePlayers()) {
                        EntityType[] entities = EntityType.values();
                        EntityType entity = entities[new Random().nextInt(entities.length)];
                        player.getWorld().spawnEntity(player.getLocation(), entity);
    					player.sendMessage("Un mob aléatoire a été spawn, cette fois c'est" + entity.name() + "!");

                    }

    			}
    		}, 2400, 2400);
    	}
    	if(config.getString("twists.position-swapping").equalsIgnoreCase("true")) {
    		repeatingTask3 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

    			@Override
				public void run() {
    				List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    				for (Player player : players) player.sendMessage("Vous avez été swap de position !");
                    int size = players.size();
                    if (size % 2 != 0) {
                        players.add(null);
                        size++;
                    }
                    for (int i = 0; i < size; i += 2) {
                        Player player1 = players.get(i);
                        Player player2 = players.get(i + 1);
                        if (player1 != null && player2 != null) {
                            Location location1 = player1.getLocation();
                            Location location2 = player2.getLocation();
                            player1.teleport(location2);
                            player2.teleport(location1);
                        }
                    }

    			}
    		}, 2400, 2400);
    	}
    }

    @EventHandler
    public void whenGameEnds(GameEndEvent event) {
    	if(config.getString("twists.random-effects").equalsIgnoreCase("true")) {
    		Bukkit.getScheduler().cancelTask(repeatingTask1);
    		teamsAssigned = false;
    	    int repeatingTask1 = 0;
    	    int repeatingTask2 = 0;
    	    int repeatingTask3 = 0;
    	}
    	if(config.getString("twists.random-mob-spawns").equalsIgnoreCase("true")) {
    		Bukkit.getScheduler().cancelTask(repeatingTask2);
    		teamsAssigned = false;
    	    int repeatingTask1 = 0;
    	    int repeatingTask2 = 0;
    	    int repeatingTask3 = 0;
    	}
    	if(config.getString("twists.position-swapping").equalsIgnoreCase("true")) {
    		Bukkit.getScheduler().cancelTask(repeatingTask3);
    		teamsAssigned = false;
    	    int repeatingTask1 = 0;
    	    int repeatingTask2 = 0;
    	    int repeatingTask3 = 0;
    	}
    	teamsAssigned = false;
        int repeatingTask1 = 0;
        int repeatingTask2 = 0;
        int repeatingTask3 = 0;
    }

    @EventHandler
    public void whenPlayerSneak(PlayerToggleSneakEvent event) {

    	if(config.getString("twists.blocked-sneak").equalsIgnoreCase("true")) {
            Player player = event.getPlayer();
            if (player.isSneaking()) {
                Location location = player.getLocation();
                location.getWorld().createExplosion(location, 4F);
            }
    	}
    }

}
