<?php
include "dbinfo.php";
$cookie_name = "pidlist";

$conn = mysqli_connect($GLOBALS['servername'], $GLOBALS['username'], $GLOBALS['password'], $GLOBALS['dbname']);

mysqli_set_charset($conn, 'utf8');
	
if (!$conn) {
   	die("Connection failed: " . mysqli_connect_error());
}

if(isset($_COOKIE[$cookie_name])) {
	$cookie_value = json_decode($_COOKIE[$cookie_name]);
    $cookie_array = "'".implode("','", $cookie_value)."'";
    $sql = "SELECT p_id, paragraph FROM parsed_data_v2 WHERE p_id NOT IN ($cookie_array) AND label_count = (SELECT MIN(label_count) FROM parsed_data_v2) LIMIT 5000";
	$result = mysqli_query($conn, $sql);
	
	$total_data = mysqli_num_rows($result);

	if (mysqli_num_rows($result) > 0) {
		$index = rand(1, $total_data);
		$counter = 1;
   		// output data of each row
    	while($row = mysqli_fetch_assoc($result)) {
			if($counter == $index){
				$GLOBALS['current_pid'] = $row["p_id"];
				$GLOBALS['current_paragraph'] = $row["paragraph"];
			}
			$counter++;
    	}
	} else {
		$GLOBALS['current_pid'] = 0;
		$GLOBALS['current_paragraph'] = "Yardımlarınız için teşekkürler. Cookielerinizi temizleyerek etiketlemeye devam edebilirsiniz.";
	}
	mysqli_close($conn);
	array_push($cookie_value, $GLOBALS['current_pid']);
	setcookie($cookie_name, json_encode($cookie_value), time() + (86400 * 75), "/"); // 86400 = 1 day
} else {
    $sql = "SELECT p_id, paragraph FROM parsed_data_v2 WHERE label_count = (SELECT MIN(label_count) FROM parsed_data_v2) LIMIT 50";
	$result = mysqli_query($conn, $sql);
	
	$total_data = mysqli_num_rows($result);

	if (mysqli_num_rows($result) > 0) {
   		$index = rand(1, $total_data);
		$counter = 1;
   		// output data of each row
    	while($row = mysqli_fetch_assoc($result)) {
			if($counter == $index){
				$GLOBALS['current_pid'] = $row["p_id"];
				$GLOBALS['current_paragraph'] = $row["paragraph"];
			}
			$counter++;
    	}
	} else {
		$GLOBALS['current_pid'] = 0;
		$GLOBALS['current_paragraph'] = "";
	}
	mysqli_close($conn);
	$cookie_value = array();
	array_push($cookie_value, $GLOBALS['current_pid']);
	setcookie($cookie_name, json_encode($cookie_value), time() + (86400 * 75), "/"); // 86400 = 1 day
	}
?>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hüzün Projesi</title>
    <!-- Bootstrap -->
    <link href="css/bootstrap-4.0.0.css" rel="stylesheet">
	<link href="css/all.css" rel="stylesheet">
	<link href="css/bootstrap-social.css" rel="stylesheet">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style type="text/css">
.fa {
	margin: 5px;
    padding: 0px;
    font-size: 50px;
    width: 50px;
    text-align: center;
    text-decoration: none;
	}

/* Add a hover effect if you want */
.fa:hover {
    opacity: 0.7;
}

/* Set a specific color for each brand */

/* Facebook */
.fa-facebook {
    background: #3B5998;
    color: white;
}

/* Twitter */
.fa-twitter {
    background: #55ACEE;
    color: white;
}
.fa-whatsapp{
    background: #25D366;
    color: white;
}
.fa-telegram {
    background: white;
    color: #0088cc;
}
.fa-linkedin {
    background: #0077B5;
    color: white;
}
.fa-reddit {
    background: white ;
    color: #ff4301;
}
    body {
	background-color: #F8ECC2;
}
    </style>
</head>
  <body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <a class="navbar-brand" href="#">Hüzün</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item active">
            <a class="nav-link" href="#mainText">Katkıda Bulun<span class="sr-only">(current)</span></a>
          </li>
		<li class="nav-item active">
            <a class="nav-link" href="/test.php">Sınıflandır<span class="sr-only">(current)</span></a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#share">Paylaş</a>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            Proje Detayları
            </a>
            <div class="dropdown-menu" aria-labelledby="navbarDropdown">
              <a class="dropdown-item" href="#mission">Amacımız</a>
              <a class="dropdown-item" href="#communication">Biz kimiz?</a>
            </div>
          </li>
        </ul>
        <form class="form-inline my-2 my-lg-0">
        </form>
      </div>
    </nav>
    <section>
      <div class="jumbotron jumbotron-fluid text-center mt-2 bg-warning text-danger">
        <div class="container">
          <div class="row">
            <div class="col-12">
              <h1><em>Hüzün Projesi</em></h1>
              <p><a class="btn btn-primary btn-lg" href="#mission" role="button">Daha Fazla »</a></p>
            </div>
          </div>
        </div>
      </div>
    </section>
	<section>
      <div class="text-center mt-2">
        <div class="container">
          <div class="row">
            <div class="col-12">
              <h3>Etiketleme ile ilgili bilmeniz gerekenler:</h3>
              <ul class="text-justify">
                <li>İnternet sayfamızı, Hüzün, Melankoli ve Üzüntü duygularının hem kendileri hem de diğer temel duygular (Mutluluk, Öfke, Korku, Şaşırma, Tiksinme) arasındaki farklılıkları anlayabilmek amacıyla kurduk.</li>
				<li>Sizden Hüzün, Melankoli ve Üzüntü arasından en fazla bir seçenek olmak üzere okuduğunuz metnin sizde yarattığı duyguları işaretlemenizi rica ediyoruz.</li>
				<li>Okuduğunuz metin, seçtiğimiz duygular arasından hiçbirini çağrıştırmıyorsa, Diğer seçeceğini işaretleyebilirsiniz.</li>
				<li>İşaretlerken kendinizi rahat hissedin, herhangi bir doğru ya da yanlış cevap yok!</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>
    <section>
      <div class="container" id="nextPar">
	    <div class="row text-center">
            <div class="col-12">
              <h4>Okuduğunuz paragraf hangi duyguyu uyandırıyor?</h4>
            </div>
        </div>
		<form action="/index.php#nextPar" method="post">
        <div class="row">
          <div class="col-md-5 col-12 text-center">
			  <br>
			  <input type="hidden" name="p_id" id="p_id" value="<?php echo $GLOBALS['current_pid']?>">
			<div id="mainText"><em><?php echo $GLOBALS['current_paragraph']?></em></div>
			  <p></p>
		  </div>            
		<div class="col-md-5 col-12 text-center">
		  <br>
		  <label class="m-2">
				    <input type="checkbox" class="huzun" name="labeling[]" value="huzun" id="Label_0">
			    Hüzün</label>
		  <label class="m-2">
				    <input type="checkbox" class="huzun" name="labeling[]" value="melankoli" id="Label_1">
			    Melankoli</label>
		  <label class="m-2">
				    <input type="checkbox" class="huzun" name="labeling[]" value="uzuntu" id="Label_2">
				    Üzüntü</label>
				  <br>
		  <label class="m-2">
				    <input type="checkbox" name="labeling[]" value="mutluluk" id="Label_3">
	      Mutluluk</label>
		  <label class="m-2">
				    <input type="checkbox" name="labeling[]" value="ofke" id="Label_4">
				    Öfke</label>
		  <label class="m-2">
				    <input type="checkbox" name="labeling[]" value="korku" id="Label_5">
				    Korku</label>
				  <br>
		  <label class="m-2">
				    <input type="checkbox" name="labeling[]" value="sasirma" id="Label_6">
				    Şaşırma</label>
		  <label class="m-2">
				    <input type="checkbox" name="labeling[]" value="tiksinme" id="Label_7">
				    Tiksinme</label>
		  <label class="m-2">
				    <input type="checkbox" class="diger" name="labeling[]" value="diger" id="Label_8">
				    Diğer</label>
				  <br>
				<br>
		  <input type="submit" name="label" value="Sonraki Metin"><p></p>
		  </div>
			<div class="col-md-2 col-12 text-center" id="share">
			  <h5>Paylaşarak Bize Yardım Edin</h5>
				<a href="http://www.facebook.com/sharer.php?u=https://huzun.com" class="fa fa-facebook"></a>
				<a href="https://twitter.com/share?url=https://huzun.com&amp;" class="fa fa-twitter"></a>
				<a href="https://wa.me/?text=https://huzun.com" class="fa fa-whatsapp"></a>
				<a href="https://t.me/share/url?url=https://huzun.com" class="fa fa-telegram"></a>
				<a href="http://www.linkedin.com/shareArticle?mini=true&amp;url=https://huzun.com" class="fa fa-linkedin"></a>
				<a href="#" class="fa fa-clipboard"></a>
		  </div>
        </div>
		</form>
		  <div class="row text-center mt-2">
            <div class="col-12">
              <h5>Detaylı Seçenek Açıklaması:</h5>
              <ul class="list-unstyled">
                <li>Hüzün, Melankoli ve Üzüntü duygularından 0 ya da 1 seçim.</li>
				<li>Mutluluk, Öfke, Korku, Şaşırma ve Tiksinme duygularından hiç seçmemek de dahil istediğiniz kadar.</li>
				<li>Sadece Diğer. Bu seçeceğe tıkladığınızda, önceki seçtikleriniz kaldırılır.</li>
              </ul>
            </div>
          </div>
		  <?php
if($_SERVER['REQUEST_METHOD'] == "POST" and isset($_POST['label']))
{
     label();
}
function label(){
$conn = mysqli_connect($GLOBALS['servername'], $GLOBALS['username'], $GLOBALS['password'], $GLOBALS['dbname']);
	
mysqli_set_charset($conn, 'utf8');
	
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$pid = $_POST["p_id"];
$optionsSQL = "";
$label_counter = 0;

foreach($_POST["labeling"] as $option) {
  $optionsSQL .= ", $option = $option + 1";
  $label_counter++;
}
if($label_counter != 0){
	$sql = "UPDATE parsed_data_v2 SET label_count = label_count + $label_counter".$optionsSQL." WHERE p_id = $pid";
	$result = mysqli_query($conn, $sql);
	
	if(!$result){
		die("Query failed: " . mysqli_error());
	}
}

mysqli_close($conn);
}
?>
      </div>
    </section>
<hr>
    <section>
      <div class="container">
        <div class="row">
          <div class="col-12 text-center" id="mission">
            <h1>Amacımız</h1>
            <p>Projenin birinci amacı, verilen herhangi bir metnin hüzünlü olup olmadığını tespit edebilecek bir program oluşturmaktır. Buna ek olarak, hüzün, melankoli ve üzüntü konseptleri arasında insan algısında ne tip farklılıklar olduğunu anlamak ve bu konseptlerin diğer duygularla ne kadar ilişkili olduğunu anlamak hedeflerimiz arasında. Projenin merkezinde metin sınıflandırması için kullanılan makine öğrenimi teknikleri bulunuyor. Bu sebeple, bilgisayar mühendisliği son sınıf dizayn projesi olarak yaptığımız bu projede ihtiyacımız olan datayı oluşturmak için sizin yardımınıza ihtiyacımız var. Günde sadece 5 dakikanızı ayırarak bize katkıda bulunabilirsiniz.</p>
            <a href="#mainText"><button type="button" class="btn btn-success">Katkıda Bulun</button></a>
          </div>
        </div>
      </div>
    </section>
    <hr>
    <div class="section">
      <div class="container">
        <div class="row">
          <div class="col-md-12 col-12 text-center" id="communication">
            <h3 class="text-center">Bize Ulaşın</h3>
			  <p>Koç Üniversitesi son sınıf öğrencileri olarak yer aldığımız bu projeyle ilgili bilgi almak için bize ulaşın!</p>
              <abbr title="Phone">İsmail Göktuğ Mutlu:</abbr> <a href="https://www.linkedin.com/in/ismail-g%C3%B6ktu%C4%9F-mutlu-272433167/"> LinkedIn</a><br>
			  <abbr title="Phone">Şafak Tüfekçi:</abbr>  <a href="https://www.linkedin.com/in/safaktufekci/"> LinkedIn</a><br>
			  <abbr title="Phone">Emre Akgün:</abbr> <a href="https://www.linkedin.com/in/emre-akgun-97720115b/"> LinkedIn</a>
          </div>
        </div>
      </div>
    </div>
    <hr>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
  <script src="js/jquery-3.2.1.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
  <script src="js/popper.min.js"></script>
  <script src="js/bootstrap-4.0.0.js"></script>
  <script type="text/javascript"> 
		function myFunction() {
  /* Get the text field */
  var copyText = document.getElementById("mainText");

  /* Select the text field */
  copyText.select();

  /* Copy the text inside the text field */
  document.execCommand("copy");
}</script>
<script>
$('.huzun').on('change', function() {
    $('.huzun').not(this).prop('checked', false);
});
$('.diger').on('change', function() {
    $('input[name="' + this.name + '"]').not(this).prop('checked', false);
});
$('input[type="checkbox"]').on('change', function() {
    $('.diger').not(this).prop('checked', false);
});</script>
  </body>
</html>