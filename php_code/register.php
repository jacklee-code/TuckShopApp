<?php
    include("connectDB.php");

    $username = $_POST["username"];
    $password = $_POST["password"];
    $fullname = $_POST["fullname"];

    try {
        
        $sql_query = "INSERT INTO Accounts (Username, Password, Fullname) VALUE (:username, :password, :fullname);";
        $statement = $db->prepare($sql_query);
        $statement->bindParam(":username", $username);
        $statement->bindParam(":password", $password);
        $statement->bindParam(":fullname", $fullname);
        $statement->execute();
        
    } catch(PDOException $e) {
        http_response_code(403);
        return;
    }

    http_response_code(200);
?>