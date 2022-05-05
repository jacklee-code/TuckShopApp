<?php
    include "connectDB.php";

    try {
        $sql = "SELECT TypeId AS Id, TypeName AS Name FROM FoodType;";
        $stmt = $db->prepare($sql);
        $stmt->execute();
        echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC), JSON_NUMERIC_CHECK);
    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }
?>
