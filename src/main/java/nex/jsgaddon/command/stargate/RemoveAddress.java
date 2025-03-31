package nex.jsgaddon.command.stargate;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import nex.jsgaddon.command.AbstractJSGACommand;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class RemoveAddress extends AbstractJSGACommand {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Nonnull
    @Override
    public String getName() {
        return "removeaddress";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Removes address from addresslist.json";
    }

    @Nonnull
    @Override
    public String getGeneralUsage() {
        return "removeaddress <name of address>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            baseCommand.sendUsageMess(sender, this);
            return;
        }

        String name = args[0].replace("-", " ");
        File configFile = new File("config/jsgaddon/addresslist.json");

        try {
            Map<String, Map<String, ArrayList<String>>> addressMap = readAddresses(configFile);

            if (!addressMap.containsKey(name)) {
                baseCommand.sendErrorMess(sender, "Address '" + name + "' not found!");
                return;
            }

            addressMap.remove(name);

            try (Writer writer = new FileWriter(configFile)) {
                gson.toJson(addressMap, writer);
            }

            baseCommand.sendSuccessMess(sender, "Address '" + name + "' removed from addresslist!");
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
