package nex.jsgaddon.command.stargate;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import nex.jsgaddon.command.AbstractJSGACommand;
import nex.jsgaddon.utils.FindNearestTile;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddAddress extends AbstractJSGACommand {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Nonnull
    @Override
    public String getName() {
        return "addaddress";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Adds address under defined name into addresslist.json";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "addaddress <name of address>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, ICommandSender sender, @Nonnull String[] args) {

        TileEntity tileEntity = FindNearestTile.runByCLass(sender.getEntityWorld(), sender.getPosition(), StargateClassicBaseTile.class, 20, 20);
        if (tileEntity == null) {
            baseCommand.sendErrorMess(sender, "Can't find Stargate in your radius.");
            return;
        }
        StargateClassicBaseTile originGate = (StargateClassicBaseTile) tileEntity;
        if (args.length == 0) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        String name = args[0].replace("-", " ");
        File configFile = new File("config/jsgaddon/addresslist.json");

        try {
            Map<String, Map<String, ArrayList<String>>> addressMap = readAddresses(configFile);

            if (addressMap.containsKey(name)) {
                baseCommand.sendErrorMess(sender, "This name is already used!");
                return;
            }

            Map<String, ArrayList<String>> map = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                SymbolTypeEnum type = SymbolTypeEnum.valueOf(i);
                ArrayList<String> address = new ArrayList<>();
                for (int u = 0; u < 8; u++) {
                    address.add(Objects.requireNonNull(originGate.getStargateAddress(type)).get(u).getEnglishName());
                }
                address.add(type.getOrigin().getEnglishName());
                map.put(type.toString(), address);
            }

            addressMap.put(name, map);

            try (Writer writer = new FileWriter(configFile)) {
                gson.toJson(addressMap, writer);
            }

            baseCommand.sendSuccessMess(sender, "Stargate's address types logged under name '" + name + "'!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, Map<String, ArrayList<String>>> readAddresses(File configFile) throws IOException {
        try (Reader reader = new FileReader(configFile)) {
            Type type = new TypeToken<Map<String, Map<String, ArrayList<String>>>>() {}.getType();
            return gson.fromJson(reader, type);
        }
    }
}

