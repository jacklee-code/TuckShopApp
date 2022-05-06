<?php
    include "myLibrary.php";
    include "connectDB.php";

    try {
        if (!isset($_POST["username"]) || !isset($_POST["password"]))
            callForbidden();

        $username = $_POST["username"];
        $password = $_POST["password"];

        $userid = loginAndGetUserId($db, $username, $password);

        if (strlen($userid) == 0 || !isTeacher($userid))
            callForbidden();

        //check arguments
        $passed = isset($_POST["foodid"]) && strlen($_POST["foodid"]) > 0;
        $passed = $passed && isset($_POST["foodname"]) && strlen($_POST["foodname"]) > 0;
        $passed = $passed && isset($_POST["supplierid"]) && strlen($_POST["supplierid"]) > 0;
        $passed = $passed && isset($_POST["typeid"]) && strlen($_POST["typeid"]) > 0;
        $passed = $passed && isset($_POST["quantity"]) && strlen($_POST["quantity"]) > 0;
        $passed = $passed && isset($_POST["price"]) && strlen($_POST["price"]) > 0;

        if (!$passed)
            callForbidden();

        //Binding variables
        $foodid = $_POST["foodid"];
        $foodname = $_POST["foodname"];
        $supplierid = $_POST["supplierid"];
        $typeid = $_POST["typeid"];
        $quantity = $_POST["quantity"];
        $price = $_POST["price"];

        $sql = "UPDATE Foods SET FoodName = :foodname, SupplierId = :supplierid, TypeId = :typeid, Quantity = :quantity, Price = :price WHERE FoodId = :foodid;";

    }

    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }
?>
