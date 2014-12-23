package cs.hm.edu.sisy.chat.tools;

import android.content.Context;
import android.util.Log;
import cs.hm.edu.sisy.chat.enums.Constants;
import cs.hm.edu.sisy.chat.enums.State;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.Storage;

public class TestData {

    public static void printInterestingData(Context thisC) {
    	String alias = Storage.getAlias(thisC);
    	String hash = Storage.getHash(thisC);
    	String id = Integer.toString(Storage.getID(thisC));
    	String pin = Storage.getPIN(thisC);
    	String session = Storage.getSessionId(thisC);
    	String storagedPinDate = Integer.toString(Storage.getStoragedPinDate(thisC));
    	
    	PubPrivKeyGenerator kg = new PubPrivKeyGenerator(thisC);
    	String pubK = kg.getPrivateKeyAsString();
    	String privK = kg.getPublicKeyAsString();
    	
    	String state = State.getStateMessage();
    	
    	Log.d(Constants.LOG, "Alias: "+alias);
    	Log.d(Constants.LOG, "Hash: "+hash);
    	Log.d(Constants.LOG, "ID: "+id);
    	Log.d(Constants.LOG, "PIN: "+pin);
    	Log.d(Constants.LOG, "StoragedPinDate: "+storagedPinDate);
    	
    	Log.d(Constants.LOG, "SessionID: "+session);
    	Log.d(Constants.LOG, "State: "+state);
    	
    	Log.d(Constants.LOG, "PublicKey: "+pubK);
    	Log.d(Constants.LOG, "PrivateKey: "+privK);
	}
	
}
