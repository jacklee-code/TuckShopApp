<?php
    include "connectDB.php";
    include "myLibrary.php";

    $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
    $targetid = $_POST["targetid"];
    $foodid = $_POST["foodid"];

    try {
        if (strlen($userid) < 1 || !isParent($db, $userid)) {
            http_response_code(403);
            return;
        }

        // Check Linkage
        $sql = "SELECT * FROM Linkage WHERE ParentId = :userid AND StudentId = :targetid;";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":userid", $userid, PDO::PARAM_INT);
        $stmt->bindParam(":targetid", $targetid, PDO::PARAM_INT);
        $stmt->execute();

        if ($stmt->rowCount() == 0) {
            http_response_code(403);
            return;
        }

        $sql = "INSERT INTO Banned (StudentId, FoodId) VALUES (:targetid, :foodid);";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":foodid", $foodid, PDO::PARAM_INT);
        $stmt->bindParam(":targetid", $targetid, PDO::PARAM_INT);
        $stmt->execute();

    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }
?>