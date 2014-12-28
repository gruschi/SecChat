<?php

class Connection extends AppModel {

	public function beforeSave($options = array()){
		$this->loadModel("User");

		if(!isset($this->data["Connection"]["senderId"]) && isset($this->data["User"]["sessionId"])){
			$objCurUser = $this->User->find('first', array("conditions" => array("User.sessionId" => $this->data["User"]["sessionId"])));
			
			if(!empty($objCurUser)){
				$this->data["Connection"]["senderId"] = $objCurUser["User"]["id"];
			}else{
				return false;
			}
			
			
		}
		
		return true;
	}
	
}

?>