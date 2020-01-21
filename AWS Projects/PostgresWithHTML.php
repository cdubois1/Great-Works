<?php
$servername = "localhost";
$username = "other_user";
$password = "$other_user";
$dbname = "shirts";

//create connection
$conn = pg_connect('host=localhost port=5432 dbname=shirts user=other_user password=$other_user');
if(!$conn){
	die("Error connecting to DB".pg_last_error());
}
$sql = "SELECT * FROM shirts_attributes";
$results = pg_query($conn, $sql);
if(!$results){
	die("Error in sql query ".pg_last_error());
}
echo"<table border='1'>";
echo"<tr><td>ASIN</td><td>Name</td><td>Price</td><td>Rank</td></tr>";
while($row = pg_fetch_array($result)){
	echo("<tr>");
	echo("<td> " .$row[0]. "</td>");
	echo("<td> " .$row[1]. "</td>");
	echo("<td> " .$row[2]. "</td>");
	echo("<td> " .$row[3]. "</td>");
	//echo('<td>');
    //echo('<form method="post">');
    //echo('<button type="submit" name="test" value= ' . $i  . '  >view</button>');
    //echo('</form>');
    //echo('</td>');
    //echo('</tr>');
	echo("</tr>");
}
echo"</table>";
pg_close($conn);
?>
