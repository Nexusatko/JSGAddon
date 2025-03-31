package nex.jsgaddon.command.stargate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.command.JSGACommand;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class IrisBypassCommand extends AbstractJSGACommand {
    @Override
    @Nonnull
    public String getName() {
        return "iris";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Opens iris on the destination gate.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "iris";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        TileEntity tileEntity;
        if (args.length > 0) {
            EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[0]);
            if (player != null) {
                tileEntity = FindNearestTile.runByCLass(player.getEntityWorld(), player.getPosition(), StargateClassicBaseTile.class, 20, 20);
            } else {
                ((JSGACommand) baseCommand).sendErrorMess(sender, new TextComponentString("Player is either invalid or not online."));
                return;
            }
        } else {
            tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        }
        if (tileEntity == null) {
            baseCommand.sendErrorMess(sender, "Can't find Stargate in your radius.");
            return;
        }
        if (tileEntity instanceof StargateClassicBaseTile) {
            StargateClassicBaseTile originGate = (StargateClassicBaseTile) tileEntity;

            StargatePos targetGate = originGate.getNetwork().getStargate(originGate.getDialedAddress());
            if (targetGate != null) {
                StargateAbstractBaseTile targetTile = targetGate.getTileEntity();
                if (targetTile instanceof StargateClassicBaseTile) {
                    StargateClassicBaseTile originGateTargetTile = (StargateClassicBaseTile) targetTile;
                    if (!originGateTargetTile.hasIris()) {
                        ((JSGACommand) baseCommand).sendErrorMess(sender, new TextComponentString("Iris is not present, you can enter."));
                    } else if (originGateTargetTile.getIrisState() == EnumIrisState.CLOSED || originGateTargetTile.getIrisState().equals(EnumIrisState.OPENING)) {
                        originGateTargetTile.toggleIris();
                        ((JSGACommand) baseCommand).sendSuccessMess(sender, new TextComponentString("Iris is now opening!"));
                    } else {
                        ((JSGACommand) baseCommand).sendErrorMess(sender, new TextComponentString("Iris is already open!"));
                    }
                }
            }
        }
    }

}

