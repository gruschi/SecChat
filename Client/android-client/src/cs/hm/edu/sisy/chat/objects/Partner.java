package cs.hm.edu.sisy.chat.objects;

public class Partner {
	private static String partnerAlias;
	private static String partnerPubKey;
	private static String partnerPin;
	private static int partnerId;
	
	private static String partnerNewMsg;
	
	public static String getPartnerAlias() {
		//TODO: Storage in SharedPreferences
		return partnerAlias;
	}
	public static void setPartnerAlias(String partnerAlias) {
		//TODO: Storage in SharedPreferences
		Partner.partnerAlias = partnerAlias;
	}
	public static String getPartnerPubKey() {
		//TODO: Storage in SharedPreferences
		return partnerPubKey;
	}
	public static void setPartnerPubKey(String partnerPubKey) {
		//TODO: Storage in SharedPreferences
		Partner.partnerPubKey = partnerPubKey;
	}
	public static String getPartnerPin() {
		//TODO: Storage in SharedPreferences
		return partnerPin;
	}
	public static void setPartnerPin(String partnerPin) {
		//TODO: Storage in SharedPreferences
		Partner.partnerPin = partnerPin;
	}
	public static int getPartnerId() {
		//TODO: Storage in SharedPreferences
		return partnerId;
	}
	public static void setPartnerId(int receiverId) {
		//TODO: Storage in SharedPreferences
		Partner.partnerId = receiverId;
	}
	public static String getPartnerNewMsg() {
		//TODO: Storage in SharedPreferences
		return partnerNewMsg;
	}
	public static void setPartnerNewMsg(String partnerNewMsg) {
	    if (Partner.partnerNewMsg == "")
	    	Partner.partnerNewMsg = partnerNewMsg;
	    else
	    	Partner.partnerNewMsg += "\n" + partnerNewMsg;
	}
	public static void resetPartnerNewMsg() {
		Partner.partnerNewMsg = "";
	}
}
