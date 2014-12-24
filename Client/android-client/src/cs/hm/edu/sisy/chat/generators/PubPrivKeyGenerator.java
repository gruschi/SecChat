package cs.hm.edu.sisy.chat.generators;

import java.math.BigInteger;
import java.security.InvalidKeyException;
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
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Base64;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PubPrivKeyGenerator extends Activity{

    SharedPreferences SP;
    SharedPreferences.Editor SPE;
    PublicKey pubKey;
    PrivateKey privKey; 
    Context context;
    
    KeyPairGenerator kpg;
    KeyPair kp;
    byte[] encryptedBytes, decryptedBytes;
    Cipher cipher, cipher1;
    String encrypted, decrypted;

    public PubPrivKeyGenerator(Context context){
        this.context = context;
        SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
    }

    @SuppressLint("TrulyRandom")
	public void generateKeys(){
        try {
            KeyPairGenerator generator;
            generator = KeyPairGenerator.getInstance("RSA", "BC"); //AES
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
    	if(pubKeyStr == null || pubKeyStr == "")
    		return null; 
        
        byte[] sigBytes = Base64.decode(pubKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC"); //AES
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
    	if(privKeyStr == null || privKeyStr == "")
    		return null; 
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
    
    // decrypts the message
    public String encrypt (String message) 
    {
		try {
	        kpg = KeyPairGenerator.getInstance("RSA", "BC"); //AES
	        kpg.initialize(1024);
	        kp = kpg.genKeyPair();
	        pubKey = kp.getPublic();
	        privKey = kp.getPrivate();

	        cipher = Cipher.getInstance("RSA", "BC");
	        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
	        encryptedBytes = cipher.doFinal(message.getBytes());

	        encrypted = bytesToString(encryptedBytes);
	        return encrypted;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		
		return null;
    }

    // decrypts the message
    public String decrypt (String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException 
    {
		try {
	        cipher1=Cipher.getInstance("RSA", "BC");
	        cipher1.init(Cipher.DECRYPT_MODE, privKey);
	        //decryptedBytes = cipher1.doFinal(stringToBytes(message)); //TODO: stringToBytes = Base64.decodeBase64(message)
	        decryptedBytes = cipher1.doFinal(Base64.decode(message)); //TODO: stringToBytes = Base64.decodeBase64(message)
	        decrypted = new String(decryptedBytes);
	        return decrypted;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	
	return null;
    }

    public  String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public  byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }
    
    /*
    //ALTERNATIVE

    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte [] encryptedBytes,decryptedBytes;
    Cipher cipher,cipher1;
    String encrypted,decrypted;
    
    public byte[] RSAEncrypt(final String plain) throws NoSuchAlgorithmException, NoSuchPaddingException,
    InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		kp = kpg.genKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
		
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		encryptedBytes = cipher.doFinal(plain.getBytes());
		System.out.println("EEncrypted?????" + org.apache.commons.codec.binary.Hex.encodeHexString(encryptedBytes));
		return encryptedBytes;
    }

	public String RSADecrypt(final byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
	    InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher1 = Cipher.getInstance("RSA");
		cipher1.init(Cipher.DECRYPT_MODE, privateKey);
		decryptedBytes = cipher1.doFinal(encryptedBytes);
		decrypted = new String(decryptedBytes);
		System.out.println("DDecrypted?????" + decrypted);
		return decrypted;
	}
	*/
    
    
}