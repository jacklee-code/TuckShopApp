<?php
$host = getenv('TUCKSHOP_DB_HOST') ?: '127.0.0.1';
$port = getenv('TUCKSHOP_DB_PORT') ?: '3306';
$database = getenv('TUCKSHOP_DB_NAME');
$username = getenv('TUCKSHOP_DB_USER');
$password = getenv('TUCKSHOP_DB_PASSWORD');

if ($database === false || $username === false || $password === false) {
    throw new RuntimeException('Missing required TuckShop database environment variables.');
}

$dsn = sprintf(
    'mysql:host=%s;port=%s;dbname=%s;charset=utf8mb4',
    $host,
    $port,
    $database
);

try {
    $db = new PDO($dsn, $username, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);
} catch (PDOException $e) {
    error_log('Database connection failed: ' . $e->getMessage());
    http_response_code(500);
    exit('Database connection failed.');
}
?>
