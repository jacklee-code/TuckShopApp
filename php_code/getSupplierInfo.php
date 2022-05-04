<?php
    include "myLibrary.php";
    include "connectDB.php";

    try {
        if (!isset($_POST["username"]) || !isset($_POST["password"]))
            callForbidden();

        $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
        if (strlen($userid) < 1)
            callForbidden();

        if (!isTeacher($db, $userid))
            callForbidden();

        // Get Supplier Info and Total Income
        $sql = "SELECT s.SupplierId, s.SupplierName, SUM(b.Quantity * f.Price) AS Income
                FROM Suppliers AS s, Foods AS f, BuySlots AS b
                WHERE b.FoodId = f.FoodId AND f.SupplierId = s.SupplierId 
                GROUP BY s.SupplierId;";

        $stmt = $db->prepare($sql);
        $stmt->execute();
        $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

        echo json_encode($results, JSON_NUMERIC_CHECK);

    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }

?>