package koral.lobbyspawn;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.List;
import java.util.Objects;

public final class LobbySpawn extends JavaPlugin implements Listener, CommandExecutor
{
    File LocationFile;
    YamlConfiguration Location;

@EventHandler
      public void onPlayerJoin(PlayerJoinEvent event)
{
    Player player = event.getPlayer();
     spawnTeleport(player);
}


void spawnTeleport(Player player)
{
    final double x = this.Location.getDouble("Location." + ".X" );
    final double y = this.Location.getDouble("Location." + ".Y");
    final double z = this.Location.getDouble("Location." + ".Z");
    final float yaw = (float)this.Location.getLong("Location." + ".yaw");
    final float pitch = (float)this.Location.getLong("Location" + ".pitch");
    final World world = Bukkit.getWorld(this.Location.getString("Location." + ".worldname"));
    final Location spawn = new Location(world, x, y, z, yaw, pitch);
    player.teleport(spawn);
}

@EventHandler
    public void onPlayerDamageByVoid(final EntityDamageEvent event) {
    if (event.getCause().equals((Object) EntityDamageEvent.DamageCause.VOID) && event.getEntity() instanceof Player) {
        event.setCancelled(true);
        final Player player = (Player) event.getEntity();
        player.sendMessage(ChatColor.GRAY + "Przeteleportowano na spawn, ponieważ spadłeś w otchłań.");
        spawnTeleport(player);
    }
}



@Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final Player player = (Player)sender;
        if (label.equalsIgnoreCase("setspawn") && args.length == 0) {
            final double x = player.getLocation().getX();
            final double y = player.getLocation().getY();
            final double z = player.getLocation().getZ();
            final float yaw = player.getLocation().getYaw();
            final float pitch = player.getLocation().getPitch();
            final String worldName = player.getWorld().getName();
            this.Location.set("Location." + ".X", (Object)x);
            this.Location.set("Location." + ".Y", (Object)y);
            this.Location.set("Location." + ".Z", (Object)z);
            this.Location.set("Location." + ".yaw", (Object)yaw);
            this.Location.set("Location." + ".pitch", (Object)pitch);
            this.Location.set("Location." + ".worldname", (Object)worldName);
            this.saveLocationFile();
            player.sendMessage(ChatColor.GRAY + "Ustawiono " + ChatColor.RED + "forcespawn" + ChatColor.GRAY + " w miejscu w którym aktualnie się znajdujesz");
        }


        if (cmd.getName().equalsIgnoreCase("spawn") && args.length == 0) {
            if(LocationFile.exists())
            {
              spawnTeleport(player);
            }
            else {
                player.sendMessage(ChatColor.RED + "SPAWN NIE JEST USTAWIONY");
                return true;
            }

        }
        return true;
    }

    public LobbySpawn() {
        this.LocationFile = new File(this.getDataFolder(), "Location.yml");
        this.Location = YamlConfiguration.loadConfiguration(this.LocationFile);
    }
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);
        this.getCommand("setspawn");
        this.getCommand("spawn");
    }
    @Override
    public void onDisable() {
        this.saveLocationFile();
    }

    void saveLocationFile() {
        try {
            this.Location.save(this.LocationFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}