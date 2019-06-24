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

import br.com.pag.pedidos.model.ClienteModel;
import br.com.pag.pedidos.repository.ClienteRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "API responsável pelo gerenciamento dos clientes.")
@RestController
@RequestMapping("/v1/cliente/")
public class ClienteController {

	@Autowired
	private ClienteRepository repository;

	@ApiOperation(value = "Realiza o cadastro de um novo cliente na base de dados")
	@PostMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<ClienteModel> save(@Valid @RequestBody ClienteModel cliente) {
		this.repository.add(cliente);
		return ResponseEntity.ok(cliente);
	}

	@ApiOperation(value = "Realiza o update das informações do cliente na base de dados")
	@PutMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<ClienteModel> update(@Valid @RequestBody ClienteModel cliente) {
		this.repository.set(cliente);
		return ResponseEntity.ok(cliente);
	}

	@ApiOperation(value = "Realiza a listagem de todos os clientes cadastrados na base de dados.")
	@GetMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public List<ClienteModel> getAll() {
		return this.repository.getAll();
	}

	@ApiOperation(value = "Realiza uma consulta na base de dados pesquisando por um cliente de acordo com a chave (id,cpf)")
	@GetMapping("{id}/{cpf}")
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<ClienteModel> getByID(@PathVariable String id, @PathVariable String cpf) {
		return ResponseEntity.ok(this.repository.getByID(id, cpf));
	}

	@ApiOperation(value = "Realiza uma consulta na base de dados pesquisando por um cliente de acordo com seu CPF.")
	@GetMapping("byCPF/{cpf}")
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<ClienteModel> getByCPF(@PathVariable String cpf) {
		return ResponseEntity.ok(this.repository.getByCPF(cpf));
	}

	
	@ApiOperation(value = "Realiza a exclusão dos dados de um determinado paciente da base de dados")
	@DeleteMapping("{id}/{cpf}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<ClienteModel> delete(@PathVariable String id, @PathVariable String cpf) {
		this.repository.delete(new ClienteModel(id, cpf));
		return ResponseEntity.ok().build();
	}

}
