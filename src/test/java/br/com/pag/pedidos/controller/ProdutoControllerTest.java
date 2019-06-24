package br.com.pag.pedidos.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import br.com.pag.pedidos.model.ProdutoModel;
import br.com.pag.pedidos.repository.ProdutoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProdutoControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private ProdutoRepository produtoRepository;

	private HttpEntity<Void> userRoleHeader;
	private HttpEntity<Void> adminRoleHeader;
	private HttpEntity<Void> invalidHeader;

	@TestConfiguration
	static class configTest {

		@Bean
		public RestTemplateBuilder restTemplateBuild() {
			return new RestTemplateBuilder();
		}
	}
	
	@Before
	public void configUserRoleHeader() {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set("username", "saulo");
		request.set("password", "saulo");
		request.set("grant_type", "password");
		@SuppressWarnings("unchecked")
		Map<String, Object> token = this.testRestTemplate.withBasicAuth("apppagpedidos", "teste")
				.postForObject("/oauth/token", request, Map.class);
		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", "Bearer " + token.get("access_token"));
		this.userRoleHeader = new HttpEntity<>(header);
	}

	@Before
	public void configAdminRoleHeader() {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set("username", "root");
		request.set("password", "root");
		request.set("grant_type", "password");
		@SuppressWarnings("unchecked")
		Map<String, Object> token = this.testRestTemplate.withBasicAuth("apppagpedidos", "teste")
				.postForObject("/oauth/token", request, Map.class);
		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", "Bearer " + token.get("access_token"));
		this.adminRoleHeader = new HttpEntity<>(header);
	}

	@Before
	public void configInvalidHeader() {
		HttpHeaders header = new HttpHeaders();
		header.add("", "Bearer xpto");
		this.invalidHeader = new HttpEntity<>(header);
	}

	@Test
	public void listarProdutosComStatusCode401() {
		ResponseEntity<ProdutoModel> response = this.testRestTemplate.exchange("/v1/produto/", HttpMethod.GET,
				this.invalidHeader, ProdutoModel.class);
		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void listarProdutosComStatusCode200() {
		List<ProdutoModel> produtos = new ArrayList<>();
		produtos.add(new ProdutoModel("1", "Bala", new BigDecimal(0.5)));
		produtos.add(new ProdutoModel("2", "Chiclete", new BigDecimal(1500)));
		produtos.add(new ProdutoModel("3", "Carro", new BigDecimal(2000)));

		BDDMockito.when(this.produtoRepository.getAll()).thenReturn(produtos);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/produto/", HttpMethod.GET,
				this.adminRoleHeader, String.class);
		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
	}

	@Test
	public void getProdutoPorIDComStatusCode200() {
		ProdutoModel produto = new ProdutoModel("1", "Bala", new BigDecimal(0.5));
		BDDMockito.when(this.produtoRepository.getByID("1")).thenReturn(produto);
		ResponseEntity<ProdutoModel> response = this.testRestTemplate.exchange("/v1/produto/{id}", HttpMethod.GET,
				this.adminRoleHeader, ProdutoModel.class,"1");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void salvarProdutoComStatusCode200() {
		ProdutoModel produto = new ProdutoModel("1", "Bala", new BigDecimal(0.5));
		BDDMockito.when(this.produtoRepository.add(produto)).thenReturn(produto);
		ResponseEntity<ProdutoModel> response = this.testRestTemplate.exchange("/v1/produto/", HttpMethod.POST,
				new HttpEntity<ProdutoModel>(produto, this.adminRoleHeader.getHeaders()), ProdutoModel.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void salvarProdutoNomeNull400() {
		ProdutoModel produto = new ProdutoModel("1", null, new BigDecimal(0.5));
		BDDMockito.when(this.produtoRepository.add(produto)).thenReturn(produto);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/produto/", HttpMethod.POST,
				new HttpEntity<ProdutoModel>(produto, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O Nome é obrigatório");
	}

	@Test
	public void salvarProdutoNomeStringVazia400() {
		ProdutoModel produto = new ProdutoModel("1", "", new BigDecimal(0.5));
		BDDMockito.when(this.produtoRepository.add(produto)).thenReturn(produto);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/produto/", HttpMethod.POST,
				new HttpEntity<ProdutoModel>(produto, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O Nome não pode estar vazio");
	}

	@Test
	public void salvarClienteNomeMaisDe100Caracteres400() {
		ProdutoModel produto = new ProdutoModel("1", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", new BigDecimal(0.5));
		BDDMockito.when(this.produtoRepository.add(produto)).thenReturn(produto);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/produto/", HttpMethod.POST,
				new HttpEntity<ProdutoModel>(produto, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O campo de nome deve ter no máximo 100 caracteres");
	}
	
	@Test
	public void deleteUserRoleProdutoComStatusCode401() {
		ProdutoModel produto = new ProdutoModel("1", "Bala", new BigDecimal(0.5));
		BDDMockito.doNothing().when(this.produtoRepository).delete(produto);
		ResponseEntity<ProdutoModel> response = this.testRestTemplate.exchange("/v1/produto/{id}", HttpMethod.DELETE,
				new HttpEntity<ProdutoModel>(produto, this.userRoleHeader.getHeaders()), ProdutoModel.class,"1");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void deleteAdminRoleProdutoComStatusCode200() {
		ProdutoModel produto = new ProdutoModel("1", "Bala", new BigDecimal(0.5));
		BDDMockito.doNothing().when(this.produtoRepository).delete(produto);
		ResponseEntity<ProdutoModel> response = this.testRestTemplate.exchange("/v1/produto/{id}", HttpMethod.DELETE,
				new HttpEntity<ProdutoModel>(produto, this.adminRoleHeader.getHeaders()), ProdutoModel.class,"1");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void deleteAdminRoleQuandoProdutoNaoExiste404() {
		ProdutoModel produto = new ProdutoModel("1", "Bala", new BigDecimal(0.5));
		BDDMockito.doNothing().when(this.produtoRepository).delete(produto);
		ResponseEntity<ProdutoModel> response = this.testRestTemplate.exchange("/v1/produto", HttpMethod.DELETE,
				new HttpEntity<ProdutoModel>(produto, this.adminRoleHeader.getHeaders()), ProdutoModel.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
}
