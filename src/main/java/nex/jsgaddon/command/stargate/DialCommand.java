package nex.jsgaddon.command.stargate;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.utils.DialGate;
import tauri.dev.jsg.stargate.EnumDialingType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;
import static nex.jsgaddon.loader.StargateAddressList.ADDRESS_MAP;

public class DialCommand extends AbstractJSGACommand {

    @Override
    @Nonnull
    public String getName() {
        return "dial";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Dials the nearest gate to you or optionally defined player to the chosen destination.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "dial <name> [normal/fast/nox] [nick]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EnumDialingType dialTypeParse = EnumDialingType.NOX;
        EntityPlayer player = null;
        if (args.length < 1) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("NOX") || args[1].equalsIgnoreCase("NORMAL") || args[1].equalsIgnoreCase("FAST")) {
                    dialTypeParse = EnumDialingType.valueOf(args[1].toUpperCase());
            } else {
                try {
                    player = server.getPlayerList().getPlayerByUsername(args[1]);
                } catch (Exception e) {
                    baseCommand.sendErrorMess(sender, "Supplied secondary parameter isn't valid dial type or player.");
                    return;
                }
            }

        } else if (args.length == 3) {
            try {
                dialTypeParse = EnumDialingType.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                baseCommand.sendErrorMess(sender, "Invalid dialing type.");
                return;
            }
            try {
                player = server.getPlayerList().getPlayerByUsername(args[2]);
            } catch (Exception e) {
               baseCommand.sendErrorMess(sender, "Player is either invalid or not online.");
               return;
            }

        } else if (args.length >= 4) {
            baseCommand.sendErrorMess(sender, "Too many arguments supplied, aborting any action.");
            return;
        }
        if (player == null && sender instanceof EntityPlayer) {
            player = (EntityPlayer) sender;
        }
        DialGate.dialGate(sender, player, true, args[0].replace("-", " "), dialTypeParse);

    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (!checkPermission(server, sender)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, ADDRESS_MAP.keySet());
        } else if (args.length == 2) {
            String partialInput = args[1].toLowerCase();

            List<String> matchingPlayers = Arrays.stream(server.getOnlinePlayerNames())
                    .filter(playerName -> playerName.toLowerCase().startsWith(partialInput))
                    .collect(Collectors.toList());

            if (!matchingPlayers.isEmpty()) {
                return matchingPlayers;
            } else {
                return Lists.newArrayList("normal","fast","nox");
            }
        } else if (args.length == 3) {
            String partialInput = args[2].toLowerCase();

            List<String> matchingPlayers = Arrays.stream(server.getOnlinePlayerNames())
                    .filter(playerName -> playerName.toLowerCase().startsWith(partialInput))
                    .collect(Collectors.toList());

            if (!matchingPlayers.isEmpty()) {
                return matchingPlayers;
            } else {
                return Lists.newArrayList("normal","fast","nox");
            }
        }
        return Collections.emptyList();
    }
}
