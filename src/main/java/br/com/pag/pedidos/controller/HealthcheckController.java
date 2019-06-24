package br.com.pag.pedidos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "API responsável pelo Healthcheck feito pelo amazon ECS .")
@RestController
@RequestMapping("/healthcheck")
public class HealthcheckController {

	@ApiOperation(value = "Metodo invocado pelo amazon ECS para saber se o serviço está ativo no cluster.")
	@GetMapping
	public ResponseEntity<String> healthcheck() {
		return ResponseEntity.ok().build();
	}

}
