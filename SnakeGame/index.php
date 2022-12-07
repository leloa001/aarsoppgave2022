<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>

    <?php
    // compile and run java on web

    shell_exec(javac gameFrame.java && gamePanel.java && javac snakeGame.java)
    shell_exec(java snakeGame.java)
    ?>

</body>
</html>