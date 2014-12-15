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
		$this->loadModel("Connection");	
		
		$connections = $this->Connection->find("all", array("conditions" => array("Connection.receiverId" => $this->Auth->user("id"))));
		
		$cC = count($connections);
		$messages = array();
		
		for($i = 0; $i < $cC; $i++){
			$tmpMessages = $this->Message->find("all", array("conditions" => array(
				array("connectionId" => $connections[$i]["Connection"]["id"]))));
			
			$cM = count($tmpMessages);
			
			for($m = 0; $m < $cM; $m++){
				$messages[] = $tmpMessages[$m];
			}
		}
				
		$return = array("receivedMessages" => $messages);
		
		$this->set("messages", json_encode($return));
// 		$this->layout = "ajax";
	}
	
}

?>