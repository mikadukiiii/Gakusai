<meta name="viewport" content="width=device-width" >

<?php
$path ='./../';

require_once ($path.'common/common.php'); //データーベース接続
require_once ($path.'common/common_user.php'); //common_user.php読み込み
require_once ($path.'manager/shop_manager.php'); //shop_manager.php読み込み

?>
<html>
<meta name="viewport" content="width=300" >

<head>
<title>ASO SHOP</title>
<?php
include ($path.'common/js_css.php'); //s_css.php読み込み
include ($path.'common/js_css_user.php'); //js_css_user.php読み込み
include ($path.'common/cookie.php'); //cookie.php読み込み

$date = array();
$date[] = $_GET['id'];
$date[] = $cookey;

?>

<?PHP

	$quryset=favorite_check($con,$date); //favorite_check関数呼び出し
	//$data = mysql_fetch_row($quryset);


	if($quryset == 'true'){
		$cnt=1;
	}else{
		$cnt=0;
}
?>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.js"></script>

<script type="text/javascript">
		var cnt = "<?php echo $cnt ?>";

		// img0.jpg,img1.jpgなどの数字が続いたファイルを複数用意します。
		nme = "./../img/star" // 画像のディレクトリとファイル名の数字と拡張子より前の部分
		exp = "png" // 拡張子

		
		function changeImage() { 

			cnt++;
			cnt %= 2;
			document.img.src = nme + cnt + "." + exp;
			var Cookey = "<?php echo $date[1]; ?>";
			var Sid = "<?php echo $date[0]; ?>"; 

				$.ajax({
    					type:'POST',
					url:'favorite_insert.php',
					data:{
					'data1': Cookey ,'data2': Sid
					},
				success: function(data) {

					//通信の確認
					//alert('success!!');
				},
				error: function(data) {
					alert('error!!!');
				}
				});
		}

</script>



</head>
<body>
<div class="basewhite">

<?php
	$shop_id = $_GET['id']; //shop_id取得

	$selectall = shop_select_class($con,$shop_id); //shop_select_class関数呼び出し
	$school_id = $selectall['school_id']; 
	$school_name = $selectall['school_name'];
	include ($path.'common/header_user.php');
			echo '<br>';
			echo '<h1>';
            echo $selectall['floor_name']."　".$selectall['place']; 
            echo '<br>';
            echo "<img src='".$path."image/".$selectall['genre_image']."' height='25'>"; 
            echo $selectall['shop_name'] ; 
            echo '</h1>';
            echo '<br>';

		$quryset2 = favorite_check($con,$date);
		//$data2 = mysql_fetch_row($quryset);

if($quryset2 == 'true'){
	echo "<A href=\"JavaScript:changeImage()\" >";
	echo "<IMG src=\"./../img/star1.png\" name=\"img\" id=\"img\" border=\"0\"></A><BR>";
	}else{
	echo "<A href=\"JavaScript:changeImage()\">";
	echo "<IMG src=\"./../img/star0.png\" name=\"img\" id=\"img\" border=\"0\"></A><BR>";

}
			echo '<br>';
            echo '<p>';
            echo "<img src='".$path."image/".$selectall['shop_image']."' height='150px'>";
            echo '</p>';
            echo '<div class="tenpo">';
                  echo $selectall['shop_detail']; 
			echo '</div>';	
                  echo '<br><div class="relative">';
                  echo '<div align="center">';
                  echo "<img src='".$path."image/".$selectall['ad_back_image']."' width='400px'>";
				  echo '</div>';
				  echo "<p class='absolute'><font color='".$selectall['ad_color']."'><b>";
                  echo $selectall['ad_top_text'];
                  echo '</b></font><br>';
                  echo $selectall['ad_text'];
                  echo '</p></div>';


           ?>
<BR>


</div>
</body>
</html>