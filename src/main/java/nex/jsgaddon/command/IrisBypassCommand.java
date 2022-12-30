package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.EnumIrisMode;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class IrisBypassCommand extends CommandBase {
    @Override
    @Nonnull
    public String getName() {
        return "iris";
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "/iris";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        TileEntity tileEntity = null;
        if (args.length > 0) {
            EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[0]);
            if (player != null) {
                tileEntity = FindNearestTile.runByCLass(player.getEntityWorld(), player.getPosition(), StargateClassicBaseTile.class, 20, 20);
            } else {
                sender.sendMessage(new TextComponentString("Player is either invalid or not online."));
            }
        } else {
            tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        }
        if (tileEntity == null) {
            return;
        }
        if (tileEntity instanceof StargateClassicBaseTile) {
            StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;

            StargatePos targetGate = casted.getNetwork().getStargate(casted.getDialedAddress());
            if (targetGate != null) {
                StargateAbstractBaseTile targetTile = targetGate.getTileEntity();
                if (targetTile instanceof StargateClassicBaseTile) {
                    StargateClassicBaseTile castedTargetTile = (StargateClassicBaseTile) targetTile;
                    if (castedTargetTile.getIrisState() == EnumIrisState.CLOSED) {
                        if (castedTargetTile.getIrisMode() == EnumIrisMode.CLOSED || castedTargetTile.getIrisMode() == EnumIrisMode.OC) {
                            castedTargetTile.setIrisMode(EnumIrisMode.AUTO);
                        }
                        castedTargetTile.toggleIris();
                        sender.sendMessage(new TextComponentString("Iris is now open!"));
                    } else {
                        sender.sendMessage(new TextComponentString("Iris is already open!"));
                    }
                }
            }
        }
    }

}

