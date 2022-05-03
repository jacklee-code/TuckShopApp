<?php

    include("connectDB.php");
    include("myLibrary.php");

    $json = json_decode(file_get_contents("php://input"), true);
	$success = True;
	$total_price = 	0.00;
	
    try {
        $userid = loginAndGetUserId($db, $json["Username"], $json["Password"]);
        if (strlen($userid) < 1 || !isStudent($db, $userid)) {
            http_response_code(403);
            return;
        }

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


            // Store total price
			$price = $stmt->fetchColumn() * $val;
			$total_price += $price;

            // Check Banned
            $sql = "SELECT * FROM Accounts as a, Banned AS b WHERE a.UserId = :userid AND a.UserId = b.StudentId AND b.FoodId = :foodid;";
            $stmt = $db->prepare($sql);
            $stmt->bindParam(":userid", $userid);
            $stmt->bindParam(":foodid", $key);
            $stmt->execute();
            $banned = $stmt->rowCount() > 0;

            $success = !$banned;
            if ($banned)
                break;

		}

        // Balance OK?
		if ($success) {
			$sql = "SELECT Balance FROM Accounts WHERE UserId = :userid;";
			$stmt = $db->prepare($sql);
			$stmt->bindParam(":userid", $userid);
			$stmt->execute();
			if ($stmt->rowCount() > 0) {
                $result = $stmt->fetch();
				$balance = $result["Balance"];
				$success = $balance - $total_price >= 0;
			} else 
				$success = False;
		}

        // INSERT record into DB
        if ($success) {

            // Add a new record
            $sql = "INSERT INTO BuyRecords (StudentId) VALUES (:userid);";
            $stmt = $db->prepare($sql);
            $stmt->bindParam(":userid", $userid);
            $stmt->execute();
            $recordid = $db->lastInsertId();

            // Remove Item + Add Item to Slot
            foreach($json["ItemList"] as $key => $val) {
                // Remove from quantity
                $sql = "UPDATE Foods SET Quantity = Quantity - :amount WHERE FoodId=:foodId;";
                $stmt = $db->prepare($sql);
                $stmt->bindParam(":foodId", $key, PDO::PARAM_INT);
                $stmt->bindParam(":amount", $val, PDO::PARAM_INT);
                $stmt->execute();

                //Add a new slot
                $sql = "INSERT INTO BuySlots (RecordId, FoodId, Quantity) VALUES (:recordid, :foodid, :quantity);";
                $stmt = $db->prepare($sql);
                $stmt->bindParam(":recordid", $recordid, PDO::PARAM_INT);
                $stmt->bindParam(":foodid", $key, PDO::PARAM_INT);
                $stmt->bindParam(":quantity", $val, PDO::PARAM_INT);
                $stmt->execute();
            }

            // Payment
            $sql = "UPDATE Accounts SET Balance = Balance - :amount WHERE UserId=:userId;";
            $statement = $db->prepare($sql);
            $statement->bindParam(":amount", $total_price);
            $statement->bindParam(":userId", $userid, PDO::PARAM_INT);
            $statement->execute();

            http_response_code(200);
        }
        else
            http_response_code(403);
		
    }
    catch (Exception $e) {
        http_response_code(403);
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
    }

?>