<?php
    include("connectDB.php");

    $username = $_POST["username"];
    $password = $_POST["password"];

    $sql_query = "SELECT UserId, Username, Password, Fullname FROM Accounts where Username=:username AND Password = :password;";
    $statement = $db->prepare($sql_query);
    $statement->bindParam(":username", $username);
    $statement->bindParam(":password", $password);
    $statement->execute();

    $results = $statement->fetch(PDO::FETCH_ASSOC);

    if($statement->rowCount() < 1) {
       http_response_code(403);
       return;
    } else {
       http_response_code(200);
    }

    $json = json_encode($results, JSON_NUMERIC_CHECK);
    echo $json;
?>