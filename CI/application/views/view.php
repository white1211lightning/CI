<div class="container-fluid">
	<h1>表單資料</h1>

	<div>
	<?php
		foreach ($data as $key) {
			echo $key->account.'<br/>';
		}	
	?>
	<span><?php echo $order_id; ?></span>
	
	<!-- Provides extra visual weight and identifies the primary action in a set of buttons -->
	<button type="button" class="btn btn-primary">Primary</button>

	<!-- Indicates a successful or positive action -->
	<button type="button" class="btn btn-success">Success</button>

	<!-- Contextual button for informational alert messages -->
	<button type="button" class="btn btn-info">Info</button>

	<!-- Indicates caution should be taken with this action -->
	<button type="button" class="btn btn-warning">Warning</button>

	<!-- Indicates a dangerous or potentially negative action -->
	<button type="button" class="btn btn-danger">Danger</button>
	
	<br />
	

	</div>

</div>