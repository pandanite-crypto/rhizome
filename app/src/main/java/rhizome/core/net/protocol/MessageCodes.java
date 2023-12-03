package rhizome.core.net.protocol;

import java.util.Map;
import com.google.common.collect.Maps;

import static rhizome.core.net.protocol.ProtocolVersion.V01;

public enum MessageCodes {
    BLOCKS_REQUEST(0x00),
    BLOCKS_RESPONSE(0x01),
    SYNC_BLOCK(0x02),
    SYNCBLOCK_REQUEST(0x03);

    private static final Map<ProtocolVersion, Map<Integer, MessageCodes>> intToTypeMap = Maps.newHashMap();
    private static final Map<ProtocolVersion, MessageCodes[]> versionToValuesMap = Maps.newHashMap();

    static {
        versionToValuesMap.put(
                V01,
                new MessageCodes[]{
                        BLOCKS_REQUEST,
                        BLOCKS_RESPONSE,
                        SYNC_BLOCK,
                        SYNCBLOCK_REQUEST
                });

        for (ProtocolVersion v : ProtocolVersion.values()) {
            Map<Integer, MessageCodes> map = Maps.newHashMap();
            intToTypeMap.put(v, map);
            for (MessageCodes code : values(v)) {
                map.put(code.cmd, code);
            }
        }
    }

    private final int cmd;

    MessageCodes(int cmd) {
        this.cmd = cmd;
    }

    public static MessageCodes[] values(ProtocolVersion v) {
        return versionToValuesMap.get(v);
    }

    public static MessageCodes fromByte(byte i, ProtocolVersion v) {
        Map<Integer, MessageCodes> map = intToTypeMap.get(v);
        return map.get((int) i);
    }

    public static boolean inRange(byte code, ProtocolVersion v) {
        MessageCodes[] codes = values(v);
        return code >= codes[0].asByte() && code <= codes[codes.length - 1].asByte();
    }

    public byte asByte() {
        return (byte) (cmd);
    }
}
