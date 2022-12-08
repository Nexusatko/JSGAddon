package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

public class IrisBypassCommand extends CommandBase {
    @Override
    public String getName() {
        return "iris";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/iris";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        TileEntity tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);

        if (tileEntity == null) {
            sender.sendMessage(new TextComponentString("Can't find Stargate in your radius."));
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

