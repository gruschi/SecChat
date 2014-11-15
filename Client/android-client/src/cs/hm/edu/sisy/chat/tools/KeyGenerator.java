package cs.hm.edu.sisy.chat.tools;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.util.encoders.Base64;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class KeyGenerator extends Activity{

    SharedPreferences SP;
    SharedPreferences.Editor SPE;
    PublicKey pubKey;
    PrivateKey privKey; 
    Context context;

    public KeyGenerator(Context context){
        this.context = context;
        SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
    }

    @SuppressLint("TrulyRandom")
	public void generateKeys(){
        try {
            KeyPairGenerator generator;
            generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(256, new SecureRandom());
            KeyPair pair = generator.generateKeyPair();
            pubKey = pair.getPublic();
            privKey = pair.getPrivate();            
            byte[] publicKeyBytes = pubKey.getEncoded();
            String pubKeyStr = new String(Base64.encode(publicKeyBytes));
            byte[] privKeyBytes = privKey.getEncoded();
            String privKeyStr = new String(Base64.encode(privKeyBytes));            
            SPE = SP.edit();
            SPE.putString("PublicKey", pubKeyStr);
            SPE.putString("PrivateKey", privKeyStr);           
            SPE.commit();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }           
    }
    public PublicKey getPublicKey(){
        String pubKeyStr = SP.getString("PublicKey", "");       
        byte[] sigBytes = Base64.decode(pubKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            return  keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getPublicKeyAsString(){
        return SP.getString("PublicKey", "");       
    }
    public PrivateKey getPrivateKey(){
        String privKeyStr = SP.getString("PrivateKey", "");
        byte[] sigBytes = Base64.decode(privKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            return  keyFact.generatePrivate(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getPrivateKeyAsString(){
        return SP.getString("PrivateKey", "");      
    }
}