<?php
/**
 * Created by PhpStorm.
 * User: manike
 * Date: 04/10/18
 * Time: 09:47
 */
    //error_reporting(0);
    //ini_set('display_errors', 0 );
    use \Psr\Http\Message\ServerRequestInterface as Request;
    use \Psr\Http\Message\ResponseInterface as Response;

    require 'vendor/autoload.php';


    $app = new \Slim\App;
    //header('Content-type: application/json');
    /////////////////////////////////////////////////////////////////////////////////
///

    $app->get('/prod', function (Request $request, Response $response, array $args) {

        $response->getBody()->write("Hello");

        return $response;
    });
    $app->post('/imc/{imc}',function (Request $request, Response $response, array $args){
        $sql = "INSERT INTO imc(peso,altura,data,idpaciente) VALUES(:peso,:altura,:data,:idpaciente);";
        $imc = json_decode($request->getBody());
        $conn = new PDO("mysql:dbname=pablofica;host=localhost","root","");
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(":peso",$imc->peso);
        $stmt->bindParam(":altura",$imc->altura);
        $stmt->bindParam(":data",$imc->data);
        $stmt->bindParam(":idpaciente",$imc->idpaciente);
        $stmt->execute();

       return echo json_encode($conn->lastInsertId());
        
    });


    $app->run();






?>
