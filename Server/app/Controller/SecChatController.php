<?php

class SecChatController extends AppController {
	
public function beforeFilter() {
		parent::beforeFilter();
 		$this->Auth->allow('connect', 'service', 'serviceConnect', 'destroyChatSession');
	}
	
	public function index(){		
		$this->layout = "ajax";
	}
	
	public function connect($type=null){
		$this->loadModel("Connection");
		
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
		$this->loadModel("User");
		
		//Get Incoming Connections
//  		echo "<br>UserId: ".$this->Auth->user("user");
		
		//GetUserId		
		$objUser = $this->User->find("first", array("conditions" => array("User.sessionId" => $this->request->data["Connection"]["sessionId"])));		
		
		$result = $this->Connection->find('all', 
					array(
							"conditions" => 
								array("Connection.receiverId" => intval($objUser["User"]["id"]), "Connection.receiverPin IS NOT NULL"),
							"fields" => array(
								"Connection.senderId", "Connection.alias", "Connection.receiverPin", "Connection.PubKey"
							)
					)
				);			
		
		$return = array("chatSession" => $result);
		
		$this->set("result", json_encode($return));

 		$this->layout = "ajax";
	}
	
	public function serviceConnect(){
		$this->loadModel("Connection");
		
		//Tut das gleiche wie Contact
		$this->Connection->create();
		if($this->Connection->save($this->request->data)){
			$result = array("chatSessionId" => $this->Connection->id);
		}else{
			debug($this->Connection->invalidFields());
		}
		
		$this->set("result", json_encode($result));
		
		$this->layout = "ajax";
		
	}
	
	public function destroyChatSession(){
		$this->loadModel("Connection");
		$return = false;
		if(isset($this->request->data["Connection"]["id"]) && isset($this->request->data["User"]["sessionId"])){
			//TODO Teste ob SessionId zur senderId passt.			
			$this->Connection->delete($this->request->data('Connection.id'));
			
			$return = true;
		}
		
		$result = array("chatDisconnected" => $return);
		
		$this->set("result", json_encode($result));
		
		$this->layout = "ajax";
	}
}

?>