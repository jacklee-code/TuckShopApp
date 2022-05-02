<?php
    error_reporting(E_ALL);

    include("connectDB.php");

    $userId = $_POST["UserId"];

    try {
        $sql_query = "SELECT f.FoodId, f.FoodName, t.TypeName AS FoodType, f.Quantity, f.Price, s.SupplierName AS Supplier
                      FROM Foods AS f, Suppliers AS s, FoodType AS t WHERE f.TypeId = t.TypeId AND f.SupplierId = s.SupplierId;";
        $statement = $db->prepare($sql_query);
        $statement->execute();
        $results = $statement->fetchAll(PDO::FETCH_ASSOC);

        for ($x = 0; $x < count($results); $x++) {
            $sql_query = "SELECT * FROM Banned WHERE StudentId = :userId AND FoodId = :foodId;";
            $statement = $db->prepare($sql_query);
            $statement->bindParam(":userId", $userId, PDO::PARAM_INT);
            $statement->bindParam(":foodId", $results[$x]["FoodId"], PDO::PARAM_INT);
            $statement->execute();
            $results[$x]["Banned"] = $statement->rowCount() > 0;
        }
    }
    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
        http_response_code(403);
    }

    $json = json_encode($results, JSON_NUMERIC_CHECK);
    echo $json;
?>