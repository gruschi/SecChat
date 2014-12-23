package cs.hm.edu.sisy.chat.storage;

public class Partner {
	private static String partnerAlias;
	private static String partnerPubKey;
	private static String partnerPin;
	private static int partnerId;
	
	public static String getPartnerAlias() {
		return partnerAlias;
	}
	public static void setPartnerAlias(String partnerAlias) {
		Partner.partnerAlias = partnerAlias;
	}
	public static String getPartnerPubKey() {
		return partnerPubKey;
	}
	public static void setPartnerPubKey(String partnerPubKey) {
		Partner.partnerPubKey = partnerPubKey;
	}
	public static String getPartnerPin() {
		return partnerPin;
	}
	public static void setPartnerPin(String partnerPin) {
		Partner.partnerPin = partnerPin;
	}
	public static int getPartnerId() {
		return partnerId;
	}
	public static void setPartnerId(int receiverId) {
		Partner.partnerId = receiverId;
	}
}
