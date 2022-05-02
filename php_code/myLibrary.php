<?php

    function isParent($db, $userid) {
        $sql = "SELECT t.TypeName AS type FROM Accounts AS a, AccountType AS t WHERE a.TypeId = t.TypeId AND UserId = :userid;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":userid", $userid);
        $statement->execute();
        $results = $statement->fetch(PDO::FETCH_ASSOC);
        if (count($results) == 0)
            return false;
        return strtolower($results["type"]) == "parent";
    }

    function isTeacher($db, $userid)
    {
        $sql = "SELECT t.TypeName AS type FROM Accounts AS a, AccountType AS t WHERE a.TypeId = t.TypeId AND UserId = :userid;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":userid", $userid);
        $statement->execute();
        $results = $statement->fetch(PDO::FETCH_ASSOC);
        if (count($results) == 0)
            return false;
        return strtolower($results["type"]) == "teacher";
    }

    function getUserTypeStringLower($db, $userid) {
        $sql = "SELECT t.TypeName AS type FROM Accounts AS a, AccountType AS t WHERE a.TypeId = t.TypeId AND UserId = :userid;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":userid", $userid);
        $statement->execute();
        $results = $statement->fetch(PDO::FETCH_ASSOC);
        if (count($results) == 0)
            return "";
        return strtolower($results["type"]);
    }

    function getUserIdByUsername($db, $username) {
        $sql = "SELECT UserId AS id FROM Accounts WHERE Username = :username;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":username", $username);
        $statement->execute();
        $results = $statement->fetch(PDO::FETCH_ASSOC);
        if (count($results) > 0)
            return $results["id"];
    }

    function isStudent($db, $userid)
    {
        $sql = "SELECT t.TypeName AS type FROM Accounts AS a, AccountType AS t WHERE a.TypeId = t.TypeId AND UserId = :userid;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":userid", $userid);
        $statement->execute();
        $results = $statement->fetch(PDO::FETCH_ASSOC);
        if (count($results) == 0)
            return false;
        return strtolower($results["type"]) == "student";
    }

    function loginAndGetUserId($db, $username, $password) {
        $sql = "SELECT UserId AS id FROM Accounts WHERE Username = :username AND Password = :password;";
        $statement = $db->prepare($sql);
        $statement->bindParam(":username", $username);
        $statement->bindParam(":password", $password);
        $statement->execute();

        if ($statement->rowCount() == 0)
            return "";

        $results = $statement->fetch(PDO::FETCH_ASSOC);
        return $results["id"];
    }

?>