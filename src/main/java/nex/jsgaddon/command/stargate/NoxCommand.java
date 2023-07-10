package nex.jsgaddon.command.stargate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.command.JSGACommand;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.scheduled.ScheduledTask;
import nex.jsgaddon.scheduled.ScheduledTasksStatic;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.StargateOpenResult;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;
import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;


@SuppressWarnings("DuplicatedCode")
public class NoxCommand extends AbstractJSGACommand {

    @Nullable
    private static JsonStargateAddress findAddress(String addressName, SymbolTypeEnum symbolType) {
        if (ADDRESS_MAP.get(addressName) != null) {
            return ADDRESS_MAP.get(addressName).get(symbolType.toString());
        }
        return null;
    }

    @Override
    @Nonnull
    public String getName() {
        return "nox";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Dials the nearest gate to you or optionally defined player to the chosen destination.";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "nox <name> [nick]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        TileEntity tileEntity = null;
        if (args.length > 1) {
            EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[1]);
            if (player != null) {
                tileEntity = FindNearestTile.runByCLass(player.getEntityWorld(), player.getPosition(), StargateClassicBaseTile.class, 20, 20);
            } else {
                ((JSGACommand) baseCommand).sendErrorMess(sender, new TextComponentString("Player is either invalid or not online."));
            }
        } else {
            tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        }
        if (tileEntity == null) {
            baseCommand.sendErrorMess(sender, "Can't find Stargate in your radius.");
            return;
        }
        if (args.length < 1) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        JsonStargateAddress foundAddress = findAddress(args[0].replace("-", " "), casted.getSymbolType());

        if (foundAddress == null) {
            ((JSGACommand) baseCommand).sendErrorMess(sender, new TextComponentString("Invalid address name!"));
            return;
        }
        if (!casted.getStargateState().idle() || casted.getDialedAddress().size() > 0) {
            baseCommand.sendErrorMess(sender, "Gate is busy!");
        }
        int time = 0;
        ((JSGACommand) baseCommand).sendRunningMess(sender, new TextComponentString("Dialing started: " + args[0]));
        StargateClassicBaseTile foundGate = (StargateClassicBaseTile) Objects.requireNonNull(casted.getNetwork().getStargate(foundAddress)).getTileEntity();
        if (casted instanceof StargateUniverseBaseTile) {
            ((StargateUniverseBaseTile) casted).dialAddress(foundAddress.toImmutable(), foundAddress.size());
        } else {
            int dialed = 0;
            for (SymbolInterface symbol : foundAddress.getAddress()) {
                int maxSymbols = 8;
                DHDAbstractTile dhd = null;
                if (casted instanceof StargatePegasusBaseTile)
                    dhd = ((StargatePegasusBaseTile) casted).getLinkedDHD(casted.getWorld());
                if (casted instanceof StargateMilkyWayBaseTile)
                    dhd = ((StargateMilkyWayBaseTile) casted).getLinkedDHD(casted.getWorld());
                if (dhd != null && !dhd.hasUpgrade(DHDAbstractTile.DHDUpgradeEnum.CHEVRON_UPGRADE))
                    maxSymbols = 6;

                if (dialed < maxSymbols || symbol.origin()) {
                    dialed++;
                    time += 15;
                    ScheduledTasksStatic.add(new ScheduledTask(time, () -> {
                        if (casted.canAddSymbol(symbol)) {
                            casted.addSymbolToAddressDHD(symbol);
                        }
                    }));
                }
            }
            ScheduledTasksStatic.add(new ScheduledTask(time + 40, () -> {
                if (casted instanceof StargatePegasusBaseTile) {
                    casted.addSymbolToAddressDHD(casted.getSymbolType().getOrigin());
                    casted.addSymbolToAddressDHD(casted.getSymbolType().getBRB());
                } else {
                    StargateOpenResult r = casted.attemptOpenAndFail();
                    if (r.ok()) {
                        baseCommand.sendSuccessMess(sender, "Wormhole connected to " + args[0]);
                    } else {
                        if (foundGate.getStargateState().engaged() || foundGate.getStargateState().dialing())
                            baseCommand.sendErrorMess(sender, "Wormhole signature detected at destination address!");
                        else {
                            baseCommand.sendErrorMess(sender, r.toString());
                        }
                    }
                }
            }));
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, ADDRESS_MAP.keySet());
    }
}
