package nex.jsgaddon.command.stargate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.loader.JsonStargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.teleportation.TeleportHelper;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;
import static nex.jsgaddon.loader.StargateAddressList.ADDRESS_MAP;

public class TeleportCommand extends AbstractJSGACommand {

    @Nullable
    private static JsonStargateAddress findAddress(String addressName) {
        if (ADDRESS_MAP.get(addressName) != null) {
            return ADDRESS_MAP.get(addressName).get(SymbolTypeEnum.MILKYWAY.toString());
        }
        return null;
    }

    @Override
    @Nonnull
    public String getName() {
        return "tp";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Teleports you or player to the chosen Stargate.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "tp <name> [nick]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Entity player = null;
        if (args.length == 1) {
            if (sender.getCommandSenderEntity() == null) {
                baseCommand.sendUsageMess(sender,this);
                return;
            }
            player = sender.getCommandSenderEntity();
        } else if (args.length == 2) {
            try {
                player = server.getPlayerList().getPlayerByUsername(args[1]);
            } catch (Exception e) {
                baseCommand.sendErrorMess(sender, "Supplied player is either offline or invalid!");
            }
        } else {
            baseCommand.sendUsageMess(sender,this);
            return;
        }
        StargatePos targetGatePos;
        StargateNetwork stargateNetwork = StargateNetwork.get(server.getWorld(0));
        JsonStargateAddress foundAddress = findAddress(args[0].replace("-", " "));

        if (player != null) {
            if (foundAddress != null) {
                targetGatePos = stargateNetwork.getStargate(foundAddress);
                if (targetGatePos == null) {
                    baseCommand.sendErrorMess(sender, "Target gate is invalid!");
                    return;
                }
            } else {
                baseCommand.sendErrorMess(sender, "Target gate is invalid!");
                return;
            }
            StargateClassicBaseTile targetGate = (StargateClassicBaseTile) targetGatePos.getTileEntity();
            if (targetGate.getStargateState().engaged() || targetGate.getStargateState().dialing() || targetGate.getStargateState().incoming()) {
                baseCommand.sendErrorMess(sender, "Target gate is being dialed or is engaged! Cancelling to prevent unwanted teleportation or death.");
                return;
            }
            TeleportHelper.teleportEntityToStargate(player, targetGatePos, true);
            if (sender instanceof EntityPlayer) {
                baseCommand.sendSuccessMess(sender, "You were teleported to the pos of " + args[0] + "!");
            } else {
                baseCommand.sendSuccessMess(sender, "Player " + args[1] + " was teleported to the pos of " + args[0] + "!");
            }

        } else {
            baseCommand.sendErrorMess(sender, "Player is offline or invalid!");
        }
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
            String partialInput;
            partialInput = args[1].toLowerCase();

            List<String> matchingPlayers = Arrays.stream(server.getOnlinePlayerNames())
                    .filter(playerName -> playerName.toLowerCase().startsWith(partialInput))
                    .collect(Collectors.toList());

            if (!matchingPlayers.isEmpty()) {
                return matchingPlayers;
            }
        }
        return Collections.emptyList();
    }
}
