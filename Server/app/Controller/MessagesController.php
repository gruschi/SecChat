<?php

class MessagesController extends AppController {
	
	var $uses = array('User');
	
	public function beforeFilter() {
		parent::beforeFilter();
		
		$this->Auth->allow();
	}
	
	public function send(){		
		$this->loadModel("Message");
		
		if ($this->request->is('post')) {			
			$this->Message->create();					
			
			if ($this->Message->save($this->request->data)) {
				$response = array("received" => "true"); 				
			}else{
				$response = array("received" => "false"); 
			}
			
			$this->set("response", json_encode($response));
			
		}
		
		$this->layout = "ajax";
	}
	
	public function receive(){
		$this->loadModel("Message");		
		
		$messages = $this->Message->find("all", array("condition" => array(
				array("receiverId" => $this->Auth->user("id")))));
		
		$return = array("receivedMessages" => $messages);
		
		$this->set("messages", json_encode($return));
		$this->layout = "ajax";
	}
	
}

?>