<?php
    include("connectDB.php");

    try {
        $sql_query = "SELECT f.FoodId, f.FoodName, t.TypeName AS FoodType, f.Quantity, f.Price, s.SupplierName AS Supplier FROM Foods AS f, Suppliers AS s, FoodType AS t WHERE f.TypeId = t.TypeId AND f.SupplierId = s.SupplierId;";
        $statement = $db->prepare($sql_query);
        $statement->execute();
        $results = $statement->fetchAll(PDO::FETCH_ASSOC);
    }
    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
        http_response_code(403);
    }

    $json = json_encode($results, JSON_NUMERIC_CHECK);
    echo $json;
?>