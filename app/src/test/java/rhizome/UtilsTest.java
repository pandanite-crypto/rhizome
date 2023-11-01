package rhizome;

import org.junit.jupiter.api.Test;

import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.TransactionSignature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static rhizome.core.common.Utils.walletAddressToString;
import static rhizome.core.common.Utils.signatureToString;
import static rhizome.core.common.Utils.stringToSignature;
import static rhizome.core.common.Utils.stringToWalletAddress;

class UtilsTest {
    
    @Test
    void checkAddressSerialisation() {
        var a = PublicWalletAddress.random();
        var b = stringToWalletAddress(walletAddressToString(a.address()));
        assertEquals(a,b);
    }

    @Test
    void checkTransactionSignatureSerialisation() {
        var a = TransactionSignature.random();
        var b = stringToSignature(signatureToString(a.signature));
        assertEquals(a,b);
    }
}
