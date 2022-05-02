<?php
    include("connectDB.php");

    $username = $_POST["username"];
    $password = $_POST["password"];
    $targetId = $_POST["userId"];
    $amount = $_POST["amount"];

    try {
        $sql = "SELECT t.TypeName AS AccountType FROM Accounts AS a, AccountType AS t WHERE a.Username=:username AND a.Password=:password AND a.TypeId=t.TypeId;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":username", $username);
        $statement->bindParam(":password", $password);
        $statement->execute();
        $results = $statement->fetch(PDO::FETCH_ASSOC);

        $type = strtolower($results["AccountType"]);

        //Student Top Up
        if ($type == "student") {
            $sql = "UPDATE Accounts SET Balance = Balance + :amount WHERE Username=:username AND Password = :password AND UserId = :userId;";
            $statement = $db->prepare($sql);
            $statement->bindParam(":amount", $amount);
            $statement->bindParam(":username", $username);
            $statement->bindParam(":password", $password);
            $statement->bindParam(":userId", $targetId, PDO::PARAM_INT);
            $statement->execute();
        } elseif ($type == "parent") {

        } else {
            http_response_code(403);
            return;
        }
    }
    catch (Exception $e) {
        http_response_code(403);
        echo 'Caught exception: ',  $e->getMessage(), "\n";
    }

    if($statement->rowCount() < 1) {
       http_response_code(403);
       return;
    } else {
       http_response_code(200);
    }

?>