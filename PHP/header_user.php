<div class="header">
<?php
	echo "<a href='./'>店舗トップ</a>";
	
if(isset($school_name)){
	echo " ＞ <a href='./beacon_link.php?id=".$school_id."'>".$school_name."</a>";
}

	echo "<div align='right'>";
	echo '<a href="favorite_list.php"><img src="./../img/favorite.png" alt="favorite" border="0" width="20%" ></a>';
	echo "</div>"
?>
</div>