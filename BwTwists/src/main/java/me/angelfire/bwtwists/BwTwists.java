package me.angelfire.bwtwists;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.angelfire.bwtwists.commands.Twist;

public class BwTwists extends JavaPlugin{
		FileConfiguration config = getConfig();
	   @Override
	   public void onEnable() {
		   saveDefaultConfig();
		   // Register commands
		   getCommand("twist").setExecutor(new Twist(getConfig(), this));
		   getCommand("twist").setTabCompleter(null);
		   getServer().getPluginManager().registerEvents(new TwistsListeners(config, this), this);
	   }

	   @Override
	   public void onDisable() {
	      // Plugin shutdown logic
	   }

}
