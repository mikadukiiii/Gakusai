<meta name="viewport" content="width=device-width" >

<?php
$path ='./../';

require_once ($path.'common/common.php'); //データーベース接続
require_once ($path.'common/common_user.php'); //common_user.php読み込み
require_once ($path.'manager/shop_manager.php'); //shop_manager.php読み込み
require_once ($path.'manager/favorite_manager.php'); //favorite_manager.php読み込み

?>
<html>
<head>
<meta charset="UTF-8">
<title>ASO SHOP</title>
<?php
include ($path.'common/js_css.php'); //js_css.php読み込み
include ($path.'common/js_css_user.php'); //js_css_user.php読み込み
include ($path.'common/cookie.php'); //cookie.php読み込み


?>
</head>
<body>
<div class="basewhite">
<?php

$id = $cookey;

$favorite = favorite_select_class($con,$id); //favorite_select_class関数呼び出し

include ($path.'common/header_user.php'); //header_user.php読み込み
echo '<br>';
	echo '<h1>お気に入りリスト</h1>';

foreach($favorite as $f){
	echo '<br/>';
	echo '<form action="./stores_detail.php" method="GET">';
		if(is_array($f)){
			echo"<button type='submit' name='id' id='id' value='".$f['shop_id']."'>";
			echo "<img src='".$path."image/".$f['genre_image']."' height='25'>"; 
            echo $f['shop_name'];
			echo"</button><br/>";
		}
}

$con = null; //データベース切断

?>
</div>
</body>
</html>