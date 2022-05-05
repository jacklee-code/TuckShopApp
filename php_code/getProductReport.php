<?php
    include "myLibrary.php";
    include "connectDB.php";

    try {
        $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);

        if (!isset($_POST["foodid"]))
            callForbidden();

        $foodid = $_POST["foodid"];

        if (!isTeacher($db, $userid))
            callForbidden();

        $sql = "SELECT s.RecordId, b.Time, s.Quantity, s.Quantity * f.Price AS Income
                FROM BuySlots AS s, BuyRecords AS b, Foods AS f
                WHERE s.RecordId = b.RecordId AND f.FoodId = s.FoodId AND f.FoodId = :foodid";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":foodid", $foodid, PDO::PARAM_INT);
        $stmt->execute();

        echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC),JSON_NUMERIC_CHECK);

    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }

?>
