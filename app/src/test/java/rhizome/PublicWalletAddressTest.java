package rhizome;

import org.junit.jupiter.api.Test;

import rhizome.core.common.Utils.PublicWalletAddress;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static rhizome.core.common.Utils.walletAddressToString;

import static rhizome.core.common.Utils.stringToWalletAddress;

class PublicWalletAddressTest {
    
    @Test
    void checkAddressSerialisation() {
        var a = new PublicWalletAddress();
        var b = stringToWalletAddress(walletAddressToString(a.address));
        assertArrayEquals(a.address, b.address);
    }
}
