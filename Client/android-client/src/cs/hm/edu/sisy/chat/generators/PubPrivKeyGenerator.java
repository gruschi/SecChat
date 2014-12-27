package cs.hm.edu.sisy.chat.generators;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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
	
	final private static String algorithm = "RSA"; //RSA, AES, DSA or + provider BC

  @SuppressLint("TrulyRandom")
	public static void generateKeys(Context context){
      SharedPreferences SP;
      SharedPreferences.Editor SPE;
      PublicKey pubKey;
      PrivateKey privKey; 
      
      SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
      
        try {
            KeyPairGenerator generator;
            generator = KeyPairGenerator.getInstance(algorithm);
            //generator.initialize(256, new SecureRandom());
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            pubKey = pair.getPublic();
            privKey = pair.getPrivate();        
            
            /*byte[] publicKeyBytes = pubKey.getEncoded();
            String pubKeyStr = new String(Base64.encode(publicKeyBytes));
            byte[] privKeyBytes = privKey.getEncoded();
            String privKeyStr = new String(Base64.encode(privKeyBytes));*/  
            
            String pubKeyStr = null;
			String privKeyStr = null;
			
			try {
				KeyFactory fact = KeyFactory.getInstance(algorithm); //DSA
				X509EncodedKeySpec spec = fact.getKeySpec(pubKey,
				        X509EncodedKeySpec.class);
				pubKeyStr = new String(Base64.encode(spec.getEncoded()));
				
				KeyFactory fact2 = KeyFactory.getInstance(algorithm);
				PKCS8EncodedKeySpec spec2 = fact2.getKeySpec(privKey,
				        PKCS8EncodedKeySpec.class);
				byte[] packed = spec2.getEncoded();
				String key64 = new String(Base64.encode(packed));
				Arrays.fill(packed, (byte) 0);
				privKeyStr = key64;
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
            
            SPE = SP.edit();
            SPE.putString("PublicKey", pubKeyStr);
            SPE.putString("PrivateKey", privKeyStr);           
            SPE.commit();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }           
    }
    
    public static PublicKey getPublicKey(Context context, String pubKeyStr){
    	if(pubKeyStr == null || pubKeyStr == "")
    		return null; 
    	
        try {
			byte[] data = Base64.decode(pubKeyStr);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
			KeyFactory fact = KeyFactory.getInstance(algorithm);
			return fact.generatePublic(spec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
        
        /*byte[] sigBytes = Base64.decode(pubKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance(algorithm); //AES
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
        return null;*/
    }
    
    public static String getOwnPublicKeyAsString(Context context){
      SharedPreferences SP;
      SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
      return SP.getString("PublicKey", "");       
    }
    
    public static PrivateKey getPrivateKey(Context context, String privKeyStr){
    	if(privKeyStr == null || privKeyStr == "")
    		return null; 
    	
        try {
			byte[] clear = Base64.decode(privKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
			KeyFactory fact = KeyFactory.getInstance(algorithm);
			PrivateKey priv = fact.generatePrivate(keySpec);
			Arrays.fill(clear, (byte) 0);
			return priv;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
        
        return null;
    	
        /*byte[] sigBytes = Base64.decode(privKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance(algorithm);
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
        return null;*/
    }
    
    public static String getOwnPrivateKeyAsString(Context context){
      SharedPreferences SP;
      SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
      return SP.getString("PrivateKey", "");      
    }
    
    // encrypts the message with partners public key
    public static String encrypt (String message, Context context, String pubKeyStr) 
    {
      PublicKey pubKey = getPublicKey(context, pubKeyStr);
 
      byte[] encryptedBytes;
      Cipher cipher;
      String encrypted;
      
		try {
	        cipher = Cipher.getInstance(algorithm);
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
		}
		
		return null;
    }

    // decrypts the message with my own private key
    public static String decrypt (String message, Context context, String privKeyStr) 
    {
      PrivateKey privKey = getPrivateKey(context, privKeyStr);
      
      byte[] decryptedBytes;
      Cipher cipher;
      String decrypted;    
		try {
	        cipher=Cipher.getInstance(algorithm);
	        cipher.init(Cipher.DECRYPT_MODE, privKey);
	        decryptedBytes = cipher.doFinal(stringToBytes(message)); //or stringToBytes = Base64.decodeBase64(message)
	        //decryptedBytes = cipher.doFinal(Base64.decode(message)); //or stringToBytes = Base64.decodeBase64(message)
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
		}
	
	return null;
    }

    public static  String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public static byte[] stringToBytes(String s) {
    	if(s == null)
    		return null;
    	
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