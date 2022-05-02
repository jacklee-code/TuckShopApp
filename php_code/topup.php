<?php
    include("connectDB.php");
    include("myLibrary.php");

    $username = $_POST["username"];
    $password = $_POST["password"];
    $targetId = $_POST["userId"];
    $amount = $_POST["amount"];

    try {
        $userid = loginAndGetUserId($db, $username, $password);

        if (strlen($userid) == 0) {
            http_response_code(403);
            return;
        }

        $type = getUserTypeStringLower($db, $userid);

        //Student Top Up
        if ($type == "student") {
            topup($db, $userid, $amount);
        } elseif ($type == "parent") {
            $sql = "SELECT * FROM Accounts AS a, Linkage AS l 
                    WHERE l.ParentId = a.UserId AND l.ParentId = :userid AND l.StudentId = :studentid;";
            $statement = $db->prepare($sql);
            $statement->bindParam(":userid", $userid);
            $statement->bindParam(":studentid", $targetId);
            $statement->execute();
            if ($statement->rowCount() > 0)
                topup($db, $targetId, $amount);
            else {
                http_response_code(403);
                return;
            }
        } else {
            http_response_code(403);
            return;
        }
    }
    catch (Exception $e) {
        http_response_code(403);
        echo 'Caught exception: ',  $e->getMessage(), "\n";
    }


    function topup($db, $id, $amount) {
        $sql = "UPDATE Accounts SET Balance = Balance + :amount WHERE UserId = :userid;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":userid", $id, PDO::PARAM_INT);
        $statement->bindParam(":amount", $amount);
        $statement->execute();
    }

?>