<?php
    include("connectDB.php");
    include("myLibrary.php");

    try {
        $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
        $targetid = loginAndGetUserId($db, $_POST["linkusername"], $_POST["linkpassword"]);

        if (strlen($userid) < 1 || strlen($targetid) < 1 || !isParent($db, $userid)) {
            http_response_code(403);
            return;
        }

        $sql = "SELECT * FROM Linkage WHERE ParentId = :userid AND StudentId = :targetid;";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":userid", $userid, PDO::PARAM_INT);
        $stmt->bindParam(":targetid", $targetid, PDO::PARAM_INT);
        $stmt->execute();

        if ($stmt->rowCount() < 1) {
            $sql = "INSERT INTO Linkage (ParentId, StudentId) VALUE (:userid, :targetid);";
            $stmt = $db->prepare($sql);
            $stmt->bindParam(":userid", $userid, PDO::PARAM_INT);
            $stmt->bindParam(":targetid", $targetid, PDO::PARAM_INT);
            $stmt->execute();
        } else {
            http_response_code(400);
        }

    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
        http_response_code(403);
    }
?>
