package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

public class CloseCommand extends CommandBase {
    @Override
    public String getName() {
        return "close";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/close";
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
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        casted.attemptClose(StargateClosedReasonEnum.REQUESTED);

    }
}
