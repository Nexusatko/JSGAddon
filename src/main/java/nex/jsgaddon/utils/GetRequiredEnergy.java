package nex.jsgaddon.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.power.StargateEnergyRequired;

public class GetRequiredEnergy {
    public StargateEnergyRequired getEnergyRequiredToDial(StargatePos localGate, StargatePos targetGate) {
        BlockPos sPos = localGate.gatePos;
        BlockPos tPos = targetGate.gatePos;

        DimensionType sourceDim = localGate.getWorld().provider.getDimensionType();
        DimensionType targetDim = targetGate.getWorld().provider.getDimensionType();

        if (sourceDim == DimensionType.OVERWORLD && targetDim == DimensionType.NETHER)
            tPos = new BlockPos(tPos.getX() * 8, tPos.getY(), tPos.getZ() * 8);
        else if (sourceDim == DimensionType.NETHER && targetDim == DimensionType.OVERWORLD)
            sPos = new BlockPos(sPos.getX() * 8, sPos.getY(), sPos.getZ() * 8);

        double distance = (int) sPos.getDistance(tPos.getX(), tPos.getY(), tPos.getZ());

        if (distance < 5000) distance *= 0.8;
        else distance = 5000 * Math.log10(distance) / Math.log10(5000);

        StargateEnergyRequired energyRequired = new StargateEnergyRequired(tauri.dev.jsg.config.JSGConfig.powerConfig.openingBlockToEnergyRatio, tauri.dev.jsg.config.JSGConfig.powerConfig.keepAliveBlockToEnergyRatioPerTick);
        StargateEnergyRequired energy;
        energy = energyRequired.mul(distance).add(StargateDimensionConfig.getCost(sourceDim, targetDim));

        return energy;
    }
}
