<?php
    include("connectDB.php");
    include("myLibrary.php");

    $username = $_POST["username"];
    $password = $_POST["password"];
    $userid = "";
    $results = null;

    try {
        $userid = loginAndGetUserId($db, $username, $password);
        if (strlen($userid) < 1) {
            http_response_code(403);
            return;
        } else {

            if (isStudent($db, $userid)) {
                $sql = "SELECT a.UserId, a.Username, a.Fullname, a.Balance, t.TypeName AS AccountType FROM Accounts AS a, AccountType AS t WHERE a.UserId=:userid AND a.TypeId=t.TypeId;";
                $statement = $db->prepare($sql);
                $statement->bindParam(":userid", $userid, PDO::PARAM_INT);
                $statement->execute();
                $results = $statement->fetch(PDO::FETCH_ASSOC);
            } else if (isParent($db, $userid)) {
                $sql = "SELECT a.UserId, a.Username, a.Fullname, a.Balance, t.TypeName AS AccountType FROM Accounts AS a, AccountType AS t, Linkage AS l 
                        WHERE a.UserId = l.StudentId AND l.ParentId = :userid AND a.TypeId=t.TypeId;";
                $statement = $db->prepare($sql);
                $statement->bindParam(":userid", $userid, PDO::PARAM_INT);
                $statement->execute();
                $results = $statement->fetchAll(PDO::FETCH_ASSOC);
            } else if (isTeacher($db, $userid)) {

            }
        }


    }
    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
        http_response_code(403);
    }

    if($statement->rowCount() < 1) {
       http_response_code(403);
       return;
    } else {
       http_response_code(200);
    }

    $json = json_encode($results, JSON_NUMERIC_CHECK);
    echo $json;
?>