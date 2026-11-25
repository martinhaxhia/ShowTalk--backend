package com.viatech.showTalk.utils;



import java.security.KeyPair;
import java.security.KeyPairGenerator;


//final means the class can not be subclassed
final class KeyGeneratorUtil {

    //prevents other code from creating an instance
    private KeyGeneratorUtil() {}


    static KeyPair generateRSAKeyPair() {
        KeyPair keyPair;

        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }catch (Exception e) {
            throw new IllegalStateException("Could not generate RSA key", e);
        }

        return keyPair;
    }

}
