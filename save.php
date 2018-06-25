<?php
// fetch variables
$data = isset($_POST['data']) ? $_POST['data'] : '';
$filename = isset($_POST['filename']) ? $_POST['filename'] : '';

$result = file_put_contents('logs/'.$filename, $data, FILE_APPEND);

echo $result;
?>