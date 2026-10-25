package dev.tnttunnel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class TNTTunnel extends JavaPlugin {
    private BukkitTask running;
    @Override public void onDisable() { if (running != null) running.cancel(); }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                        @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
        if (!player.hasPermission("tnttunnel.use")) { player.sendMessage("No permission."); return true; }
        if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            if (running != null) { running.cancel(); running = null; player.sendMessage("TNT tunnel stopped (already spawned TNT can still explode)."); }
            else player.sendMessage("No tunnel is running.");
            return true;
        }
        if (args.length != 1) { player.sendMessage("Usage: /tnttunnel <10-1000|stop>"); return true; }
        int length;
        try { length = Integer.parseInt(args[0]); } catch (NumberFormatException e) { player.sendMessage("Length must be a number."); return true; }
        if (length < 10 || length > 1000) { player.sendMessage("Length must be between 10 and 1000."); return true; }
        if (running != null) { player.sendMessage("A tunnel is already running. Use /tnttunnel stop."); return true; }
        World world = player.getWorld();
        if (world.getEnvironment() != World.Environment.NETHER) { player.sendMessage("Use this in the Nether."); return true; }
        Vector direction = player.getLocation().getDirection().setY(0);
        if (direction.lengthSquared() < 0.01) direction = new Vector(0, 0, 1);
        direction.normalize();
        Location start = player.getLocation().clone().add(0, 1, 0);
        final int maxSteps = (int) Math.ceil(length / 4.0);
        final int[] step = {0};
        player.sendMessage("Starting real TNT blasts over " + length + " blocks. Stay clear! /tnttunnel stop cancels future TNT.");
        running = getServer().getScheduler().runTaskTimer(this, () -> {
            if (step[0] >= maxSteps) {
                running.cancel(); running = null;
                player.sendMessage("TNT tunnel spawning finished.");
                return;
            }
            Location pos = start.clone().add(direction.clone().multiply(4.0 * (step[0] + 1)));
            step[0]++;
            if (!world.isChunkLoaded(pos.getBlockX() >> 4, pos.getBlockZ() >> 4)) {
                // Avoid forcing distant chunks to load and unexpectedly lagging the server.
                running.cancel(); running = null;
                player.sendMessage("Stopped at an unloaded chunk. Move forward and start again.");
                return;
            }
            // Two TNT entities per step produce overlapping explosions, while limiting lag.
            for (int i = 0; i < 2; i++) {
                Location spawn = pos.clone().add(0, i * 1.5, 0);
                world.spawn(spawn, TNTPrimed.class, tnt -> {
                    tnt.setFuseTicks(25);
                    tnt.setVelocity(new Vector(0, 0, 0));
                });
            }
        }, 0L, 8L);
        return true;
    }
}
