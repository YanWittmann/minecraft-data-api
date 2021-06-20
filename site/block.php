<?php
    include("login.php");


    $type = '';
    $name = '';
    $identifier = '';
    $isRenewable = '';
    $isLuminant = '';
    $isTransparent = '';
    $isFlammable = '';
    $isFlammableFromLava = '';
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

    if (isset($_GET['isLuminant']) && ($_GET['isLuminant'] == 'true' || $_GET['isLuminant'] == '1')) $isLuminant = '1';
    else if (isset($_POST['isLuminant']) && ($_POST['isLuminant'] == 'true' || $_POST['isLuminant'] == '1')) $isLuminant = '1';
    else if(isset($_GET['isLuminant']) || isset($_POST['isLuminant'])) $isLuminant = '0';

    if (isset($_GET['isTransparent']) && ($_GET['isTransparent'] == 'true' || $_GET['isTransparent'] == '1')) $isTransparent = '1';
    else if (isset($_POST['isTransparent']) && ($_POST['isTransparent'] == 'true' || $_POST['isTransparent'] == '1')) $isTransparent = '1';
    else if(isset($_GET['isTransparent']) || isset($_POST['isTransparent'])) $isTransparent = '0';

    if (isset($_GET['isFlammable']) && ($_GET['isFlammable'] == 'true' || $_GET['isFlammable'] == '1')) $isFlammable = '1';
    else if (isset($_POST['isFlammable']) && ($_POST['isFlammable'] == 'true' || $_POST['isFlammable'] == '1')) $isFlammable = '1';
    else if(isset($_GET['isFlammable']) || isset($_POST['isFlammable'])) $isFlammable = '0';

    if (isset($_GET['isFlammableFromLava']) && ($_GET['isFlammableFromLava'] == 'true' || $_GET['isFlammableFromLava'] == '1')) $isFlammableFromLava = '1';
    else if (isset($_POST['isFlammableFromLava']) && ($_POST['isFlammableFromLava'] == 'true' || $_POST['isFlammableFromLava'] == '1')) $isFlammableFromLava = '1';
    else if(isset($_GET['isFlammableFromLava']) || isset($_POST['isFlammableFromLava'])) $isFlammableFromLava = '0';

    if (isset($_POST['stackableCount'])) $stackableCount = $_POST['stackableCount'];
    else if (isset($_GET['stackableCount'])) $stackableCount = $_GET['stackableCount'];


    $hasParam = false;
    $sql = "SELECT * FROM `blocks`";
    
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
    
    if (strlen($isLuminant) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' isLuminant = "' . $isLuminant . '"';
        $hasParam = true;
    }
    
    if (strlen($isTransparent) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' isTransparent = "' . $isTransparent . '"';
        $hasParam = true;
    }
    
    if (strlen($isFlammable) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' isFlammable = "' . $isFlammable . '"';
        $hasParam = true;
    }
    
    if (strlen($isFlammableFromLava) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' isFlammableFromLava = "' . $isFlammableFromLava . '"';
        $hasParam = true;
    }
    
    if (strlen($stackableCount) > 0) {
        if (!$hasParam) $sql = $sql . ' WHERE ';
        else $sql = $sql . ' && ';
        $sql = $sql . ' stackableCount = "' . $stackableCount . '"';
        $hasParam = true;
    }


    $result = mysqli_query($db, $sql . ' ORDER BY `blocks`.`name` ASC');

    $items = array();
    $count = 0;

    while ($row = mysqli_fetch_array($result)) {
        $count = $count + 1;
        $images = $row['images'];
        if (strpos($images, ', ') !== false) $images = explode(", ", $images);
        $item = array(
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