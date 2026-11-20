package com.lemonlightmc.zenith.unfinished;

import org.bukkit.Particle;

public record ParticleData<T>(
        Particle particle,
        T data) {
}
