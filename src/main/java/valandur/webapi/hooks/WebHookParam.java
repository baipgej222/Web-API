package valandur.webapi.hooks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.cache.CachedLocation;
import valandur.webapi.cache.CachedPlayer;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;

import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class WebHookParam {

    public enum WebHookParamType {
        STRING, BOOL, INTEGER, DOUBLE, PLAYER, WORLD, DIMENSION, LOCATION, VECTOR3D
    }

    @Setting(comment = "The name of the parameter")
    private String name;
    public String getName() {
        return name;
    }

    @Setting(comment = "The type of the parameter (string/bool/integer/double/player/world/location/vector3d)")
    private WebHookParamType type;
    public WebHookParamType getType() {
        return type;
    }
    
    public Optional<CommandElement> getCommandElement() {
        Text textName = Text.of(name);

        switch (type) {
            case STRING:
                return Optional.of(GenericArguments.string(textName));

            case BOOL:
                return Optional.of(GenericArguments.bool(textName));

            case INTEGER:
                return Optional.of(GenericArguments.integer(textName));

            case DOUBLE:
                return Optional.of(GenericArguments.doubleNum(textName));

            case PLAYER:
                return Optional.of(GenericArguments.player(textName));

            case WORLD:
                return Optional.of(GenericArguments.world(textName));

            case LOCATION:
                return Optional.of(GenericArguments.location(textName));

            case VECTOR3D:
                return Optional.of(GenericArguments.vector3d(textName));
        }

        return Optional.empty();
    }

    public Optional<Tuple<String, String>> getValue(CommandContext args) {
        Optional<String> arg = args.getOne(name);
        if (!arg.isPresent()) return Optional.empty();

        Object obj = arg.get();

        switch (type) {
            case STRING:
            case BOOL:
            case INTEGER:
            case DOUBLE:
                String base = obj.toString();
                return Optional.of(new Tuple<>(base, base));

            case PLAYER:
                UUID pUuid = ((Player)obj).getUniqueId();
                CachedPlayer p = DataCache.getPlayer(pUuid, false).orElse(null);
                return Optional.of(new Tuple<>(p.uuid, JsonConverter.toString(p)));

            case WORLD:
                UUID wUuid = ((WorldProperties)obj).getUniqueId();
                CachedWorld w = DataCache.getWorld(wUuid, false).orElse(null);
                return Optional.of(new Tuple<>(w.uuid, JsonConverter.toString(w)));

            case DIMENSION:
                String t = ((DimensionType)obj).getName();
                return Optional.of(new Tuple<>(t, t));

            case LOCATION:
                String loc = JsonConverter.toString(CachedLocation.copyFrom((Location)obj));
                return Optional.of(new Tuple<>(loc, loc));

            case VECTOR3D:
                String vec = JsonConverter.toString(obj);
                return Optional.of(new Tuple<>(vec, vec));
        }

        return Optional.empty();
    }
}
