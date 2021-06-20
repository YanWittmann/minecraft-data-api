<?php
    include("login.php");
    echo("\n");

    if (isset($_POST['blockType']) && isset($_POST['minecraftIdentifier']) && isset($_POST['itemName']) && isset($_POST['description']) && isset($_POST['wikiPage']) && isset($_POST['images'])
        && isset($_POST['notes']) && isset($_POST['isRenewable']) && isset($_POST['stackableCount']) && isset($_POST['bestTool']) && isset($_POST['blastResistance']) && isset($_POST['hardness'])
        && isset($_POST['isLuminant']) && isset($_POST['isTransparent']) && isset($_POST['isFlammable']) && isset($_POST['isFlammableFromLava'])) {
        
        $sql = "INSERT INTO `blocks` (`id`, `name`, `identifier`, `description`, `type`, `wikiPage`, `images`, `notes`, `isRenewable`, `isLuminant`, `isTransparent`, `isFlammable`, `isFlammableFromLava`, `blastResistance`, `hardness`, `stackableCount`, `bestTool`) VALUES
        (NULL, '" . $_POST["itemName"] . "', '" . $_POST["minecraftIdentifier"] . "', '" . $_POST["description"] . "', '" . $_POST["blockType"] . "', '" . $_POST["wikiPage"] . "', '" . $_POST["images"] . "', '" . $_POST["notes"] . "', '" . $_POST["isRenewable"] . "', '" . $_POST["isLuminant"] . "', '" . $_POST["isTransparent"] . "', '" . $_POST["isFlammable"] . "', '" . $_POST["isFlammableFromLava"] . "', '" . $_POST["blastResistance"] . "', '" . $_POST["hardness"] . "', '" . $_POST["stackableCount"] . "', '" . $_POST["bestTool"] . "');";
        
        
        if ($db->query($sql) === TRUE) {
            echo "New record created successfully";
        } else {
            echo "Error: " . $sql . "<br>" . $db->error;
        }
        
        $db->close();
    } else {
        echo('Something is missing!');
        print_r($_POST);
    }
?>