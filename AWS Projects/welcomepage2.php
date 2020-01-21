<!---This is the home page for the website--->
<?php
session_start();
?>
<!DOCTYPE html>
<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
//echo "<script>alert('IN PHP');</script>";
if(isset($_FILES["fileToUpload"])){
    echo "<script>alert('File recieved');</script>";
    $target_dir = "/var/www/html/keyfolder/";
    $target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
    $file_type = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));
    echo "<script>alert('starting compare');</script>";
    if(strcmp($file_type, "txt") == 0){
	if(file_exists($target_dir . $_FILES["fileToUpload"]["name"]))
	    unlink($target_dir . $_FILES["fileToUpload"]["name"]);
	if(move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)){
	    rename($target_file, $target_dir . "keywords.txt");
	    chmod($target_dir . "keywords.txt", 0777);
	    echo "<script>alert('successfully uploaded');</script>";
	    $file = fopen("./keyfolder/pagination.txt", "w");
	    $num_pages = $_POST["pagination"];
	    fwrite($file, $num_pages);
	    fclose($file);
	} else {
	    echo "<script>alert('File failed to upload');</script>";
	}
    } else {
	echo"<script>alert('please upload a txt file');</script>";
    }
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "http://localhost:6800/schedule.json");
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_POSTFIELDS, "project=ScrapyProject&spider=spider1");
    curl_setopt($ch, CURLOPT_POST, 1);
    $headers = array();
    $headers[] = 'Content-Type: application/x-www-form-urlencoded';
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    $result = curl_exec($ch);
    if (curl_errno($ch)) {
        echo 'Error:' . curl_error($ch);
    }
    curl_close($ch);
    unset($_FILES['fileToUpload']);
}
?>
<!=============BEGIN HTML==============>
<html lang="en">
<title>W3.CSS Template</title>
<meta charset="UTF-8">
<meta name="robots" content="noindex">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Poppins">
<script src="JavaScripts.js"></script>
<script src="PHPScripts.php"></script>
<style>
body,h1,h2,h3,h4,h5 {font-family: "Poppins", sans-serif}
body {font-size:16px;}
.w3-half img{margin-bottom:-6px;margin-top:16px;opacity:0.8;cursor:pointer}
.w3-half img:hover{opacity:1}
</style>
<body>

<!-- Sidebar/menu -->
<nav class="w3-sidebar w3-red w3-collapse w3-top w3-large w3-padding" style="z-index:3;width:300px;font-weight:bold;" id="mySidebar"><br>
  <a href="javascript:void(0)" onclick="w3_close()" class="w3-button w3-hide-large w3-display-topleft" style="width:100%;font-size:22px">Close Menu</a>
  <div class="w3-container">
    <h3 class="w3-padding-64"><b>Conrad^2<br>Inc.</b></h3>
  </div>
  <div class="w3-bar-block">
    <a href="#" onclick="w3_close()" class="w3-bar-item w3-button w3-hover-white">Run Spider</a> 
    <br>
    <form action="" method="post">
	<input type="text" placeholder="Full Text Search..." name="search">
	<p><input type="submit"/></p>
    </form>
    <form action="" method="post">
	<select name="Keyword">
	<?php
	$file = fopen("./keyfolder/db_keywords.txt", "r") or die ("unable to open keyword file");
	while(!feof($file)){
		$keyword = trim(fgets($file));
		echo "<option value=\"".$keyword."\">".$keyword."</option>";
	}
	fclose($file); 
	?>
	</select>
	<p><input type="submit"/></p>
    </form>
    <br>
    <form action="" method="post">
    <button type="submit" value="reset">Reset Search</button>
    </form>
    <br>
    <form action="" method="POST" enctype="multipart/form-data">
        <input type="file" name="fileToUpload" id="fileToUpload" />
	<input type="range" min="2" max="20" step="1" value="2" id="slider" name="pagination" onchange='document.getElementById("val").value = "Slider Value = " + document.getElementById("slider").value;'/>
	<input type="text" name="val" id="val" value="Slider Value = 2" disabled />
        <input type="submit" value="Upload Keywords" />
    </form>
  </div>
</nav>
<?php
if(isset($_POST['search'])){
    $_SESSION['searchString'] = $_POST['search'];
    unset($_POST['search']);
}
if(isset($_POST['Keyword'])){
    $_SESSION['keywordSearch'] = $_POST['Keyword'];
    unset($_POST['Keyword']);
}
if(isset($_POST['reset'])){
    unset($_SESSION['keywordSearch']);
    unset($_SESSION['searchString']);
    unset($_POST['Keyword']);
    unset($_POST['search']);
    unset($_POST['reset']);
}
?>

<!-- Top menu on small screens -->
<header class="w3-container w3-top w3-hide-large w3-red w3-xlarge w3-padding">
  <a href="javascript:void(0)" class="w3-button w3-red w3-margin-right" onclick="w3_open()">☰</a>
  <span>Conrad^2 Inc.</span>
</header>

<!-- Overlay effect when opening sidebar on small screens -->
<div class="w3-overlay w3-hide-large" onclick="w3_close()" style="cursor:pointer" title="close side menu" id="myOverlay"></div>

<!-- !PAGE CONTENT! -->
<div class="w3-main" style="margin-left:340px;margin-right:40px">

  <!-- Header -->
  <div class="w3-container" style="margin-top:80px" id="showcase">
    <h1 class="w3-jumbo"><b>Data Analysis</b></h1>
    <h1 class="w3-xxxlarge w3-text-red"><b>Analytics:.</b></h1>
    <hr style="width:50px;border:5px solid red" class="w3-round">
  </div>
<body>

<div class="w3-container">
  <h2>Search Results</h2>
  <p>Ordered by Rank</p>

  <table class="w3-table-all">
    <thead>
      <tr class="w3-light-grey">
        <th>ASIN</th>
	<th>Keyword</th>
        <th>Name</th>
	<th>Date</th>
        <th>Price</th>
	<th>Rank</th>
      </tr>
    </thead>
<?php
//create connection
$conn = pg_connect('host=localhost port=5432 dbname=shirts user=power_user password=$poweruser');
if(!$conn)
	die("Error connecting to DB".pg_last_error());

if(isset($_SESSION['searchString'])){
	$sql = "SELECT * FROM shirts_attributes WHERE keyword like '%".$_SESSION['searchString']. "%' ORDER BY rank LIMIT 1000";
	unset($_SESSION['searchString']);
}
else if(isset($_SESSION['keywordSearch'])){
	$sql = "SELECT * FROM shirts_attributes WHERE keyword like '".$_SESSION['keywordSearch']."' ORDER BY rank LIMIT 1000";
	unset($_SESSION['keywordSearch']);
}
else{
	$sql = "SELECT * FROM shirts_attributes ORDER BY rank LIMIT 1000";
}
$results = pg_query($conn, $sql);
if(!$results){
	die("Error in sql query ".pg_last_error());
}
while($row = pg_fetch_array($results)){
	echo"<tr>";
	echo"<td><a target='_blank' rel='noopener noreferer' href=https://amazon.com/dp/" .$row[0]. ">".$row[0]."</a></td>";
	echo"<td> " .$row[5]. "</td>";
	echo"<td> " .$row[1]. "</td>";
        echo"<td> " .$row[4]. "</td>";
	echo"<td> " .$row[2]. "</td>";
	echo"<td> " .$row[3]. "</td>";
	echo"</tr>";
}
pg_close($conn);
?>
  </table>
</div>

</body>

<!-- End page content -->
</div>

<!-- W3.CSS Container -->
<div class="w3-light-grey w3-container w3-padding-32" style="margin-top:75px;padding-right:58px"><p class="w3-right">Powered by <a href="https://www.w3schools.com/w3css/default.asp" title="W3.CSS" target="_blank" class="w3-hover-opacity">w3.css</a></p></div>

<script>
// Script to open and close sidebar
function w3_open() {
  document.getElementById("mySidebar").style.display = "block";
  document.getElementById("myOverlay").style.display = "block";
}
 
function w3_close() {
  document.getElementById("mySidebar").style.display = "none";
  document.getElementById("myOverlay").style.display = "none";
}

// Modal Image Gallery
function onClick(element) {
  document.getElementById("img01").src = element.src;
  document.getElementById("modal01").style.display = "block";
  var captionText = document.getElementById("caption");
  captionText.innerHTML = element.alt;
}
</script>

</body>
</html>
