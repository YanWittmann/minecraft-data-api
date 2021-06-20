<?php
    include("login.php");


    $type = '';
    $name = '';
    $identifier = '';
    $isRenewable = '';
    $stackableCount = '';


    if (isset($_POST['type'])) $type = $_POST['type'];
    else if (isset($_GET['type'])) $type = $_GET['type'];

    if (isset($_POST['name'])) $name = $_POST['name'];
    else if (isset($_GET['name'])) $name = $_GET['name'];

    if (isset($_POST['identifier'])) $identifier = $_POST['identifier'];
    else if (isset($_GET['identifier'])) $identifier = $_GET['identifier'];

    if (isset($_GET['isRenewable']) && ($_GET['isRenewable'] == 'true' || $_GET['isRenewable'] == '1')) $isRenewable = '1';
    else if (isset($_POST['isRenewable']) && ($_POST['isRenewable'] == 'true' || $_POST['isRenewable'] == '1')) $isRenewable = '1';
    else if(isset($_GET['isRenewable']) || isset($_POST['isRenewable'])) $isRenewable = '0';

    if (isset($_POST['stackableCount'])) $stackableCount = $_POST['stackableCount'];
    else if (isset($_GET['stackableCount'])) $stackableCount = $_GET['stackableCount'];


    $hasParam = false;
    $sql = "";
    
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
    
    if (strlen($stackableCount) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' stackableCount = "' . $stackableCount . '"';
        $hasParam = true;
    }

    $result = mysqli_query($db, "SELECT * FROM `items`" . $sql . ' ORDER BY `items`.`name` ASC');

    $items = array();
    $countItems = 0;

    while ($row = mysqli_fetch_array($result)) {
        $countItems = $countItems + 1;
        $item = array(
            "name" => $row['name'],
            "identifier" => $row['identifier'],
            "description" => $row['description'],
            "type" => $row['type'],
            "wikiPage" => $row['wikiPage'],
            "image" => $row['image'],
            "isRenewable" => $row['isRenewable'],
            "stackableCount" => $row['stackableCount'],
            "rarity" => $row['rarity'],
            "restores" => $row['restores']
        );
        foreach ($item as &$str) {
            $str = str_replace('"', '', $str);
        }
        array_push($items, $item);
    }

    $itemPart->count = $countItems;
    $itemPart->items = $items;


    $result = mysqli_query($db, "SELECT * FROM `blocks`" . $sql . ' ORDER BY `blocks`.`name` ASC');

    $blocks = array();
    $countBlocks = 0;

    while ($row = mysqli_fetch_array($result)) {
        $countBlocks = $countBlocks + 1;
        $images = $row['images'];
        if (strpos($images, ', ') !== false) $images = explode(", ", $images);
        $block = array(
            "name" => $row['name'],
            "identifier" => $row['identifier'],
            "description" => $row['description'],
            "type" => $row['type'],
            "wikiPage" => $row['wikiPage'],
            "image" => $images,
            "notes" => array_filter(explode(", ", $row['notes']), create_function('$value', 'return $value !== "";')),
            "isRenewable" => $row['isRenewable'],
            "isLuminant" => $row['isLuminant'],
            "isTransparent" => $row['isTransparent'],
            "isFlammable" => $row['isFlammable'],
            "isFlammableFromLava" => $row['isFlammableFromLava'],
            "blastResistance" => $row['blastResistance'],
            "hardness" => $row['hardness'],
            "stackableCount" => $row['stackableCount'],
            "bestTool" => $row['bestTool']
        );
        foreach ($block as &$str) {
            $str = str_replace('"', '', $str);
        }
        array_push($blocks, $block);
    }

    $blockPart->count = $countBlocks;
    $blockPart->blocks = $blocks;


    $jsonResponse->totalCount = $countBlocks + $countItems;
    $jsonResponse->items = $itemPart;
    $jsonResponse->blocks = $blockPart;

    echo str_replace("\\", "", json_encode($jsonResponse, JSON_NUMERIC_CHECK));

    $db -> close();

?>