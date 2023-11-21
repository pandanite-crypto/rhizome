package rhizome.core.net;

import io.activej.serializer.CorruptedDataException;

/**
 * Represents a serializer which encodes and decodes &lt;T&gt; values to byte arrays in Pandanite format
 */
public interface BinarySerializer<T> {
	default int encode(byte[] array, int pos, T item) {
		BinaryOutput out = new BinaryOutput(array, pos);
		encode(out, item);
		return out.pos();
	}

	default T decode(byte[] array, int pos) throws CorruptedDataException {
		return decode(new BinaryInput(array, pos));
	}

	void encode(BinaryOutput out, T item);

	T decode(BinaryInput in) throws CorruptedDataException;
}
