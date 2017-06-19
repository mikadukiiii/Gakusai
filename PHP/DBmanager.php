<?php

function connect(){//データベースに接続
    try {
		$table ='eddystone';
		$connectDB = 'mysql:dbname='.$table.';host=127.0.0.1;charset=utf8';
		$user = 'root';
		$pass = '';
        //ここでデータベースに接続
        $con = new PDO($connectDB, $user, $pass);
		return $con;
    }catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//管理者情報取得
function administrator_select($con,$id){ 
	try {
		if($id == 'null'){//全件取得
			$sql = 'SELECT * FROM ADMINISTRATOR_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}else{
			$sql = 'SELECT * FROM ADMINISTRATOR_TBL WHERE administrator_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
		}
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//管理者情報作成
function administrator_insert($con,$data){
	try{
			$sql = "INSERT INTO ADMINISTRATOR_TBL (administrator_id,administrator_pass,administrator_name,salt,shop_id) VALUES (:v0,:v1,:v2,:v3,:v4)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->bindParam(':v3', $data[3], PDO::PARAM_STR);
        	$stmt->bindParam(':v4', $data[4], PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId();  //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//管理者情報更新
function administrator_update($con,$data){
	try {
			$sql = "UPDATE ADMINISTRATOR_TBL SET administrator_pass = :v1 WHERE administrator_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//管理者情報削除
function administrator_delete($con,$id){
	try {
			$sql = "DELETE FROM ADMINISTRATOR_TBL WHERE administrator_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//ジャンル情報取得
function genre_select($con,$id){
	try {
		if($id == 'null'){//全件取得
			$sql = 'SELECT * FROM GENRE_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}else{
			$sql = 'SELECT * FROM GENRE_TBL WHERE genre_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
		}
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//ジャンル情報作成
function genre_insert($con,$data){
	try {
			$sql = "INSERT INTO GENRE_TBL (genre_name,genre_image) VALUES (:v0,:v1)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//ジャンル情報更新
function genre_update($con,$data){
	try {
			if(count($data) == 3){
				$sql = "UPDATE GENRE_TBL SET genre_name = :v1 , genre_image = :v2 WHERE genre_id = :v0";
				$stmt = $con -> prepare($sql);
				//変数を指定
        		$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        		$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        		$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        		$stmt->execute();//SQL文を実行
			}else{
				$sql = "UPDATE GENRE_TBL SET genre_name = :v1 , WHERE genre_id = :v0";
				$stmt = $con -> prepare($sql);
				//変数を指定
        		$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        		$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        		$stmt->execute();//SQL文を実行
			}
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//ジャンル情報削除
function genre_delete($con,$id){
	try {
			$sql = "DELETE FROM GENRE_TBL WHERE genre_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//校舎情報取得
function school_select($con,$id){
	try {
		if($id == 'null'){//全件取得
			$sql = 'SELECT * FROM SCHOOL_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}else{
			$sql = 'SELECT * FROM SCHOOL_TBL WHERE school_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
		}
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//校舎情報作成
function school_insert($con,$data){
	try {
			$sql = "INSERT INTO SCHOOL_TBL (school_name,ad_id) VALUES (:v0,:v1)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//校舎情報更新
function school_update($con,$data){
	try {
			$sql = "UPDATE SCHOOL_TBL SET school_name = :v1 , ad_id = :v2 WHERE school_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//広告時間帯情報削除
function school_delete($con,$id){
	try {
			$sql = "DELETE FROM SCHOOL_TBL WHERE school_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//広告時間帯情報取得
function ad_select($con,$id){
	try {
		if($id == 'null'){//全件取得
			$sql = 'SELECT * FROM AD_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}else{
			$sql = 'SELECT * FROM AD_TBL WHERE ad_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
		}
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//広告時間帯情報作成
function ad_insert($con,$data){
	try {
			$sql = "INSERT INTO AD_TBL (ad_start_time,ad_finish_time) VALUES (:v0,:v1)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//広告時間帯情報更新
function ad_update($con,$data){
	try {
			$sql = "UPDATE AD_TBL SET ad_start_time = :v1 , ad_finish_time = :v2 WHERE ad_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//広告時間帯情報削除
function ad_delete($con,$id){
	try {
			$sql = "DELETE FROM AD_TBL WHERE ad_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//ログ情報作成
function log_insert($con,$data){
	try {
			$sql = "INSERT INTO LOG_TBL (administrator_id,log_table,log_sql,log_primary_key,log_time) VALUES (:v0,:v1,:v2,:v3,:v4)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->bindParam(':v3', $data[3], PDO::PARAM_STR);
        	$stmt->bindParam(':v4', $data[4], PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//ログ情報取得
function log_select($con){
	try {
			$sql = 'SELECT * FROM LOG_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;

	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//フロア情報取得
function floor_select($con,$id){
	try {
		if($id == 'null'){//全件取得
			$sql = 'SELECT * FROM FLOOR_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}else{
			$sql = 'SELECT * FROM FLOOR_TBL WHERE floor_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
		}
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//フロア情報作成
function floor_insert($con,$data){
	try {
			$sql = "INSERT INTO  FLOOR_TBL (floor_name) VALUES (:v0)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data, PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//フロア情報更新
function floor_update($con,$data){
	try {
			$sql = "UPDATE FLOOR_TBL SET floor_name = :v1 WHERE floor_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//フロア情報削除
function floor_delete($con,$id){
	try {
			$sql = "DELETE FROM FLOOR_TBL WHERE floor_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//広告背景情報取得
function ad_back_select($con,$id){
	try {
		if($id == 'null'){//全件取得
			$sql = 'SELECT * FROM AD_BACK_IMAGE_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}else{
			$sql = 'SELECT * FROM AD_BACK_IMAGE_TBL WHERE ad_back_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
		}
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//広告背景情報作成
function ad_back_insert($con,$data){
	try {
			$sql = "INSERT INTO  AD_BACK_IMAGE_TBL (ad_back_image) VALUES (:v0)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data, PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//広告背景情報更新
function ad_back_update($con,$data){
	try {
			$sql = "UPDATE AD_BACK_IMAGE_TBL SET ad_back_image = :v1 WHERE ad_back_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//広告背景情報削除
function ad_back_delete($con,$id){
	try {
			$sql = "DELETE FROM AD_BACK_IMAGE_TBL WHERE ad_back_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//履歴情報作成
function history_insert($con,$data){
	try {
			$sql = "INSERT INTO HISTORY_TBL (school_id,cookey,come_by,device, browser,history_time) VALUES (:v0,:v1,:v2,:v3,:v4,:v5)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->bindParam(':v3', $data[3], PDO::PARAM_STR);
        	$stmt->bindParam(':v4', $data[4], PDO::PARAM_STR);
        	$stmt->bindParam(':v5', $data[5], PDO::PARAM_STR);
        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//履歴情報取得
function history_select($con){
	try {
			$sql = 'SELECT * FROM HISTORY_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}catch(PODException $e){
		//SQLの発行を失敗した際のエラーメッセージ
		exit('SQL失敗。'.$e->getMessage());
		}
}

//cookie情報取得
function cookey_select($con){
	try {
			$sql = 'SELECT * FROM COOKEY_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0][0];
		}catch(PODException $e){
			//SQLの発行を失敗した際のエラーメッセージ
			exit('SQL失敗。'.$e->getMessage());
		}
}

//cookie情報更新
function cookey_add($con){
	try {
			$sql = "UPDATE COOKEY_TBL set cookey = cookey + 1";
			$stmt = $con -> prepare($sql);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//お気に入り確認
function favorite_select($con,$id){
	try {
			$sql = 'SELECT * FROM  FAVORITE_TBL WHERE cookey = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}catch(PODException $e){
			//SQLの発行を失敗した際のエラーメッセージ
			exit('SQL失敗。'.$e->getMessage());
		}
}

//お気に入り登録
function favorite_insert($con,$data){
	try {
			$sql = "INSERT INTO  FAVORITE_TBL (cookey,shop_id) VALUES (:v0,:v1)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);

        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
		}catch(PODException $e){
			//SQLの発行を失敗した際のエラーメッセージ
			exit('SQL失敗。'.$e->getMessage());
		}
}

//お気に入り削除
function favorite_delete($con,$data){
	try {
			$sql = "DELETE FROM FAVORITE_TBL WHERE cookey = :v0 AND shop_id = :v1";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//店舗情報取得
function shop_select($con,$id){
	try {
		if($id == 'null'){//全件取得
			$sql = 'SELECT * FROM SHOP_TBL';
			$stmt = $con -> prepare($sql);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
		}else{
			$sql = 'SELECT * FROM SHOP_TBL WHERE shop_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
		}
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//店舗情報作成
function shop_insert($con,$data){
	try {
			$sql = "INSERT INTO SHOP_TBL (school_id,shop_name,floor_id,shop_class,place) VALUES (:v0,:v1,:v2,:v3,:v4)";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->bindParam(':v3', $data[3], PDO::PARAM_STR);
			$stmt->bindParam(':v4', $data[4], PDO::PARAM_STR);

        	$stmt->execute();//SQL実行
		//	$numbers = $con -> lastInsertId(); //オートナンバーの値を取得
		//	return $numbers;	
	}catch(PODException $e){
	//SQLの発行を失敗した際のエラーメッセージ
	exit('SQL失敗。'.$e->getMessage());
	}
}

//店舗情報更新
function shop_update($con,$data){
	try {
		  if(count($data) == 13){
			$sql = "UPDATE SHOP_TBL set genre_id = :v1 , school_id = :v2 , floor_id = :v3 , ad_back_id = :v4 , shop_name = :v5 , shop_detail = :v6 , shop_image = :v7 , ad_top_text = :v8 , ad_text = :v9 , ad_color = :v10 , shop_class = :v11 , place = :v12 WHERE shop_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->bindParam(':v3', $data[3], PDO::PARAM_STR);
        	$stmt->bindParam(':v4', $data[4], PDO::PARAM_STR);
        	$stmt->bindParam(':v5', $data[5], PDO::PARAM_STR);
        	$stmt->bindParam(':v6', $data[6], PDO::PARAM_STR);
        	$stmt->bindParam(':v7', $data[7], PDO::PARAM_STR);
        	$stmt->bindParam(':v8', $data[8], PDO::PARAM_STR);
        	$stmt->bindParam(':v9', $data[9], PDO::PARAM_STR);
        	$stmt->bindParam(':v10', $data[10], PDO::PARAM_STR);
        	$stmt->bindParam(':v11', $data[11], PDO::PARAM_STR);
        	$stmt->bindParam(':v12', $data[12], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
		  }else{
			$sql = "UPDATE SHOP_TBL set genre_id = :v1 , school_id = :v2 , floor_id = :v3 , ad_back_id = :v4 , shop_name = :v5 , shop_detail = :v6 , ad_top_text = :v8 , ad_text = :v9 , ad_color = :v10 , shop_class = :v11 , place = :v12 WHERE shop_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
        	$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
        	$stmt->bindParam(':v2', $data[2], PDO::PARAM_STR);
        	$stmt->bindParam(':v3', $data[3], PDO::PARAM_STR);
        	$stmt->bindParam(':v4', $data[4], PDO::PARAM_STR);
        	$stmt->bindParam(':v5', $data[5], PDO::PARAM_STR);
        	$stmt->bindParam(':v6', $data[6], PDO::PARAM_STR);
        	$stmt->bindParam(':v8', $data[7], PDO::PARAM_STR);
        	$stmt->bindParam(':v9', $data[8], PDO::PARAM_STR);
        	$stmt->bindParam(':v10', $data[9], PDO::PARAM_STR);
        	$stmt->bindParam(':v11', $data[10], PDO::PARAM_STR);
        	$stmt->bindParam(':v12', $data[11], PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
		  }
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//店舗情報削除
function shop_delete($con,$id){
	try {
			$sql = "DELETE FROM SHOP_TBL WHERE shop_id = :v0";
			$stmt = $con -> prepare($sql);
			//変数を指定
        	$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
        	$stmt->execute();//SQL文を実行
        }catch(PODException $e){
            //SQLの発行を失敗した際のエラーメッセージ
            exit('SQL失敗。'.$e->getMessage());
        }
}

//店舗別管理者情報取得
function administrator_select_shop($con,$id){
	try {
			$sql = 'SELECT * FROM ADMINISTRATOR_TBL WHERE shop_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
	} catch (Exception $e) {
		echo ('システムエラーが発生しました。');
	}
}

//校舎別店舗情報取得
function shop_select_school($con,$id){
	try {
			$sql = 'SELECT * FROM SHOP_TBL WHERE school_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0];
	} catch (Exception $e) {
		echo ('システムエラーが発生しました。');
	}
}

//校舎フロア別店舗情報取得
function shop_select_school_floor($con,$school,$kai){
	try {
			$sql = 'SELECT * FROM SHOP_TBL WHERE school_id = :v0 and floor_id = :v1';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $school, PDO::PARAM_STR);
			$stmt->bindParam(':v1', $kai, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
	} catch (Exception $e) {
		echo ('システムエラーが発生しました。');
	}
}

//広告時間帯別店舗情報取得
function school_select_ad($con,$id){
	try {
			$sql = 'SELECT * FROM SCHOOL_TBL WHERE ad_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;
	} catch (Exception $e) {
		echo ('システムエラーが発生しました。');
	}
}

//店舗別お気に入り数確認
function favorite_count_shop($con,$id){
	try {
			$sql = 'SELECT COUNT(*) FROM FAVORITE_TBL WHERE shop_id = :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $id, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array[0][0];

	} catch (Exception $e) {
		echo ('システムエラーが発生しました。');
	}
}


function favorite_check($con,$data){
	try {
			$sql = 'SELECT COUNT(*) FROM FAVORITE_TBL WHERE shop_id = :v0 AND cookey = :v1';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $data[0], PDO::PARAM_STR);
			$stmt->bindParam(':v1', $data[1], PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			$value;
				if($array[0][0] == '1'){
					$value = 'true';
				}else{
					$value = 'false';
				}
			return $value;
	} catch (Exception $e) {
		echo ('システムエラーが発生しました。');
	}
}

function ad_select_time($con,$data){
	try {
			$sql = 'SELECT * FROM AD_TBL WHERE ad_start_time <= :v0 AND ad_finish_time > :v0';
			$stmt = $con -> prepare($sql);
			$stmt->bindParam(':v0', $data, PDO::PARAM_STR);
			$stmt -> execute();
			$array = $stmt -> fetchAll();
			return $array;

	} catch (Exception $e) {
		echo ('システムエラーが発生しました。');
	}
}
?>