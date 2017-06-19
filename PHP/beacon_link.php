<meta name="viewport" content="width=device-width" >

<?php
$path ='./../';

require_once ($path.'common/common.php'); 
require_once ($path.'common/common_user.php'); 
require_once ($path.'manager/shop_manager.php');
require_once ($path.'manager/ad_manager.php');
?>
<html>
<head>
<meta charset="UTF-8">
<title>ASO SHOP</title>
<?php
include ($path.'common/js_css.php'); 
include ($path.'common/js_css_user.php');
include ($path.'common/js_cookie.php');


?>
    <link rel="stylesheet" href="js/flickity.min.css">
    <script src="js/jquery-2.1.0.min.js"></script>
    <script src="js/flickity.pkgd.min.js"></script>
    <script>
jQuery(function($){
  $('#main-gallery').flickity({
  wrapAround :'true'
    // ここでオプションを設定します。
  });
});
    </script>

</head>
<body>

<?php
$ad = ad_select_class($con);//広告取得

if (isset($_GET['id'])){
$id = $_GET['id'];
}

$shop = shop_select_school_class($con,$id);
$school = school_select($con,$id);
//$school_id = '';
//$school_name = '';
include ($path.'common/header_user.php');

  echo '<h2>'.$school['school_name'].'</h2>';

echo '<div id="main-gallery">';
foreach($ad as $ad_shop){
  echo "<div class='gallery-cell'>";
  echo '<div class="relative">';
    echo "<a href='./stores_detail.php?id=".$ad_shop['shop_id']."'><img src='".$path."image/".$ad_shop['ad_back_image']."' width='400' height='150'></a>";
  echo "<p class='absolute'><font color='".$ad_shop['ad_color']."'><b>";
    echo $ad_shop['ad_top_text'];
    echo '</b></font><br>';
    echo $ad_shop['ad_text'];
    echo '</p></div>';
  echo "</div>";
}
echo "</div><br/><br/>";


foreach($shop as $s){
  echo $s[0]['floor_name']."<br>";
  echo '<form action="./stores_detail.php" method="GET">';
  foreach ($s[1] as $selectall){

	$date = array();
	$date[] = $selectall['shop_id'];
	$date[] = 0; //クッキー

	//$quryset= favorite_check($con,$date);
	//$data2 = mysql_fetch_row($quryset);

	$date2 = 0;

    if(is_array($selectall)){
      echo"<button type='submit' name='id' id='id' value='".$selectall['shop_id']."'>";
      echo "<img src='".$path."image/".$selectall['genre_image']."' height='25'>"; 
            echo $selectall['shop_name'];
      echo"</button>";

if($data2>0){
	echo "<A href=\"JavaScript:changeImage()\" >";
	echo "<IMG src=\"./../img/star1.png\" name=\"img\" id=\"img\" border=\"0\"></A><BR>";
	}else{
	echo "<A href=\"JavaScript:changeImage()\">";
	echo "<IMG src=\"./../img/star0.png\" name=\"img\" id=\"img\" border=\"0\"></A><BR>";

}
  }
 }
}

//データベース切断
$con = null;

?>
</body>
</html>