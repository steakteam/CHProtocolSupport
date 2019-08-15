package tk.itstake.chprotocolsupport;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.exceptions.CRE.CRENotFoundException;
import com.laytonsmith.core.exceptions.CRE.CREPlayerOfflineException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import org.bukkit.Bukkit;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.utils.i18n.I18NData;
import tk.itstake.chprotocolsupport.util.CHProtocolVersions;


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

    abstract static class ProtocolSupportDependsFunction extends AbstractFunction {
        @Override
        public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
            checkPlugin(t);
            return doExec(t, environment, args);
        }

        protected abstract Mixed doExec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException;
    }

    @api
    public static class get_protocol_version extends ProtocolSupportDependsFunction {

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
        protected Mixed doExec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
            MCPlayer player = args.length >= 1
                    ? Static.GetPlayer(args[0], t)
                    : Static.getPlayer(env, t);
            ProtocolVersion version = CHProtocolVersions.getProtocolVersion(player, t);
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
            return "string {[player]} version Returns a Protocol Version.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }

    @api
    public static class get_protocol_data extends ProtocolSupportDependsFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREInvalidPluginException.class,
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
        protected Mixed doExec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
            ProtocolVersion version = CHProtocolVersions.getProtocolVersion(args[0], t);
            return CHProtocolVersions.parseToCArray(version, t);
        }

        @Override
        public String getName() {
            return "get_protocol_data";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{1};
        }

        @Override
        public String docs() {
            return "array {protocolname} Returns a protocol data.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }

    @api
    public static class pget_protocol_data extends ProtocolSupportDependsFunction {

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
        protected Mixed doExec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
            MCPlayer player = args.length >= 1
                    ? Static.GetPlayer(args[0], t)
                    : Static.getPlayer(env, t);
            ProtocolVersion version = CHProtocolVersions.getProtocolVersion(player, t);
            return CHProtocolVersions.parseToCArray(version, t);
        }

        @Override
        public String getName() {
            return "pget_protocol_data";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{0, 1};
        }

        @Override
        public String docs() {
            return "array {[player]} Returns a protocol data.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }

    @api
    public static class get_i18n_data extends ProtocolSupportDependsFunction {

        @Override
        protected Mixed doExec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
            String message = I18NData.getI18N(args[0].val()).getTranslationString(args[1].val());
            return message != null
                    ? new CString(message, t)
                    : CNull.NULL;
        }

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{};
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return null;
        }

        @Override
        public Version since() {
            return MSVersion.V3_3_4;
        }

        @Override
        public String getName() {
            return "get_i18n_data";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{2};
        }

        @Override
        public String docs() {
            return "string {locale, key} Returns an i18n message.";
        }
    }

}
