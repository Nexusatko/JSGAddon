package nex.jsgaddon.command.stargate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CloseCommand extends AbstractJSGACommand {
    @Override
    @Nonnull
    public String getName() {
        return "close";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Closes the nearest gate";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "close";
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
            baseCommand.sendErrorMess(sender, "Can't find Stargate in your radius.");
            return;

        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        if (!casted.getStargateState().idle() || !casted.getStargateState().dialing() || casted.randomIncomingIsActive) {
            casted.attemptClose(StargateClosedReasonEnum.COMMAND);
            sender.sendMessage(new TextComponentString("Wormhole connection was successfully terminated."));
            return;
        }
        sender.sendMessage(new TextComponentString("Stargate is closed or dialing!"));
    }
}
