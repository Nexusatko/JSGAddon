package nex.jsgaddon.command.ring;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.utils.FindNearestTile;
import nex.jsgaddon.utils.GetEnergyRequired;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.power.general.LargeEnergyStorage;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.transportrings.TransportResult;
import tauri.dev.jsg.transportrings.TransportRings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class RingNearCommand extends AbstractJSGACommand {
    @Nonnull
    @Override
    public String getDescription() {
        return "Calls nearest ringset to nearest ringset.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "ringnear";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @Nonnull
    public String getName() {
        return "ringnear";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        TileEntity originRingTile = FindNearestTile.runByCLassRings(sender.getEntityWorld(), sender.getPosition(), TransportRingsAbstractTile.class, 20, 20);
        if (originRingTile == null) {
            baseCommand.sendErrorMess(sender, "Can't find rings in your radius.");
            return;
        }
        TransportRingsAbstractTile originRings = (TransportRingsAbstractTile) originRingTile;
        TransportRings nearestRings = null;
        double minDistance = Double.MAX_VALUE;

        for (TransportRings rings : originRings.ringsMap.values()) {
            double distance = originRings.getPos().distanceSq(rings.getPos());

            if (distance < minDistance) {
                minDistance = distance;
                nearestRings = rings;
            }
        }
        if (nearestRings == null) {
            baseCommand.sendErrorMess(sender, "Can't find destination rings in your radius.");
            return;
        }
        EnergyRequiredToOperate neededEnergyToOpen = GetEnergyRequired.getEnergyRequiredToDialRings(originRings.getRings(), nearestRings);
        LargeEnergyStorage energyStorage = (LargeEnergyStorage) originRings.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            baseCommand.sendErrorMess(sender, "Can't find EnergyStorage of origin rings!");
            return;
        }
        energyStorage.receiveEnergy(neededEnergyToOpen.keepAlive * 120 + neededEnergyToOpen.energyToOpen, false);
        TransportResult transportResult = originRings.dialNearestRings(true);
        switch (transportResult) {
            case OK:
                baseCommand.sendSuccessMess(sender, "Successfully dialed nearest rings.");
                return;
            case BUSY:
            case BUSY_TARGET:
                baseCommand.sendErrorMess(sender, "Origin or destination rings are busy.");
                return;
            case OBSTRUCTED:
                baseCommand.sendErrorMess(sender, "Origin rings are obstructed.");
                return;
            case OBSTRUCTED_TARGET:
                baseCommand.sendErrorMess(sender, "Destination rings are obstructed.");
                return;
            case NOT_ENOUGH_POWER:
                baseCommand.sendErrorMess(sender, "Origin rings have not enough power!");
                return;
            default:
                baseCommand.sendErrorMess(sender, "Transport attempt result: " + transportResult);
        }
    }
}
