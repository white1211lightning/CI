<?php defined('BASEPATH') OR exit('No direct script access allowed');

class Testdb extends CI_Controller {

	public function __construct(){
		parent::__construct();
		$this->load->model('Member');
	}
	
	public function index(){
		$data = array(
			'setup_js' => $this->setup_js(),
			'data'     => $this->Member->read(),
			'order_id' => ''
		);


		$this->load->view('templates/header', $data);
		$this->load->view('view');
		$this->load->view('templates/footer');
		
	}

	public function setup_js(){
		return 
<<<EOD
		//alert('Javascirpt做測試');
		
	
EOD;
	}

}