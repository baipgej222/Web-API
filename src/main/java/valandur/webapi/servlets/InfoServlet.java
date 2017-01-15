package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.Permission;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class InfoServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "info")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        Platform platform = Sponge.getPlatform();

        JsonObject json = data.getJson();
        json.addProperty("motd", server.getMotd().toPlain());
        json.addProperty("players", server.getOnlinePlayers().size());
        json.addProperty("maxPlayers", server.getMaxPlayers());
        json.addProperty("uptimeTicks", server.getRunningTimeTicks());
        json.addProperty("hasWhitelist", server.hasWhitelist());
        json.addProperty("minecraftVersion", platform.getMinecraftVersion().getName());

        PluginContainer api = platform.getApi();
        JsonObject obj = new JsonObject();
        obj.addProperty("id", api.getId());
        obj.addProperty("name", api.getName());
        Optional<String> version = api.getVersion();
        obj.addProperty("version", version.isPresent() ? version.get() : null);
        Optional<String> descr = api.getDescription();
        obj.addProperty("description", descr.isPresent() ? descr.get() : null);
        Optional<String> url = api.getVersion();
        obj.addProperty("url", url.isPresent() ? url.get() : null);
        JsonArray arr = new JsonArray();
        for (String author : api.getAuthors()) {
            arr.add(new JsonPrimitive(author));
        }
        obj.add("authors", arr);

        json.add("api", obj);
    }
}