<?php
include "connectDB.php";
include "myLibrary.php";

$userid = loginAndGetUserId($db, $_POST["username"], $_POST["password"]);
$targetid = $_POST["targetid"];
$foodid = $_POST["foodid"];

try {
    if (strlen($userid) < 1 || !isParent($db, $userid))
        callForbidden();

    // Check Linkage
    if (!isLinked($userid, $targetid))
        callForbidden();

    $sql = "DELETE FROM Banned WHERE StudentId = :targetid AND FoodId = :foodid;";
    $stmt = $db->prepare($sql);
    $stmt->bindParam(":foodid", $foodid, PDO::PARAM_INT);
    $stmt->bindParam(":targetid", $targetid, PDO::PARAM_INT);
    $stmt->execute();

} catch (Exception $e) {
    echo 'Caught exception: ',  $e->getTraceAsString(), "\n";
    http_response_code(403);
}
?>