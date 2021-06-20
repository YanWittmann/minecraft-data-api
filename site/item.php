<?php
    include("login.php");


    $type = '';
    $name = '';
    $identifier = '';
    $isRenewable = '';
    $rarity = '';
    $stackableCount = '';
    $restores = '';


    if (isset($_POST['type'])) $type = $_POST['type'];
    else if (isset($_GET['type'])) $type = $_GET['type'];

    if (isset($_POST['name'])) $name = $_POST['name'];
    else if (isset($_GET['name'])) $name = $_GET['name'];

    if (isset($_POST['identifier'])) $identifier = $_POST['identifier'];
    else if (isset($_GET['identifier'])) $identifier = $_GET['identifier'];

    if (isset($_GET['isRenewable']) && ($_GET['isRenewable'] == 'true' || $_GET['isRenewable'] == '1')) $isRenewable = '1';
    else if (isset($_POST['isRenewable']) && ($_POST['isRenewable'] == 'true' || $_POST['isRenewable'] == '1')) $isRenewable = '1';
    else if(isset($_GET['isRenewable']) || isset($_POST['isRenewable'])) $isRenewable = '0';

    if (isset($_POST['rarity'])) $rarity = $_POST['rarity'];
    else if (isset($_GET['rarity'])) $rarity = $_GET['rarity'];

    if (isset($_POST['stackableCount'])) $stackableCount = $_POST['stackableCount'];
    else if (isset($_GET['stackableCount'])) $stackableCount = $_GET['stackableCount'];

    if (isset($_POST['restores'])) $restores = $_POST['restores'];
    else if (isset($_GET['restores'])) $restores = $_GET['restores'];


    $hasParam = false;
    $sql = "SELECT * FROM `items`";
    
    if (strlen($type) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' type = "' . $type . '"';
        $hasParam = true;
    }
    
    if (strlen($name) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' name = "' . $name . '"';
        $hasParam = true;
    }
    
    if (strlen($identifier) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' identifier = "' . $identifier . '"';
        $hasParam = true;
    }
    
    if (strlen($isRenewable) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' isRenewable = "' . $isRenewable . '"';
        $hasParam = true;
    }
    
    if (strlen($rarity) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' rarity = "' . $rarity . '"';
        $hasParam = true;
    }
    
    if (strlen($stackableCount) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' stackableCount = "' . $stackableCount . '"';
        $hasParam = true;
    }
    
    if (strlen($restores) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' restores = "' . $restores . '"';
        $hasParam = true;
    }

    $result = mysqli_query($db, $sql . ' ORDER BY `items`.`name` ASC');

    $items = array();
    $count = 0;

    while ($row = mysqli_fetch_array($result)) {
        $count = $count + 1;
        $item = array(
            "name" => $row['name'],
            "identifier" => $row['identifier'],
            "description" => $row['description'],
            "type" => $row['type'],
            "wikiPage" => $row['wikiPage'],
            "images" => $row['image'],
            "isRenewable" => $row['isRenewable'],
            "stackableCount" => $row['stackableCount'],
            "rarity" => $row['rarity'],
            "restores" => $row['restores']
        );
        foreach ($item as &$str) {
            $str = str_replace('"', '', $str);
        }
        array_push($items, $row['name'], $item);
    }

    $jsonResponse->count = $count;
    $jsonResponse->items = $items;

    echo str_replace("\\", "", json_encode($jsonResponse, JSON_NUMERIC_CHECK));

    $db -> close();

?>