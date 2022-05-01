<?php

    include("connectDB.php");
    $json = json_decode(file_get_contents("php://input"), true);
	$success = True;
	$total_price = 	0.00;
    $userid = "";
	
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

            $sql = "SELECT * FROM Accounts as a, Banned AS b WHERE a.Username = :username AND a.Password = :password AND a.UserId = b.StudentId AND b.FoodId = :foodid;";
            $stmt = $db->prepare($sql);
            $stmt->bindParam(":username", $json["Username"]);
            $stmt->bindParam(":password", $json["Password"]);
            $stmt->bindParam(":foodid", $key);
            $stmt->execute();
            $banned = $stmt->rowCount() > 0;

            $success = !$banned;
            if ($banned)
                break;

		}
		
		if ($success) {
			$sql = "SELECT UserId, Balance FROM Accounts WHERE Username = :username AND Password = :password;";
			$stmt = $db->prepare($sql);
			$stmt->bindParam(":username", $json["Username"]);
			$stmt->bindParam(":password", $json["Password"]);
			$stmt->execute();
			if ($stmt->rowCount() > 0) {
                $result = $stmt->fetch();
				$balance = $result["Balance"];
                $userid = $result["UserId"];
				$success = $balance - $total_price >= 0;
			} else 
				$success = False;
		}

        $jsonString = json_encode($json["ItemList"]);

        // INSERT record into DB
        if ($success) {
            $sql = "INSERT INTO BuyRecords (StudentId, FoodId_Amount) VALUES (:studentId, :json);";
            $stmt = $db->prepare($sql);
            $stmt->bindParam(":studentId", $userid);
            $stmt->bindParam(":json", $jsonString);
            $stmt->execute();

            // Remove Item
            foreach($json["ItemList"] as $key => $val) {
                $sql = "UPDATE Foods SET Quantity = Quantity - :amount WHERE FoodId=:foodId;";
                $stmt = $db->prepare($sql);
                $stmt->bindParam(":foodId", $key, PDO::PARAM_INT);
                $stmt->bindParam(":amount", $val, PDO::PARAM_INT);
                $stmt->execute();
            }

            // Payment
            $sql = "UPDATE Accounts SET Balance = Balance - :amount WHERE UserId=:userId;";
            $statement = $db->prepare($sql);
            $statement->bindParam(":amount", $total_price);
            $statement->bindParam(":userId", $userid, PDO::PARAM_INT);
            $statement->execute();
        }
		
    }
    catch (Exception $e) {
        http_response_code(403);
        echo 'Caught exception: ',  $e->getMessage(), "\n";
    }

    if ($success)
        http_response_code(200);
    else
        http_response_code(403);

?>