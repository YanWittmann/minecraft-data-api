<?php
    function escape($var) {
        $var = str_replace("\"", "", $var);
        $var = str_replace("'", "", $var);
        $var = str_replace(";", "", $var);
        $var = str_replace("\\", "", $var);
        return $var;
    }
?>