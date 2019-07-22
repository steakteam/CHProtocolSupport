package tk.itstake.chprotocolsupport.util;

import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.natives.interfaces.Mixed;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JunHyeong Lim on 2019-07-20
 */
public class CHProtocolVersions {
    public static final String PROTOCOL_LIST_STR = buildProtocolListString();
    private static final Map<ProtocolVersion, Integer> orderCaches = new HashMap<>();

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

    public static ProtocolVersion getProtocolVersion(Mixed argument, Target t) {
        String query = argument.val();
        try {
            return ProtocolVersion.valueOf(query);
        } catch (IllegalArgumentException ex) {
            throw new CREIllegalArgumentException("Offline player or unknown protocol name.\n" +
                    "Available protocol names: " + PROTOCOL_LIST_STR, t, ex);
        }
    }

    public static ProtocolVersion getProtocolVersion(MCPlayer player, Target t) {
        Object handle = player.getHandle();
        try {
            return ProtocolSupportAPI.getProtocolVersion((Player) handle);
        } catch (Exception ex) {
            String message = String.format("Expected %s but received %s.",
                    "org.bukkit.entity.Player", handle.getClass().getName());
            throw new CREIllegalArgumentException(message, t, ex);
        }
    }

    public static CArray parseToCArray(ProtocolVersion version, Target t) {
        CArray array = CArray.GetAssociativeArray(t);
        array.set("id", new CInt(version.getId(), t), t);
        array.set("order", new CInt(getOrder(version, t), t), t);
        array.set("name", version.getName(), t);
        array.set("type", version.getProtocolType().name(), t);
        array.set("internalname", version.name(), t);
        return array;
    }
}
