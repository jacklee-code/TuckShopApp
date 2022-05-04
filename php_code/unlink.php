<?php
    include("connectDB.php");
    include("myLibrary.php");

    try {
        $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
        $targetid = $_POST["targetid"];

        if (strlen($userid) < 1 || strlen($targetid) < 1 || !isParent($db, $userid) || !isStudent($db, $targetid)) {
            http_response_code(403);
            return;
        }


        $sql = "DELETE FROM Linkage WHERE StudentId = :targetid AND ParentId = :userid;";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":userid", $userid, PDO::PARAM_INT);
        $stmt->bindParam(":targetid", $targetid, PDO::PARAM_INT);
        $stmt->execute();

    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
        http_response_code(403);
    }
?>
