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
		
		$result = 0;
		echo "foob";
		if($type == "contact"){
			$this->Connection->create();
			if($this->Connection->save($this->request)){
				$result = array("chatSessionId" => $this->Connection->id);
			}
		}
		
		$this->set("result", json_encode($chatSessionId));
	}
}

?>