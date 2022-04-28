<?php
    include("connectDB.php");

    $username = $_POST["username"];
    $password = $_POST["password"];

    try {
        $sql_query = "SELECT a.UserId, a.Username, a.Fullname, a.Balance, t.TypeName AS AccountType FROM Accounts AS a, AccountType AS t WHERE a.Username=:username AND a.Password=:password AND a.TypeId=t.TypeId;";
        $statement = $db->prepare($sql_query);
        $statement->bindParam(":username", $username);
        $statement->bindParam(":password", $password);
        $statement->execute();
        $results = $statement->fetchAll(PDO::FETCH_ASSOC);
    }
    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getMessage(), "\n";
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