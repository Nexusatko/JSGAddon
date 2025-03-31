package nex.jsgaddon.command.stargate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;
import static nex.jsgaddon.loader.StargateAddressList.ADDRESS_MAP;

public class CloseCommand extends AbstractJSGACommand {
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
        return "close [<stargate>/<player>] <name/nick>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        StargateClassicBaseTile targetGate = null;
        Entity player;
        StargatePos targetGatePos;
        JsonStargateAddress foundAddress;
        if (args.length < 1) {
            if (sender.getCommandSenderEntity() != null) {
                targetGate = (StargateClassicBaseTile) FindNearestTile.runByCLass(
                        sender.getEntityWorld(),
                        sender.getPosition(),
                        StargateClassicBaseTile.class,
                        20,
                        20
                );
            } else {
                baseCommand.sendUsageMess(sender, this);
                return;
            }

        }
        if (args.length > 1) {
            switch (args[0].toLowerCase()) {
                case "stargate":

                    foundAddress = findAddress(args[1].replace("-", " "));
                    if (foundAddress == null) {
                        baseCommand.sendErrorMess(sender, "Invalid Stargate address!");
                        return;
                    }
                    StargateNetwork stargateNetwork = StargateNetwork.get(server.getWorld(0));
                    targetGatePos = stargateNetwork.getStargate(foundAddress);
                    if (targetGatePos == null || !(targetGatePos.getTileEntity() instanceof StargateClassicBaseTile)) {
                        baseCommand.sendErrorMess(sender, "Target gate is invalid!");
                        return;
                    }
                    targetGate = (StargateClassicBaseTile) targetGatePos.getTileEntity();
                    break;
                case "player":
                    player = server.getPlayerList().getPlayerByUsername(args[1]);
                    if (player == null) {
                        baseCommand.sendErrorMess(sender, "Supplied player is either offline or invalid!");
                        return;
                    }
                    targetGate = (StargateClassicBaseTile) FindNearestTile.runByCLass(player.getEntityWorld(), player.getPosition(), StargateClassicBaseTile.class, 20, 20);
                    break;
                default:
                    baseCommand.sendUsageMess(sender, this);
                    return;
            }
        }
        if (targetGate == null) {
            baseCommand.sendErrorMess(sender, "Target gate is invalid!");
            return;
        }
        if (!targetGate.getStargateState().idle()) {
            targetGate.attemptClose(StargateClosedReasonEnum.COMMAND);
            baseCommand.sendSuccessMess(sender, "Wormhole connection terminated.");
        } else {
            baseCommand.sendErrorMess(sender, "No wormhole signature present!");
        }

    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (!checkPermission(server, sender)) {
            return Collections.emptyList();
        }
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, "stargate", "player");
            case 2:
                if (args[0].equalsIgnoreCase("player")) {
                    return getListOfStringsMatchingLastWord(args, server.getPlayerList().getPlayers().stream()
                            .map(EntityPlayer::getName)
                            .collect(Collectors.toList()));
                } else if (args[0].equalsIgnoreCase("stargate")) {
                    return getListOfStringsMatchingLastWord(args, ADDRESS_MAP.keySet());
                }
                break;
        }
        return Collections.emptyList();
    }
}
