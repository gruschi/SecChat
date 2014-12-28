<?php

class UsersController extends AppController {
	
	public function beforeFilter() {
		parent::beforeFilter();
		
		// Allow users to register and logout.
		$this->Auth->allow('add', 'login', 'logout');
	}
	
	public function index() {
		$this->User->recursive = 0;
		$this->set('users', $this->paginate());
	}

	public function view($id = null) {
		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		$this->set('user', $this->User->read(null, $id));
	}

	public function add() {
		if ($this->request->is('post')) {
			$this->User->create();
			if ($this->User->save($this->request->data)) {								
				$user = $this->User->find("first", array("conditions" => array("User.id" => $this->User->id)));				
				$return = array('userId' => $this->User->id);				
			}else{
				$return = null;
			}	
			$this->set("return", json_encode($return));
		}
		
		$this->layout = "ajax";
	}

	public function edit($id = null) {
		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->User->save($this->request->data)) {
				$this->Session->setFlash(__('The user has been saved'));
				return $this->redirect(array('action' => 'index'));
			}
			$this->Session->setFlash(
					__('The user could not be saved. Please, try again.')
			);
		} else {
			$this->request->data = $this->User->read(null, $id);
			unset($this->request->data['User']['password']);
		}
	}

	public function delete($id = null) {
		// Prior to 2.5 use
		// $this->request->onlyAllow('post');

		$this->request->allowMethod('post');

		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		if ($this->User->delete()) {
			$this->Session->setFlash(__('User deleted'));
			return $this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('User was not deleted'));
		return $this->redirect(array('action' => 'index'));
	}
		
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
	
	private function updateSessionId($sessionId){
		$this->User->updateAll(
				array('User.sessionId' => $sessionId),
    			array('User.id' => $this->Auth->user('id'))
		);
	}
	
	/**
	 * Log Out the User
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