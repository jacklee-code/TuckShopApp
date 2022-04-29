<?php
	error_reporting(E_ALL);
	
    include("connectDB.php");
    $json = json_decode(file_get_contents("php://input"), true);
	$success = True;
	$total_price = 	0.00;
	
    try {
		// Check if quantity is enough and foodid is correct
        foreach($json["ItemList"] as $key => $val) {
			$sql = "SELECT Price FROM Foods WHERE FoodId = :key AND Quantity - :val >= 0;";
			$stmt = $db->prepare($sql);
			$stmt->bindParam(":key", $key);
			$stmt->bindParam(":val", $val);
			$stmt->execute();
			$success = $stmt->rowCount() > 0;
			if (!$success)
				break;
			
			$price = $stmt->fetchColumn() * $val;
			$total_price += $price;
			echo "Item $key cost $price<br>";

		}
		
		echo "Phase 1 Passed? ";
		var_dump($success);
		echo "<br>";
		
		if ($success) {
			$sql = "SELECT Balance FROM Accounts WHERE Username = :username and Password = :password;";
			$stmt = $db->prepare($sql);
			$stmt->bindParam(":username", $json["Username"]);
			$stmt->bindParam(":password", $json["Password"]);
			$stmt->execute();
			if ($stmt->rowCount() > 0) {
				$balance = $stmt->fetchColumn();
				echo "Balance: $balance  Total Cost: $total_price";
				$success = $balance - $total_price >= 0;
			} else 
				$success = False;
		}
		
		echo "Phase 2 Passed? ";
		var_dump($success);
		echo "<br>";
		
		
    }
    catch (Exception $e) {
        http_response_code(403);
        echo 'Caught exception: ',  $e->getMessage(), "\n";
    }

?>