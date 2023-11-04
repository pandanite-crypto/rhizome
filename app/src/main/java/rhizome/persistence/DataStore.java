package rhizome.persistence;

import org.apache.commons.io.FileUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import lombok.Getter;
import lombok.Setter;
import rhizome.core.ledger.LedgerException;

import org.iq80.leveldb.DBIterator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

@Getter
@Setter
public class DataStore {
    private DB db;
    private String path;

    public DataStore() {
        this.db = null;
        this.path = "";
    }

    public void init(String path) throws IOException {
        if (this.db != null) {
            this.closeDB();
        }
        this.path = path;
        Options options = new Options();
        options.createIfMissing(true);
        this.db = factory.open(new File(path), options);
    }

    public void deleteDB() throws IOException {
        this.closeDB();
        factory.destroy(new File(path), new Options());
        File directory = new File(path);
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            throw new LedgerException("Could not clear path " + path, e);
        }
    }

    public void closeDB() {
        if (this.db != null) {
            try {
                this.db.close();
            } catch (IOException e) {
                throw new LedgerException("Could not close DataStore db", e);
            }
            this.db = null;
        }
    }

    public void clear() {
        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = new String(iterator.peekNext().getKey(), StandardCharsets.UTF_8);
                db.delete(key.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new LedgerException("Could not clear data store", e);
        }
    }

    public String getPath() {
        return this.path;
    }
}
