<?php

class MessageController extends AppController {
	
	var $uses = array('User');
	
	public function send(){		
		if ($this->request->is('post')) {
			$this->Message->create();
			
			$this->User->find('first', array(	"condition" => 
					array('sessionId' => $this->request->data["Message"]["sessionId"])));
			
			if ($this->Message->save($this->request->data)) {
				$response = __("The message has been saved"); 				
			}else{
				$response = __("The message could not be saved");
			}
			
			$this->set("response", $response);
			
		}
		
		$this->layout = "ajax";
	}
	
	public function receive(){
		$messages = $this->Message->find("all", array("condition" => array(
				array("receiverId" => $this->User->find("first", array("condition" =>
						array('sessionId' => $this->request->data["Message"]["sessionId"])
				))))));
		
		$this->set("messages", $messages);
		$this->layout = "ajax";
	}
	
}

?>