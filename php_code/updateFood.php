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

        //check arguments
        $editmode = isset($_POST["foodid"]) && strlen($_POST["foodid"]) > 0;

        $passed = isset($_POST["foodname"]) && strlen($_POST["foodname"]) > 0;
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

        $sql = "";

        if ($editmode)
            $sql = "UPDATE Foods SET FoodName = :foodname, SupplierId = :supplierid, TypeId = :typeid, Quantity = :quantity, Price = :price WHERE FoodId = :foodid;";
        else
            $sql= "INSERT INTO Foods (FoodName, SupplierId, TypeId, Quantity, Price) VALUES (:foodname, :supplierid, :typeid, :quantity, :price);";

        $stmt = $db->prepare($sql);
        if ($editmode)
            $stmt->bindParam(":foodid", $foodid, PDO::PARAM_INT);
        $stmt->bindParam(":foodname" ,$foodname);
        $stmt->bindParam(":supplierid" ,$supplierid, PDO::PARAM_INT);
        $stmt->bindParam(":typeid" ,$typeid, PDO::PARAM_INT);
        $stmt->bindParam(":quantity" ,$quantity,PDO::PARAM_INT);
        $stmt->bindParam(":price" ,$price);

        $stmt->execute();
    }

    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }
?>
