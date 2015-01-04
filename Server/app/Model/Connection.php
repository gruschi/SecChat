<?php

class Connection extends AppModel {

	public function beforeSave($options = array()){
		$this->loadModel("User");

		//Add Sender ID to Connection Class
		if(!isset($this->data["Connection"]["senderId"]) && isset($this->data["User"]["sessionId"])){
			$objCurUser = $this->User->find('first', array("conditions" => array("User.sessionId" => $this->data["User"]["sessionId"])));
			
			if(!empty($objCurUser)){
				$this->data["Connection"]["senderId"] = $objCurUser["User"]["id"];
			}else{
				return false;
			}
		}
		
		//Check if receiverID is actually available
		$objUser = $this->User->find('first', array("conditions" => array("User.id" => $this->data["Connection"]["receiverId"])));		
		if(empty($objUser)){
			return false;
		}
		
		return true;
	}
	
}

?>