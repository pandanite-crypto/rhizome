package rhizome.core.net;

import io.activej.bytebuf.ByteBuf;

public interface NetworkSerializable {
    ByteBuf toBuffer();    
}
