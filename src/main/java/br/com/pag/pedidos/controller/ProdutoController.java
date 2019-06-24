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

import br.com.pag.pedidos.model.ProdutoModel;
import br.com.pag.pedidos.repository.ProdutoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "API responsável pelo gerenciamento dos produtos.")
@RestController
@RequestMapping("/v1/produto/")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@ApiOperation(value = "Realiza o cadastro de um novo produto na base de dados")
	@PostMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<ProdutoModel> save(@Valid @RequestBody ProdutoModel produto) {
		this.produtoRepository.add(produto);
		return ResponseEntity.ok(produto);
	}

	@ApiOperation(value = "Realiza o update das informações do produto na base de dados")
	@PutMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<ProdutoModel> update(@Valid @RequestBody ProdutoModel produto)
			throws ConditionalCheckFailedException {
		this.produtoRepository.set(produto);
		return ResponseEntity.ok(produto);
	}

	@ApiOperation(value = "Realiza a listagem de todos os produtos cadastrados na base de dados.")
	@GetMapping
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public List<ProdutoModel> getAll() {
		return this.produtoRepository.getAll();
	}

	@ApiOperation(value = "Realiza uma consulta na base de dados pesquisando por um produto de acordo com o id")
	@GetMapping("{id}")
	@PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<ProdutoModel> getByID(@PathVariable String id) {
		return ResponseEntity.ok(this.produtoRepository.getByID(id));
	}

	@ApiOperation(value = "Realiza a exclusão dos dados de um determinado produto da base de dados")
	@DeleteMapping("{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<ProdutoModel> delete(@PathVariable String id) {
		this.produtoRepository.delete(new ProdutoModel(id));
		return ResponseEntity.ok().build();
	}

}
