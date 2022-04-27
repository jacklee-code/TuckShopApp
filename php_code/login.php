<?php
   include("connectDB.php");

   $sql_query = "SELECT UserID, Username, Fullname FROM Accounts";
   $statement = $db->prepare($sql_query);
   $statement->execute();

   $results = $statement->fetchAll(PDO::FETCH_ASSOC);
   $json = json_encode($results);
   echo $json;
?>