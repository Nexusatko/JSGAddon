package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.scheduled.ScheduledTask;
import nex.jsgaddon.scheduled.ScheduledTasksStatic;
import nex.jsgaddon.utils.FindNearestTile;
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

import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;


@SuppressWarnings("DuplicatedCode")
public class NoxCommand extends CommandBase {

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

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "/nox";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws WrongUsageException {
        TileEntity tileEntity = null;
        if (args.length > 1) {
            EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[1]);
            if (player != null) {
                tileEntity = FindNearestTile.runByCLass(player.getEntityWorld(), player.getPosition(), StargateClassicBaseTile.class, 20, 20);
            } else {
                sender.sendMessage(new TextComponentString("Player is either invalid or not online."));
            }
        } else {
            tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        }
        if (tileEntity == null) {
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Enter an address name!"));
            return;
        }
        StargateClassicBaseTile casted = (StargateClassicBaseTile) tileEntity;
        JsonStargateAddress foundAddress = findAddress(args[0].replace("-", " "), casted.getSymbolType());

        if (foundAddress == null) {
            sender.sendMessage(new TextComponentString("Invalid address name!"));
            return;
        }
        if (!casted.getStargateState().idle() || casted.getDialedAddress().size() > 0) {
            throw new WrongUsageException("Gate is busy");
        }
        int time = 0;
        sender.sendMessage(new TextComponentString("Dialing started: " + args[0]));
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
                    casted.attemptOpenAndFail();
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
