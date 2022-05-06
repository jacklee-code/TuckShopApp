<?php
    include "myLibrary.php";
    include "connectDB.php";

    try {
        if (!isset($_POST["username"]) || !isset($_POST["password"]))
            callForbidden();


        $username = $_POST["username"];
        $password = $_POST["password"];

        $userid = loginAndGetUserId($db, $username, $password);

        if (strlen($userid) == 0 || !isTeacher($db, $userid))
            callForbidden();

        $passed = isset($_POST["foodid"]) && strlen($_POST["foodid"]) > 0;

        if (!$passed)
            callForbidden();

        //Binding variables
        $foodid = $_POST["foodid"];
        $sql = "DELETE FROM Foods WHERE FoodId = :foodid;";
        $stmt = $db->prepare($sql);
        $stmt->bindParam(":foodid", $foodid, PDO::PARAM_INT);
        $stmt->execute();
    }

    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }
?>
