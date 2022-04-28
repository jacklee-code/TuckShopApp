<?php
    include("connectDB.php");

    $username = $_POST["username"];
    $password = $_POST["password"];
    $fullname = $_POST["fullname"];
    $typeid = $_POST["typeid"];

    try {
        
        $sql_query = "INSERT INTO Accounts (Username, Password, Fullname, TypeId) VALUE (:username, :password, :fullname, :typeid);";
        $statement = $db->prepare($sql_query);
        $statement->bindParam(":username", $username);
        $statement->bindParam(":password", $password);
        $statement->bindParam(":fullname", $fullname);
        $statement->bindParam(":typeid", $typeid);
        $statement->execute();
        
    } catch(PDOException $e) {
        http_response_code(403);
        return;
    }

    http_response_code(200);
?>