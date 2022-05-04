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
        $sql = "SELECT s.SupplierId AS Id, s.SupplierName AS Name, IFNULL(SUM(b.Quantity * f.Price), 0.00) AS Income, s.SupplierDescription AS Description
                FROM Suppliers AS s
                LEFT JOIN Foods AS f ON f.SupplierId = s.SupplierId 
                LEFT JOIN BuySlots AS b ON b.FoodId = f.FoodId
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