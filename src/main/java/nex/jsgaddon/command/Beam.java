package nex.jsgaddon.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import nex.jsgaddon.loader.JsonStargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static nex.jsgaddon.loader.FromFile.ADDRESS_MAP;

public class Beam extends CommandBase {
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
        return "beam";
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "/beam";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        /*TileEntity tileEntity = null;
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
        JsonStargateAddress foundAddress = findAddress(args[0].replace("-", " "));
        StargatePos targetGate = StargateNetwork.get(sender.getEntityWorld()).getStargate(foundAddress);
        if (targetGate != null) {
            StargateAbstractBaseTile targetTile = targetGate.getTileEntity();
            if (targetTile instanceof StargateClassicBaseTile) {
                StargateClassicBaseTile castedTargetTile = (StargateClassicBaseTile) targetTile;


                if (foundAddress == null) {
                    sender.sendMessage(new TextComponentString("Invalid address name!"));
                    return;
                }
                Entity player = sender.getCommandSenderEntity();
                //List<EntityPlayer> entities = Objects.requireNonNull(player).world.getEntitiesWithinAABB(EntityPlayer.class, player.getEntityBoundingBox().expand(10, 10, 10));
                 for (int i = 0; i < 4; i++) {
                    EntityPlayer entityPlayer = entities.get(i);
                    if (entityPlayer != null) {
                        server.commandManager.executeCommand(sender, "/particle cloud " + player.posX + " " + player.posY + " " + player.posZ + " 3 10 3 0.00001 20000");
                        player.setPosition(castedTargetTile.getGateCenterPos().getX(), castedTargetTile.getGateCenterPos().getY(), castedTargetTile.getGateCenterPos().getZ());
                    } else {
                        return;
                    }
                }
                assert player != null;
                server.commandManager.executeCommand(sender, "/particle cloud " + player.posX + " " + player.posY + " " + player.posZ + " 3 10 3 0.00001 20000");
                player.setPosition(castedTargetTile.getGateCenterPos().getX(), castedTargetTile.getGateCenterPos().getY(), castedTargetTile.getGateCenterPos().getZ());
                server.commandManager.executeCommand(sender, "/particle cloud " + player.posX + " " + player.posY + " " + player.posZ + " 3 10 3 0.00001 20000");

            }
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, ADDRESS_MAP.keySet());
    */
    }
}
