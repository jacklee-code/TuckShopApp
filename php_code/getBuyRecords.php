<?php

    include "connectDB.php";
    include "myLibrary.php";

    if (!isset($_POST["username"]) || !isset($_POST["password"]))
        callForbidden();

    $userId = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
    $targetId = isset($_POST["targetid"]) ? $_POST["targetid"] : "";

    try {
        $type = getUserTypeStringLower($db, $userId);
        if (strlen($userId) < 1 || strlen($type) < 1)
            callForbidden();

        if ($type == "student")
            $targetId = $userId;
        else if ($type == "parent")
            if (!isLinked($db, $userId, $targetId))
                callForbidden();


        $sql = "SELECT RecordId, StudentId, Time AS DateTime FROM BuyRecords WHERE StudentId = :userId ;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":userId", $targetId);
        $statement->execute();
        $results = $statement->fetchAll(PDO::FETCH_ASSOC);

        if (count($results) > 0) {
            for ($x = 0; $x < count($results); $x++) {
                $sql = "SELECT * FROM BuySlots WHERE RecordId = :id;";
                $statement = $db->prepare($sql);
                $statement->bindParam(":id", $results[$x]["RecordId"], PDO::PARAM_INT);
                $statement->execute();
                //$foodList = $statement->fetchAll(PDO::FETCH_ASSOC);

                $foodArray = $statement->fetchAll(PDO::FETCH_ASSOC);

                /*foreach ($foodList as $key => $val) {
                    $sql = "SELECT f.FoodId, f.FoodName, f.Price, s.SupplierName AS Supplier, t.TypeName AS FoodType
                            FROM Foods AS f, Suppliers AS s, FoodType AS t
                            WHERE f.FoodId = :foodid AND t.TypeId = f.TypeId AND s.SupplierId = f.SupplierId;";
                    $statement = $db->prepare($sql);
                    $statement->bindParam(":foodid", $val["FoodId"], PDO::PARAM_INT);
                    $statement->execute();
                    if ($statement->rowCount() > 0) {
                        $food = $statement->fetch(PDO::FETCH_ASSOC);
                        $food["Quantity"] = $val["Quantity"];
                        $foodArray[] = $food;
                    }
                }*/
                $results[$x]["jsonString"] = json_encode($foodArray, JSON_NUMERIC_CHECK);
            }
        }
    }
    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }

    $json = json_encode($results, JSON_NUMERIC_CHECK);
    echo $json;

?>