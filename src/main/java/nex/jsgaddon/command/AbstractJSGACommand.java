package nex.jsgaddon.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.server.permission.PermissionAPI;
import tauri.dev.jsg.command.AbstractJSGCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

public abstract class AbstractJSGACommand extends AbstractJSGCommand {
    public AbstractJSGACommand() {
        super(JSGACommand.INSTANCE);
    }
    private boolean permissionHandlerPresent() {
        return Loader.isModLoaded("forgeessentials");
    }
    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return getGeneralUsage();
    }


    @Nonnull
    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (permissionHandlerPresent()) {
            return PermissionAPI.hasPermission((EntityPlayer) sender.getCommandSenderEntity(), "jsgaddon" + "." + getName());
        } else {
            return sender.canUseCommand(getRequiredPermissionLevel(), getName());
        }
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int index) {
        return false;
    }

}
