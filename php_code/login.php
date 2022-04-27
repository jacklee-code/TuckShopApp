<?php
   include("connectDB.php");

   $sql_query = "SELECT * FROM Accounts";
   $statement = $db->prepare($sql_query);
   $statement->execute();

   $results = $statement->fetchA    ll(PDO::FETCH_ASSOC);
   $json = json_encode($results);
   echo $json;
?>