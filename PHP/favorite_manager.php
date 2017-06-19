<?php

function favorite_select_class($con,$id){
    try {
	
	$array = favorite_select($con,$id);
	$favorite_list = array();
		foreach($array as $list){
			array_push($favorite_list,shop_select_class($con,$list['shop_id']));
		}
	return $array;
    }catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

?>