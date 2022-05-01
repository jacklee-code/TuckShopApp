<?php

    include("connectDB.php");

    $userId = $_POST["UserId"];

    try {
        $sql = "SELECT RecordId, StudentId, FoodId_Amount AS jsonString, Time AS DateTime FROM BuyRecords WHERE StudentId = :userId ;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":userId", $userId);
        $statement->execute();
        $results = $statement->fetchAll(PDO::FETCH_ASSOC);

        if (count($results) > 0) {
            for ($x = 0; $x < count($results); $x++) {
                $foodList = json_decode($results[$x]["jsonString"], true);
                $foodArray = array();
                foreach ($foodList as $key => $val) {
                    $sql = "SELECT f.FoodId, f.FoodName, f.Price, f.Quantity, s.SupplierName AS Supplier, t.TypeName AS FoodType 
                            FROM Foods AS f, Suppliers AS s, FoodType AS t WHERE f.FoodId = :foodid
                            AND t.TypeId = f.TypeId AND s.SupplierId = f.SupplierId;";
                    $statement = $db->prepare($sql);
                    $statement->bindParam(":foodid", $key, PDO::PARAM_INT);
                    $statement->execute();
                    if ($statement->rowCount() > 0) {
                        $food = $statement->fetch(PDO::FETCH_ASSOC);
                        $food["Quantity"] = $val;
                        $foodArray[] = $food;
                    }
                }
                $results[$x]["jsonString"] = json_encode($foodArray, JSON_NUMERIC_CHECK);
            }
        }
    }
    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
        http_response_code(403);
    }

    $json = json_encode($results, JSON_NUMERIC_CHECK);
    echo $json;

?>