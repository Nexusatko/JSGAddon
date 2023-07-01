package nex.jsgaddon.command.stargate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.loader.JsonStargateAddress;
import nex.jsgaddon.utils.FindNearestTile;
import nex.jsgaddon.utils.GetRequiredEnergy;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.stargate.StargateOpenResult;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;
import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;

public class CheckAddress extends AbstractJSGACommand {

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
        return "checkaddress";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Checks status and availablity of the destination stargate by address";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "checkaddress <name>";
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
                baseCommand.sendErrorMess(sender, "Player is either invalid or not online.");
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
        StargateClassicBaseTile foundGate = (StargateClassicBaseTile) Objects.requireNonNull(casted.getNetwork().getStargate(foundAddress)).getTileEntity();
        if (foundAddress == null) {
            baseCommand.sendErrorMess(sender, "Invalid address name!");
            return;
        }
        StargateOpenResult status = casted.checkAddressAndEnergy(foundAddress);
        EnergyRequiredToOperate energy = GetRequiredEnergy.getEnergyRequiredToDial(Objects.requireNonNull(casted.getNetwork().getStargate(casted.getStargateAddress(casted.getSymbolType()))), Objects.requireNonNull(casted.getNetwork().getStargate(foundAddress)));
        String name = String.format("Energy requirements and status for : §6" + args[0]);
        String requiredEnergy = String.format("§fRequired energy to dial §7: §e%,d §bRF", energy.energyToOpen);
        String keepEnergy = String.format("§fRequired energy to keep connection §7: §e%,d §bRF§7/§8t", energy.keepAlive);
        String info;
        if (foundGate.getStargateState().engaged() || foundGate.getStargateState().dialing()) {
            info = "Destination Stargate status §7: §rWormhole signature present";
        } else if (!foundAddress.validate()) {
            info = "Destination Stargate status §7: §rStargate not found";
        } else {
            info = String.format("Destination Stargate status §7: " + status);
        }
        baseCommand.sendInfoMess(sender, name + "\n" + requiredEnergy + "\n" + keepEnergy + "\n" + info);
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, ADDRESS_MAP.keySet());
    }
}
