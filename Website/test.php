<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hüzün Projesi Sınıflandırma</title>
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
              <a class="dropdown-item" href="index.php#mission">Amacımız</a>
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
              <h3>Sınıflandırma ile ilgili bilmeniz gerekenler:</h3>
              <ul class="text-justify">
				  <p></p>
                Kullandığımız iki farklı Machine Learning modeli (FastText, LSTM), girdiğiniz metni içerdiği duyguya göre temel duygular arasından (Hüzün, Mutluluk, Öfke, Korku, Şaşırma, Tiksinme) sınıflandıracak.
              </ul>
				<p></p>
            </div>
          </div>
        </div>
      </div>
    </section>
    <section>
      <div class="container" id="nextPar">
	    <div class="row text-center">
            <div class="col-10">
              <h4>Denemek istediğiniz metni aşağıdaki kutucuğa yazın.</h4>
            </div>
        </div>
		<form action="/test.php#nextPar" method="post" id="testform">
        <div class="row">
          <div class="col-md-10 col-12 text-center">
			  <br>
			  <textarea form="testform" placeholder="Metni giriniz." required name="test_text" rows="5" cols="75" minlength="10" id="test_text"></textarea>
			  <p></p>
		  <input type="submit" name="classify" value="Sınıflandır"><p></p>
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
      </div>
    </section>
	  <hr>
	 <section>
	  <div class="row">
          <div class="col-md-6 col-12 text-center">
			  <h3 class="text-center">LSTM Tahmini</h3>
			  <br><i><b>
			  <?php
if($_SERVER['REQUEST_METHOD'] == "POST" and isset($_POST['classify']))
{
	$testtext = htmlspecialchars($_POST['test_text']);
    $output=exec("python3 lstm-loader.py -i \"$testtext\"");
	echo $output;
}
?></b></i>
			  <p></p>
		  </div>  
			<div class="col-md-6 col-12 text-center" id="share">
				<h3 class="text-center">FastText Tahmini</h3>
				<br><i><b>
			  	 <?php
if($_SERVER['REQUEST_METHOD'] == "POST" and isset($_POST['classify']))
{
    $testtext = htmlspecialchars($_POST['test_text']);
    $output2=exec("java -jar huzunProject.jar -s \"$testtext\"");
	echo $output2;

}
?></b></i>
			  <p></p>
			</div>
        </div></section>
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