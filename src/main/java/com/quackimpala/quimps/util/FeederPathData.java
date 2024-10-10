package com.quackimpala.quimps.util;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;

public class FeederPathData {
    private final BlockPos feederPos;
    private Path path;

    public FeederPathData(BlockPos feederPos, Path path) {
        this.feederPos = feederPos;
        this.path = path;
    }

    public BlockPos feederPos() {
        return feederPos;
    }

    public Path path() {
        return path;
    }

    public void reroute(Path path) {
        this.path = path;
    }
}
