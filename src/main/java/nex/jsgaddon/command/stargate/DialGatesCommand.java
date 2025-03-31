package nex.jsgaddon.command.stargate;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.PermissionAPI;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.utils.DialGate;
import tauri.dev.jsg.stargate.EnumDialingType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;
import static nex.jsgaddon.loader.StargateAddressList.ADDRESS_MAP;

public class DialGatesCommand extends AbstractJSGACommand {

    @Override
    @Nonnull
    public String getName() {
        return "dialgates";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Establishes a connection between two gates.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "dialgates <origin> <destination> [NORMAL/FAST/NOX]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EnumDialingType dialTypeParse = EnumDialingType.NOX;
        switch (args.length) {
            case 0:
            case 1:
            case 2: {
                DialGate.dialGates(sender, args[0].replace("-", " "), args[1].replace("-", " "), dialTypeParse, true);
                return;
            }
            case 3: {
                if (!args[2].equalsIgnoreCase("normal") && !args[2].equalsIgnoreCase("fast") && !args[2].equalsIgnoreCase("nox"))  {
                    baseCommand.sendErrorMess(sender, "Invalid dial type");
                    return;
                }
                dialTypeParse = EnumDialingType.valueOf(args[2].toUpperCase());
                DialGate.dialGates(sender, args[0].replace("-", " "), args[1].replace("-", " "), dialTypeParse, true);
                return;
            }
            default: {
                baseCommand.sendUsageMess(sender, this);
                break;
            }
        }

    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (!PermissionAPI.hasPermission((EntityPlayer) sender.getCommandSenderEntity(), "jsgaddon" + "." + getName())) {
            return Collections.emptyList();
        }
        switch (args.length) {
            case 1:
            case 2:
                return getListOfStringsMatchingLastWord(args, ADDRESS_MAP.keySet());
            case 3:
                return Lists.newArrayList("NORMAL", "FAST", "NOX");
            default:
                return Collections.emptyList();
        }
    }
}
