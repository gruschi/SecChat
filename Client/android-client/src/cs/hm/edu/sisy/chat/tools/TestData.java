package cs.hm.edu.sisy.chat.tools;

import android.content.Context;
import android.util.Log;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.types.STATUS;

public class TestData {

    public static void printInterestingData(Context thisC) {
    	String alias = Storage.getAlias(thisC);
    	String hash = Storage.getHash(thisC);
    	String id = Integer.toString(Storage.getID(thisC));
    	String pin = Storage.getPIN(thisC);
    	String session = Storage.getSession(thisC);
    	String storagedPinDate = Integer.toString(Storage.getStoragedPinDate(thisC));
    	
    	PubPrivKeyGenerator kg = new PubPrivKeyGenerator(thisC);
    	String pubK = kg.getPrivateKeyAsString();
    	String privK = kg.getPublicKeyAsString();
    	
    	String state = Integer.toString(STATUS.getState());
    	
    	Log.d(CONST.LOG, "Alias: "+alias);
    	Log.d(CONST.LOG, "Hash: "+hash);
    	Log.d(CONST.LOG, "ID: "+id);
    	Log.d(CONST.LOG, "PIN: "+pin);
    	Log.d(CONST.LOG, "StoragedPinDate: "+storagedPinDate);
    	
    	Log.d(CONST.LOG, "SessionID: "+session);
    	Log.d(CONST.LOG, "StateID: "+state);
    	
    	Log.d(CONST.LOG, "PublicKey: "+pubK);
    	Log.d(CONST.LOG, "PrivateKey: "+privK);
	}
	
}
