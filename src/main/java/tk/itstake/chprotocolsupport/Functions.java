package tk.itstake.chprotocolsupport;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.exceptions.CRE.CRENotFoundException;
import com.laytonsmith.core.exceptions.CRE.CREPlayerOfflineException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bexco on 2016-03-12.
 */
public class Functions {

    public String docs() {
        return "Functions about ProtocolSupport API";
    }

    private static void checkPlugin(Target t) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {
            throw new CREInvalidPluginException("Can't find ProtocolSupport", t);
        }
    }

    @api
    public static class get_protocol_version extends AbstractFunction {
        private static final String PROTOCOL_LIST_STR = buildProtocolListString();

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREInvalidPluginException.class,
                    CREPlayerOfflineException.class,
                    CRENotFoundException.class,
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        public static String buildProtocolListString() {
            StringBuilder builder = new StringBuilder();
            for (ProtocolVersion value : ProtocolVersion.values()) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(value.name());
            }
            return builder.toString();
        }

        public static ProtocolVersion getProtocolVersion(MCPlayer player, Target t) {
            Object handle = player.getHandle();
            if (handle instanceof Player) {
                return ProtocolSupportAPI.getProtocolVersion((Player) handle);
            }
            throw new CREIllegalArgumentException(
                    String.format("Expected %s but received %s.",
                            Player.class.getName(), handle.getClass().getName()),
                    t
            );
        }

        public static ProtocolVersion getProtocolVersion(Mixed argument, Target t) {
            try {
                return getProtocolVersion(Static.GetPlayer(argument, t), t);
            } catch (Exception ex) {
                // Ignore
            }
            String query = argument.val();
            try {
                return ProtocolVersion.valueOf(query);
            } catch (IllegalArgumentException ex) {
                throw new CREIllegalArgumentException("Offline player or unknown protocol name.\n" +
                        "Available protocol names: " + PROTOCOL_LIST_STR, t, ex);
            }
        }

        @Override
        public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
            checkPlugin(t);
            ProtocolVersion version = args.length >= 1
                    ? getProtocolVersion(args[0], t)
                    : getProtocolVersion(Static.getPlayer(env, t), t);
            return new CString(version.getName(), t);
        }

        @Override
        public String getName() {
            return "get_protocol_version";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{0, 1};
        }

        @Override
        public String docs() {
            return "string {[player|protocolname]} version Returns a Protocol Version.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }

    @api
    public static class get_protocol_data extends AbstractFunction {
        private static final Map<ProtocolVersion, Integer> orderCaches = new HashMap<>();

        public static Integer getOrder(ProtocolVersion version, Target t) {
            return orderCaches.computeIfAbsent(version, v -> {
                try {
                    Field orderIdField = ProtocolVersion.class.getDeclaredField("orderId");
                    orderIdField.setAccessible(true);
                    Object orderId = orderIdField.get(version);
                    Field idField = orderId.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    return idField.getInt(orderId);
                } catch (Exception ex) {
                    throw new CREException("Failed while getting orderId. ProtocolSupport changed?", t, ex);
                }
            });
        }

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREInvalidPluginException.class,
                    CREPlayerOfflineException.class,
                    CRENotFoundException.class,
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
            checkPlugin(t);
            ProtocolVersion version = args.length >= 1
                    ? get_protocol_version.getProtocolVersion(args[0], t)
                    : get_protocol_version.getProtocolVersion(Static.getPlayer(env, t), t);
            CArray array = new CArray(t);
            array.set("id", new CInt(version.getId(), t), t);
            array.set("order", new CInt(getOrder(version, t), t), t);
            array.set("name", new CString(version.getName(), t), t);
            array.set("type", new CString(version.getProtocolType().name(), t), t);
            return array;
        }

        @Override
        public String getName() {
            return "get_protocol_data";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{0, 1};
        }

        @Override
        public String docs() {
            return "array {[player|protocolname]} Returns a protocol data.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }
}
