<?php

class MessagesController extends AppController {
	
	var $uses = array('User');
	
	public function beforeFilter() {
		parent::beforeFilter();
		
		$this->Auth->allow("send", "receive");
	}
	
	/**
	 * @sicher
	 */
	public function send(){		
		$this->loadModel("Message");
		$this->loadModel("Connection");		
		
		if ($this->request->is('post')) {			
						
			//Check ob App Nachricht versenden darf.
			$objUser = $this->User->find("first", array("conditions" => array("User.sessionId" => $this->request->data("User.sessionId"))));
			$objConnection = $this->Connection->find("first", array(
					"conditions" => array(
						"Connection.id" => $this->request->data("Message.connectionId")
			)));				
			
			if(!empty($objUser) && ($objConnection["Connection"]["receiverId"] == $objUser["User"]["id"] 
					|| $objConnection["Connection"]["senderId"] == $objUser["User"]["id"])){
				$this->Message->create();
				if ($this->Message->save($this->request->data)) {
					$response = array("received" => "true");
				}else{
					$response = array("received" => "false");
				}
									
			}else{
				$response = array("received" => "false");
			}
						
			$this->set("response", json_encode($response));
 			$this->layout = "ajax";
		}
		
		
	}
	
	/**
	 * Empfängt alle Nachrichten zu allen Connections einer bestimmten SessionId
	 * @sicher
	 */
	public function receive(){
		$this->loadModel("Message");	
		$this->loadModel("Connection");	
		
		if($this->request->is("post")){
			$objUser = $this->User->find("first", array("conditions" => array("User.sessionId" => $this->request->data("User.sessionId"))));
			
			if(!empty($objUser)){
				$connections = $this->Connection->find("all", array("conditions" => array("Connection.receiverId" => $objUser["User"]["id"])));
					
				if(!empty($connections)){
					$cC = count($connections);
					$messages = array();
						
					for($i = 0; $i < $cC; $i++){
						$tmpMessages = $this->Message->find("all", array(
								"fields" => array(
									"Message.id",
									"Message.connectionId",
									"Message.senderId",
									"Message.message"
								),								
								"conditions" => array(
									"Message.connectionId" => $connections[$i]["Connection"]["id"])
								)
						);
					
						$cM = count($tmpMessages);
					
						for($m = 0; $m < $cM; $m++){
							$messages[] = $tmpMessages[$m];
							
							//Flag setzen in DB
							$this->Message->updateAll(
									array("Message.read" => true), 
									array("Message.id" => $tmpMessages[$m]["Message"]["id"]));	
						}																
					}
						
					$return = array("receivedMessages" => $messages);
				}else{
					$return = array("receivedMessages" => null);
				}				
			}else{
				$return = array("receivedMessages" => null);
			}
			
			
		}		
		
		$this->set("messages", json_encode($return));
 		$this->layout = "ajax";
	}
	
}

?>