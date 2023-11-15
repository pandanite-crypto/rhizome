package rhizome.core.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;

public interface BinarySerializable {
    Map<Class<? extends BinarySerializable>, BinarySerializer<? extends BinarySerializable>> serializerCache = new ConcurrentHashMap<>();

    static <T extends BinarySerializable> T fromBuffer(byte[] buffer, Class<T> clazz) {
        BinarySerializer<T> serializer = getSerializer(clazz);
        return serializer.decode(buffer, 0);
    }

    default <T extends BinarySerializable> byte[] toBuffer() {
        var buffer = new byte[getSize()];
        BinarySerializer<T> serializer = getSerializer((Class<T>) this.getClass());
        serializer.encode(buffer, 0, (T) this);
        return buffer;
    }

    @NotNull
    int getSize();

    static <T extends BinarySerializable> BinarySerializer<T> getSerializer(Class<T> clazz) {
        //noinspection unchecked
        return (BinarySerializer<T>) serializerCache.computeIfAbsent(clazz, k -> SerializerBuilder.create().build(k));
    }
}
