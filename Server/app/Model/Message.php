<?php

class Message extends AppModel {
	
	public function beforeSave($options = array()){
		$this->loadModel("User");
							
		$objCurUser = $this->User->find('first', array("conditions" => array("User.sessionId" => $this->data["User"]["sessionId"])));				
		
		$this->data["Message"]["senderId"] = $objCurUser["User"]["id"];	
		
		return true;
	}
}