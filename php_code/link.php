<?php
    include("connectDB.php");
    include("myLibrary.php");

    try {
        $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
        $targetid = loginAndGetUserId($db, $_POST["linkusername"], $_POST["linkpassword"]);

        if (strlen($userid) < 1 || strlen($targetid) < 1 || !isParent($db, $userid))
            callForbidden();

        if (isLinked($userid, $targetid))
            callForbidden();

        $sql = "INSERT INTO Linkage (ParentId, StudentId) VALUE (:userid, :targetid);";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":userid", $userid, PDO::PARAM_INT);
        $stmt->bindParam(":targetid", $targetid, PDO::PARAM_INT);
        $stmt->execute();

    } catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
        http_response_code(403);
    }
?>
