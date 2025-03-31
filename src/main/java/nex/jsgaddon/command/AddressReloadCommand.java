package nex.jsgaddon.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import nex.jsgaddon.loader.StargateAddressList;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

public class AddressReloadCommand extends AbstractJSGACommand {
    @Override
    @Nonnull
    public String getName() {
        return "reload-address";
    }
    @Nonnull
    @Override
    public String getDescription() {
        return "Reloads a address list from addresslist.json.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "reload-address";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            StargateAddressList.reload();
        } catch (IOException e) {
            baseCommand.sendErrorMess(sender, e.getMessage());
            return;
        }
        baseCommand.sendSuccessMess(sender, "Addresses successfully reloaded.");

    }
}
