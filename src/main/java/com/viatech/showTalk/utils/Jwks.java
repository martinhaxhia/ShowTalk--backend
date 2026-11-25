package com.viatech.showTalk.utils;

import java.nio.file.Files;
import com.nimbusds.jose.jwk.RSAKey;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;


public class Jwks {

    //checks the signatures , anyone can use it to encrypt data.
    private static final String PUBLIC_KEY_FILE = "public.key";
    //signs JWT , holder of the matching private key cn decrypt the data.
    private static final String PRIVATE_KEY_FILE = "private.key";
    private static final String KEY_ID_FILE = "key-id.txt";

    public Jwks() {
    }

    public static RSAKey load(Path keyDir, boolean generateIfMissing) {
        Path publicKeyFile = keyDir.resolve(PUBLIC_KEY_FILE);
        Path privateKeyFile = keyDir.resolve(PRIVATE_KEY_FILE);

        try {
            if (Files.exists(publicKeyFile)
                    && Files.exists(privateKeyFile)) {
                 return loadFromFile(keyDir);
            }


            if (!generateIfMissing) {
                throw new IllegalStateException("Could not generate RSA key generate if missing");
            }

            return generateAndSave(keyDir);
        }catch (IllegalStateException e) {
            throw e;
        }catch (Exception e) {
            throw new IllegalStateException("Could not generate RSA key", e);
        }
    }

    private static RSAKey loadFromFile(Path keyDir) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] publicKeyBytes = Base64.getDecoder().decode(Files.readString(keyDir.resolve(PUBLIC_KEY_FILE)).trim());
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readString(keyDir.resolve(PRIVATE_KEY_FILE)).trim());
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        Path keyIdFile = keyDir.resolve(KEY_ID_FILE);

        String keyId;

        if (Files.exists(keyIdFile)) {
            keyId = Files.readString(keyIdFile).trim();
        } else {
            keyId = UUID.randomUUID().toString();
        }


        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();
    }


    private static RSAKey generateAndSave(Path keyDir) throws Exception {

        KeyPair keyPair = KeyGeneratorUtil.generateRSAKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //the label of the pair
        String keyId = UUID.randomUUID().toString();

        Files.createDirectories(keyDir);
        Files.writeString(keyDir.resolve(PUBLIC_KEY_FILE), Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        Files.writeString(keyDir.resolve(PRIVATE_KEY_FILE), Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        Files.writeString(keyDir.resolve(KEY_ID_FILE), keyId);

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();


    }


}
