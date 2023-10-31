package rhizome;

import org.junit.jupiter.api.Test;

import rhizome.core.common.Utils.PublicWalletAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rhizome.core.common.Utils.walletAddressToString;

import static rhizome.core.common.Utils.stringToWalletAddress;

class PublicWalletAddressTest {
    
    @Test
    void checkAddressSerialisation() {
        var a = PublicWalletAddress.empty();
        var b = stringToWalletAddress(walletAddressToString(a.address()));
        assertEquals(a,b);
    }
}
