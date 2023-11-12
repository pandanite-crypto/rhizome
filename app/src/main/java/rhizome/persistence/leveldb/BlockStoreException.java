package rhizome.persistence.leveldb;

public class BlockStoreException extends RuntimeException {
    public BlockStoreException(String message) {
        super(message);
    }

    public BlockStoreException(String message, Throwable cause) {
        super(message, cause);
    }    
}
