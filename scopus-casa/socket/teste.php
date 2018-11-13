
<?php
error_reporting(E_ALL);

/* Allow the  to hang around waiting for connections. */
set_time_limit(0);

/* Turn on implicit output flushing so we see what we're getting
 * as it comes in. */
ob_implicit_flush();

$address = '192.168.1.105';
$port = 5000;

if (($sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP)) === false) {
    echo "socket_create() failed: reason: " . socket_strerror(socket_last_error()) . "\n";
} 

if (socket_bind($sock, $address, $port) === false) {
    echo "socket_bind() failed: reason: " . socket_strerror(socket_last_error($sock)) . "\n";
}

if (socket_listen($sock, 5) === false) {
    echo "socket_listen() failed: reason: " . socket_strerror(socket_last_error($sock)) . "\n";
}

do {
    if (($msgsock = socket_accept($sock)) === false) {
        echo "socket_accept() failed: reason: " . socket_strerror(socket_last_error($sock)) . "\n";
        break;
    }


     if (($msgsock2 = socket_accept($sock)) === false) {
         echo "socket_accept() failed: reason: " . socket_strerror(socket_last_error($sock)) . "\n";
         break;
     }
    /* Send instructions. */
    $msg = "Welcome to the PHP Test Server.";
    socket_write($msgsock, $msg, strlen($msg));
    /* Send instructions. */
    $msg = "Welcome to the PHP Test Server22.";
    socket_write($msgsock2, $msg, strlen($msg));

    do {

        
        if (false === ($size = socket_read($msgsock, 8, PHP_NORMAL_READ))) {
            echo "socket_read() failed: reason: " . socket_strerror(socket_last_error($msgsock)) . "\n";
            break 2;
        }


        socket_write($msgsock2, $size, 8);
        
        /*if (false === ($resp = socket_read($msgsock2, 1, PHP_NORMAL_READ))) {
            echo "socket_read() failed: reason: " . socket_strerror(socket_last_error($msgsock)) . "\n";
            break 2;
        }


        socket_write($msgsock, $resp, 1);*/
        
        if (false === ($buf = socket_read($msgsock, 1266622, PHP_NORMAL_READ))) {
            echo "socket_read() failed: reason: " . socket_strerror(socket_last_error($msgsock)) . "\n";
            break 2;
        }



        if (!$buf = trim($buf)) {
            continue;
        }
        if ($buf == 'quit') {
            break;
        }
        if ($buf == 'shutdown') {
            socket_close($msgsock);
            break 2;
        }
        $talkback = "PHP: You said '$buf'.\n";
        socket_write($msgsock2, $buf, 1266622);
       // socket_write($msgsock2, $buf, 518400);
        //echo "$buf\n";
    } while (true);
    socket_close($msgsock);
} while (true);

socket_close($sock);
?>


