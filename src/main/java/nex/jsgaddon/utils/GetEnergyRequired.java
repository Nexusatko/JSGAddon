package nex.jsgaddon.utils;

import net.minecraft.util.math.BlockPos;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.transportrings.TransportRings;

public class GetEnergyRequired {
    public static EnergyRequiredToOperate getEnergyRequiredToDialRings(TransportRings originRings, TransportRings destinationRings) {
        BlockPos sPos = originRings.getPos();
        BlockPos tPos = destinationRings.getPos();

        double distance = (int) sPos.getDistance(tPos.getX(), tPos.getY(), tPos.getZ());

        if (distance < 200) distance *= 0.8;
        else distance = 200 * Math.log10(distance) / Math.log10(200);

        int energyBase = JSGConfig.Rings.power.ringsKeepAliveBlockToEnergyRatioPerTick;
        EnergyRequiredToOperate energyRequired = new EnergyRequiredToOperate(energyBase, energyBase);
        energyRequired = energyRequired.mul(distance);

        return energyRequired;
    }
}
