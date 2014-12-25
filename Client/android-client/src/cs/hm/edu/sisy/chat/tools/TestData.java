package cs.hm.edu.sisy.chat.tools;

import android.content.Context;
import android.util.Log;
import cs.hm.edu.sisy.chat.enums.SCConstants;
import cs.hm.edu.sisy.chat.enums.SCState;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;

public class TestData {

    public static void printInterestingData(Context thisC) {
    	String alias = SharedPrefs.getAlias(thisC);
    	String hash = SharedPrefs.getHash(thisC);
    	String id = Integer.toString(SharedPrefs.getID(thisC));
    	String pin = SharedPrefs.getPIN(thisC);
    	String session = SharedPrefs.getSessionId(thisC);
    	String storagedPinDate = Integer.toString(SharedPrefs.getStoragedPinDate(thisC));
    	
    	String pubK = PubPrivKeyGenerator.getOwnPrivateKeyAsString(thisC);
    	String privK = PubPrivKeyGenerator.getOwnPublicKeyAsString(thisC);
    	
    	String state = SCState.getStateMessage();
    	
    	Log.d(SCConstants.LOG, "Alias: "+alias);
    	Log.d(SCConstants.LOG, "Hash: "+hash);
    	Log.d(SCConstants.LOG, "ID: "+id);
    	Log.d(SCConstants.LOG, "PIN: "+pin);
    	Log.d(SCConstants.LOG, "StoragedPinDate: "+storagedPinDate);
    	
    	Log.d(SCConstants.LOG, "SessionID: "+session);
    	Log.d(SCConstants.LOG, "State: "+state);
    	
    	Log.d(SCConstants.LOG, "PublicKey: "+pubK);
    	Log.d(SCConstants.LOG, "PrivateKey: "+privK);
	}
	
}
