<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<?php
	require_once ($path.'common/common.php');
?>

<html>
<head>
<script type="text/javascript">
alert("’Ê‚Á‚Ä‚é");
alert("‚æI");
</script>
</head>
<body>


<?php

$data2 = array();
$data2[] = $_POST['data2'];
$data2[] = $_POST['data1'];

$data = array();
$data[] = $_POST['data1'];
$data[] = $_POST['data2'];


	$quryset=favorite_check($con,$date2); //favorite_checkŠÖ”ŒÄ‚Ño‚µ

	if($quryset == 'true'){
		favorite_delete($con,$data);
	}else{
		favorite_insert($con,$data);
}


$con = null;
?>

</body>
</html>