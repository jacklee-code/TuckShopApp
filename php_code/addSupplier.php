<?php
    include "myLibrary.php";
    include "connectDB.php";

    try {
        if (!isset($_POST["username"]) || !isset($_POST["password"]))
            callForbidden();

        $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);

        if (!isTeacher($db, $userid))
            callForbidden();

        // INSERT
        $description = strlen($_POST["description"]) > 0 ? $_POST["description"] : "";

        $sql = "INSERT INTO Suppliers (SupplierName, SupplierDescription) VALUES (:name, :description);";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":name", $_POST["name"]);
        $stmt->bindParam(":description", $description);
        $stmt->execute();

    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }

?>