package me.gabristudio.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleAnimator {
    public static void spawnCircle(Player player, String particleName, int count, double radius) {
        Particle particle = Particle.valueOf(particleName.toUpperCase());
        Location loc = player.getLocation();
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            loc.getWorld().spawnParticle(particle, loc.clone().add(x, 0.5, z), 1, 0, 0, 0, 0);
        }
    }
}
