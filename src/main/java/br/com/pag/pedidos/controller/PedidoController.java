package br.com.pag.pedidos.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

import br.com.pag.pedidos.model.PedidoModel;
import br.com.pag.pedidos.repository.PedidoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "API responsável pelo gerenciamento dos pedidos.")
@RestController
@RequestMapping("/v1/pedido/")
public class PedidoController {

	@Autowired
	private PedidoRepository repository;

	@ApiOperation(value = "Realiza o cadastro de um novo pedido na base de dados")
	@PostMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<PedidoModel> save(@Valid @RequestBody PedidoModel pedido) {
		this.repository.add(pedido);
		return ResponseEntity.ok(pedido);
	}

	@ApiOperation(value = "Realiza o update das informações do pedido na base de dados")
	@PutMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<PedidoModel> update(@Valid @RequestBody PedidoModel pedido)
			throws ConditionalCheckFailedException {
		this.repository.set(pedido);
		return ResponseEntity.ok(pedido);
	}

	@ApiOperation(value = "Realiza a listagem de todos os pedidos cadastrados na base de dados.")
	@GetMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public List<PedidoModel> getAll() {
		return this.repository.getAll();
	}

	@ApiOperation(value = "Realiza uma consulta na base de dados pesquisando por um pedido de acordo com o id")
	@GetMapping("{id}")
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<PedidoModel> getByID(@PathVariable String id) {
		return ResponseEntity.ok(this.repository.getByID(id));
	}

	@ApiOperation(value = "Realiza uma consulta na base de dados pesquisando por todos os pedido de um determinado cliente.")
	@GetMapping("byCPF/{cpf}")
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public List<PedidoModel> getByCPF(@PathVariable String cpf) {
		return this.repository.getByCPF(cpf);
	}

	
	@ApiOperation(value = "Realiza a exclusão dos dados de um determinado pedido da base de dados")
	@DeleteMapping("{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<PedidoModel> delete(@PathVariable String id) {
		this.repository.delete(new PedidoModel(id));
		return ResponseEntity.ok().build();
	}

}
