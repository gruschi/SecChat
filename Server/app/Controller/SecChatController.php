<?php

class SecChatController extends AppController {
	
	public function index(){		
		$this->layout = "ajax";
	}
	
	public function connect($type=null){
		$this->loadModel("Connection");
		
		/*
		 * data[Connection][sessionId] = sessionID des Clients
			data[Connection][receiverId] = receiverID
			data[Connection][alias] = senderAlias (an Receiver weiterleiten)
			data[Connection][receiverPin] = receiverPIN
			data[Connection][pubKey] = senderPubKey (an Receiver weiterleiten)
		 */
		
		$result = null;		
		if($type == "contact"){			
			$this->Connection->create();			
			if($this->Connection->save($this->request->data)){				
				$result = array("chatSessionId" => $this->Connection->id);
			}else{
 				debug($this->Connection->invalidFields()); 
			}
		}
		
		
		$this->set("result", json_encode($result));
		
		$this->layout = "ajax";
	}
	
	public function service(){
		$this->loadModel("Connection");
		
		//Get Incoming Connections
//  		echo "<br>UserId: ".$this->Auth->user("user");
		$result = $this->Connection->find('all', array("conditions" => array("Connection.receiverId" => $this->Auth->user("username"))));
		
		$return = array("chatSession" => $result);
		
		$this->set("result", json_encode($return));
		
		$this->layout = "ajax";
	}
}

?>