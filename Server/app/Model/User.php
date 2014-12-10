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
			),
			'alias' => array(
					'required' => array(
							'rule' => array('notEmpty'),
							'message' => 'A alias is required'
					)
			)
	);
	
	public function beforeSave($options = array()) {
	    
		//PW Hasher
		if (isset($this->data[$this->alias]['password'])) {
	        $passwordHasher = new BlowfishPasswordHasher();
	        $this->data[$this->alias]['password'] = $passwordHasher->hash($this->data[$this->alias]['password']);
	    }
	    
	    //ID Creation	    
	    $this->data[$this->alias]['username'] = uniqid();
	    
	    return true;
	}		
}

?>