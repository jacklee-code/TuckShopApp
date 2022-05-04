<?php
include "myLibrary.php";
include "connectDB.php";

try {
    if (!isset($_POST["username"]) || !isset($_POST["password"]))
        callForbidden();

    $userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);

    if (!isTeacher($db, $userid))
        callForbidden();

    // DELETE
    $supplierid = $_POST["supplierid"];

    $sql = "DELETE FROM Suppliers WHERE SupplierId = :supplierid;";
    $stmt = $db->prepare($sql);
    $stmt->bindParam(":supplierid", $supplierid);
    $stmt->execute();

} catch (Exception $e) {
    echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
    http_response_code(403);
}

?>