<?php

App::uses('BlowfishPasswordHasher', 'Controller/Component/Auth');

class User extends AppModel {
	
	public $validate = array(
			'username' => array(					
					'isUnique' => array(
							'rule' => array('isUnique'),
							'message' => 'This username is aready in use'
					)
			),
			'password' => array(
					'required' => array(
							'rule' => array('notEmpty'),
							'message' => 'A password is required'
					)
			)
	);
	
	public function beforeSave($options = array()) {
	    
		//ID Creator
		$this->data[$this->alias]["id"] = $this->createUniqueUserId();
		
		//PW Hasher
		if (isset($this->data[$this->alias]['password'])) {
	        $passwordHasher = new BlowfishPasswordHasher();
	        $this->data[$this->alias]['password'] = $passwordHasher->hash($this->data[$this->alias]['password']);
	    }
	    
	    return true;
	}		
	
	private function createUniqueUserId($length  = 9){
				 
		$chars = "1234567890";
		$id = "";
		for($i = 0; $i < $length; $i++){
			$rndInt = rand(0, 9);
			$char = substr($chars, $rndInt, 1);
			if($char != false){
				$id .= $char;
			}
		}
		
		//Teste ID
		$idAvailable = $this->find("first", array("conditions" => array("User.id" => $id)));
		
		if(!empty($idAvailable)){
			return $this->createUniqueUserId();
		}
		
		//Teste auf 9 Ziffern
		if(strlen($id) != 9){
			return $this->createUniqueUserId(); 
		}
		
		return $id;
	}
}

?>