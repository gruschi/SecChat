package edu.hm.cs.sisy.objects;

public class PubKey {
  private int _id; //PK
  private int _friendId; //friend id
  private String _pubKey; //friend's pubKey
  
  public PubKey(){     
  }

  public PubKey(int friendId, String pubKey){
    this._friendId = friendId;
    this._pubKey = pubKey;
  }  
  
  public PubKey(int id, int friendId, String pubKey){
      this._id = id;
      this._friendId = friendId;
      this._pubKey = pubKey;
  }

  public int getId() {
    return _id;
  }

  public void setId(int id) {
    this._id = id;
  }
  
  public int getFriendId() {
    return _friendId;
  }

  public void setFriendId(int id) {
    this._friendId = id;
  }

  public String getPubKey() {
    return _pubKey;
  }

  public void setPubKey(String pubKey) {
    this._pubKey = pubKey;
  }

  @Override
  public String toString() {
    return _pubKey;
  }
} 