<?php

class Member extends CI_Model{

	function read(){
		$query = $this->db->get('member',0);
		return $query->result();
	}

}