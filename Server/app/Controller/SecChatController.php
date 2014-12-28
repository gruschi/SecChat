<?php

class SecChatController extends AppController {
	
public function beforeFilter() {
		parent::beforeFilter();

		$this->Auth->allow('connect', 'service', 'serviceConnect', 'destroyChatSession');
	}
	
	/**
	 * @sicher
	 */
	public function index(){		
		if($this->request->is("post")){
			$this->layout = "ajax";
		}else{
			
		}
	}
	
	/**
	 * Um eine Verbindung mit einem anderen Client herzustellen.
	 * @param string $type (contact/group)
	 * @sicher
	 */
	public function connect($type=null){
		
		if($this->request->is("post")){
			$this->loadModel("Connection");
			
			$result = null;
			if($type == "contact"){
				$this->Connection->create();
				if($this->Connection->save($this->request->data)){
					$result = array("chatSessionId" => $this->Connection->id);
				}else{
					$result = array("disconnected");
				}
			}
			
			$this->set("result", json_encode($result));
			
			$this->layout = "ajax";
		}else{
			$this->set("result", __("No Access"));
		}
		
	}
	
	/**
	 * Gegenstück zu connect, was alle 5 Sek. aufgerufen wird:
	 * Connecter muss nach connect service pullen um Receiver-Daten zu bekommen.
	 * @sicher
	 */
	public function service(){
		if($this->request->is("post")){
			$this->loadModel("Connection");
			$this->loadModel("User");
						
			$result = null;
			if(isset($this->request->data["User"]["sessionId"])){
				//GetUserId
				$objUser = $this->User->find("first", array("conditions" => array("User.sessionId" => $this->request->data["User"]["sessionId"])));
					
				if(!empty($objUser)){
					$result = $this->Connection->find('all',
							array(
									"conditions" =>
									array(	"Connection.receiverId" => intval($objUser["User"]["id"]),
											"Connection.send" => false),
									"fields" => array(
											"Connection.id",
											"Connection.senderId",
											"Connection.alias",
											"Connection.receiverPin",
											"Connection.pubKey"
									),
									"limit" => 1
							)
					);
						
					//Flag setzen in Connection
					if(!empty($result)){
						$cR = count($result);
						for($i = 0; $i < $cR; $i++){
							if(isset($result[$i]["Connection"]["id"])){
								$this->Connection->updateAll(
										array("Connection.send" => true),
										array("Connection.id" => $result[$i]["Connection"]["id"]));
					
								unset($result[$i]["Connection"]["id"]);//ID löschen um Verwirrung zu vermeiden.
							}
						}
					}
					
					$return = array("chatSession" => $result);
				}else{
					$return = array("disconnected");
				}									
			}
				
			
			$this->set("result", json_encode($return));			
 	 		$this->layout = "ajax";
		}else{
			$this->set("result", __("No Access"));
		}
	}
	
	/**
	 * Gegenstück zu Connect
	 * @sicher
	 */
	public function serviceConnect(){
		
		if($this->request->is("post")){
			$this->loadModel("Connection");
			
			//Tut das gleiche wie Contact		
			$this->Connection->create();
			if($this->Connection->save($this->request->data)){
				$result = array("chatSessionId" => $this->Connection->id);
			}else{
				$result = array("disconnected");
			}
			
			$this->set("result", json_encode($result));
			
			$this->layout = "ajax";
		}else{
			$this->set("result", __("No Access"));
		}				
	}
	
	/**
	 * Zerstört eine ChatSession
	 * @sicher
	 */
	public function destroyChatSession(){
		if($this->request->is("post")){
			$this->loadModel("Connection");
			$this->loadModel("User");
			
			$return = false;
			if(isset($this->request->data["Connection"]["id"]) && isset($this->request->data["User"]["sessionId"])){
				
				$objUser = $this->User->find("first", array("conditions" => array("User.sessionId" => $this->request->data("User.sessionId"))));
				
				if(!empty($objUser)){
					$curConnection = $this->Connection->find("first",
							array("conditions" => array(
									"Connection.id" => $this->request->data('Connection.id'),
									"OR" => array(
											"Connection.senderId" => $objUser["User"]["id"],
											"Connection.receiverId" => $objUser["User"]["id"]
									)
							)));
					
					if(!empty($curConnection)){
						//Lösche auch entgegengesetze Richtung
						$altConnection = $this->Connection->find("first", array("conditions" =>
								array(
										"Connection.senderId" => $curConnection["Connection"]["receiverId"],
										"Connection.receiverId" => $curConnection["Connection"]["senderId"]
								)));
							
						$this->Connection->delete($altConnection["Connection"]["id"]);
							
						$this->Connection->delete($this->request->data('Connection.id'));
						$return = true;
					}
				
					$result = array("chatDisconnected" => $return);
				}else{
					$result = array("disconnected");
				}																
			}
			
			
			
			$this->set("result", json_encode($result));
			
			$this->layout = "ajax";
		}else{
			$this->set("result", __("No Access"));
		}
		
	}
}

?>