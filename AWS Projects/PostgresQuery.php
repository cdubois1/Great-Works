<!DOCTYPE html>
<html>
<title>W3.CSS</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<body>

<div class="w3-container">
  <h2>Search Results</h2>
  <p>This is an unsorted list of t-shirts from all searches</p>

  <table class="w3-table-all">
    <thead>
      <tr class="w3-light-grey">
        <th>ASIN</th>
        <th>Name</th>
        <th>Price</th>
		<th>Rank</th>
      </tr>
    </thead>
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
while($row = pg_fetch_array($result)){
	echo"<tr>";
	echo"<td> " .$row[0]. "</td>";
	echo"<td> " .$row[1]. "</td>";
	echo"<td> " .$row[2]. "</td>";
	echo"<td> " .$row[3]. "</td>";
	echo"</tr>";
}
pg_close($conn);
?>
  </table>
</div>

</body>
</html>

