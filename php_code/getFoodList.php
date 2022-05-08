<?php
    error_reporting(E_ALL);

    include("connectDB.php");
    include("myLibrary.php");



    try {
        if (!isset($_POST["username"]) || !isset($_POST["password"]))
            callForbidden();

        $id = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
        $userid = isset($_POST["targetid"]) ? $_POST["targetid"] : "";


        $type = getUserTypeStringLower($db, $id);
        if (strlen($id) < 1 || strlen($type) < 1)
            callForbidden();

        if ($type == "student")
            $userid = $id;
        else if ($type == "parent")
            if (!isLinked($db, $id, $userid))
                callForbidden();

        $godmode = $type == "teacher";
        $json = "";

        $sql = "SELECT f.FoodId, f.FoodName, t.TypeName AS FoodType, f.TypeId, f.SupplierId, f.Quantity, f.Price, s.SupplierName AS Supplier
                FROM Foods AS f, Suppliers AS s, FoodType AS t WHERE f.TypeId = t.TypeId AND f.SupplierId = s.SupplierId;";
        $statement = $db->prepare($sql);
        $statement->execute();
        $results = $statement->fetchAll(PDO::FETCH_ASSOC);

        if (!$godmode)
            for ($x = 0; $x < count($results); $x++) {
                $sql = "SELECT * FROM Banned WHERE StudentId = :userid AND FoodId = :foodid;";
                $statement = $db->prepare($sql);
                $statement->bindParam(":userid", $userid, PDO::PARAM_INT);
                $statement->bindParam(":foodid", $results[$x]["FoodId"]);
                $statement->execute();
                $results[$x]["Banned"] = $statement->rowCount() > 0;
            }

        $json = json_encode($results, JSON_NUMERIC_CHECK);

        echo $json;
    }
    catch (Exception $e) {
        echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
        http_response_code(403);
    }

?>