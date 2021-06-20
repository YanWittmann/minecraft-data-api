<?php
    include("login.php");
    echo("\n");

    if (isset($_POST['itemType']) && isset($_POST['minecraftIdentifier']) && isset($_POST['itemName']) && isset($_POST['description']) && isset($_POST['wikiPage']) && isset($_POST['image'])
        && isset($_POST['isRenewable']) && isset($_POST['stackableCount']) && isset($_POST['rarity']) && isset($_POST['restores'])) {
        
        $sql = "INSERT INTO `items` (`id`, `name`, `identifier`, `description`, `type`, `wikiPage`, `image`, `isRenewable`, `stackableCount`, `rarity`, `restores`) VALUES
        (NULL, '" . $_POST["itemName"] . "', '" . $_POST["minecraftIdentifier"] . "', '" . $_POST["description"] . "', '" . $_POST["itemType"] . "', '" . $_POST["wikiPage"] . "', '" . $_POST["image"] . "', '" . $_POST["isRenewable"] . "', '" . $_POST["stackableCount"] . "', '" . $_POST["rarity"] . "', '" . $_POST["restores"] . "')";
        
        
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