package price.tracker.coinbase;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import java.security.interfaces.ECPrivateKey;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;

import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;

@Service
@NoArgsConstructor
public class CoinbaseJWTService {

    public String getJWT(String requestMethod, String url) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        String name = System.getenv().get("COINBASE_KEY_NAME");
        String privateKeyPEM = System.getenv().get("COINBASE_PRIVATE_KEY").replace("\\n", "\n");
        String uri = requestMethod + " " + url;

        return generateJWT(name, uri, privateKeyPEM);
    }

    private String generateJWT(String name, String uri, String privateKeyPEM) throws Exception {
        SignedJWT signedJWT = getSignedJWT(name, uri);
        JWSSigner signer = getJWSSigner(privateKeyPEM);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private SignedJWT getSignedJWT(String name, String uri) {
        JWTClaimsSet claimsSet = getJWTClaimsSet(name, uri);

        JWSHeader jwsHeader = getJWSHeader(name);
        return new SignedJWT(jwsHeader, claimsSet);
    }

    private JWSSigner getJWSSigner(String privateKeyPEM) throws Exception {
        ECPrivateKey ecPrivateKey = getECPrivateKey(privateKeyPEM);
        return new ECDSASigner(ecPrivateKey);
    }

    private JWSHeader getJWSHeader(String name) {
        Map<String, Object> headers = getHeaders(name);

        return new JWSHeader.Builder(JWSAlgorithm.ES256).customParams(headers).build();
    }

    private JWTClaimsSet getJWTClaimsSet(String name, String uri) {
        Map<String, Object> data = getData(name, uri);

        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            claimsSetBuilder.claim(entry.getKey(), entry.getValue());
        }
        return claimsSetBuilder.build();
    }

    private ECPrivateKey getECPrivateKey(String privateKeyPEM) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private PrivateKey getPrivateKey(String privateKeyPEM) throws Exception {
        PEMParser pemParser = new PEMParser(new StringReader(privateKeyPEM));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        Object object = pemParser.readObject();
        PrivateKey privateKey;

        if (object instanceof PrivateKey) {
            privateKey = (PrivateKey) object;
        } else if (object instanceof org.bouncycastle.openssl.PEMKeyPair) {
            privateKey = converter.getPrivateKey(((org.bouncycastle.openssl.PEMKeyPair) object).getPrivateKeyInfo());
        } else {
            throw new Exception("Unexpected private key format");
        }
        pemParser.close();

        return privateKey;
    }

    private Map<String, Object> getData(String name, String uri) {
        Map<String, Object> data = new HashMap<>();
        data.put("iss", "cdp");
        data.put("nbf", Instant.now().getEpochSecond());
        data.put("exp", Instant.now().getEpochSecond() + 120);
        data.put("sub", name);
        data.put("uri", uri);

        return data;
    }

    private Map<String, Object> getHeaders(String name) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "ES256");
        headers.put("typ", "JWT");
        headers.put("kid", name);
        headers.put("nonce", String.valueOf(Instant.now().getEpochSecond()));

        return headers;
    }

}
