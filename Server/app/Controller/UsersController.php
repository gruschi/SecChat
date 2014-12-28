<?php

class UsersController extends AppController {
	
	public function beforeFilter() {
		parent::beforeFilter();
		
		// Allow users to register and logout.
		$this->Auth->allow('add', 'login', 'logout');
	}
	
	/**
	 * @sicher
	 */
	public function index() {
		
	}

	/**
	 * Hinzufügen eines neuen Benutzers (bei erstmaliger App Installation
	 * @sicher
	 */
	public function add() {
		if ($this->request->is('post')) {
			$this->User->create();
			$return = null;
			if ($this->User->save($this->request->data)) {								
				$user = $this->User->find("first", array("conditions" => array("User.id" => $this->User->id)));				
				$return = array('userId' => $this->User->id);				
			}else{
				$return = null;
			}	
			$this->set("return", json_encode($return));
			
			$this->layout = "ajax";
		}				
	}

	/**
	 * @sicher
	 */
 	public function login() {
		$return = null;
		if ($this->request->is('post')) {
			if ($this->Auth->login()) {
				$this->updateSessionId($this->Session->id());
				$return = array("sessionId" => $this->Session->id());
			}else{				
				$return = array("sessionId" => null);
			}							
		}
		
		$this->set("return", json_encode($return));
 		$this->layout = "ajax";
	}
	
	/**
	 * @sicher
	 * @param unknown $sessionId
	 */
	private function updateSessionId($sessionId=null){
		$this->User->updateAll(
				array('User.sessionId' => "'".$sessionId."'"),
    			array('User.id' => $this->Auth->user('id'))
		);
	}
	
	/**
	 * Log Out the User
	 * @sicher
	 */
	public function logout() {
		
		$this->updateSessionId(null);
		
		//Dreifacher Logout hält besser.
		$this->Auth->logout();
		$this->Session->delete('User');
		$this->Session->destroy();		
				
		$this->set("return", "logged out");
		
		$this->layout = "ajax";
	}

}

?>