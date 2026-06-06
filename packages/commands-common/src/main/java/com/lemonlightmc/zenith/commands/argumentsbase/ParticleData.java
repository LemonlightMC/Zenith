package com.lemonlightmc.zenith.commands.argumentsbase;

import org.bukkit.Particle;

public record ParticleData<T>(
        Particle particle,
        T data) {
}
