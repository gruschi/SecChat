package edu.hm.cs.sisy.enums;

import java.util.HashMap;

import edu.hm.cs.sisy.chat.R;

public class SCSmiley {
	
	public static final HashMap<String, Integer> EMOCTIONS = new HashMap<String, Integer>();
	
	static {
		//https://www.iconfinder.com/icons/15497/smiley_icon#size=32
		EMOCTIONS.put(":)", R.drawable.smiley1);
		EMOCTIONS.put(":-)", R.drawable.smiley1);
		EMOCTIONS.put(";)", R.drawable.smiley2);
		EMOCTIONS.put(";-)", R.drawable.smiley2);
		EMOCTIONS.put(":(", R.drawable.smiley3);
		EMOCTIONS.put(":-(", R.drawable.smiley3);
		EMOCTIONS.put("<3", R.drawable.smiley4);
	  }

}
