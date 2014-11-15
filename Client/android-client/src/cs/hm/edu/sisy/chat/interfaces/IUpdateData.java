package cs.hm.edu.sisy.chat.interfaces;
import cs.hm.edu.sisy.chat.types.FriendInfo;


public interface IUpdateData {
	public void updateData(FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}
