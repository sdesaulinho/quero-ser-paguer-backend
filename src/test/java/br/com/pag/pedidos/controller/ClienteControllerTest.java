package br.com.pag.pedidos.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
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

import br.com.pag.pedidos.model.ClienteModel;
import br.com.pag.pedidos.repository.ClienteRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClienteControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private ClienteRepository clienteRepository;

	private HttpEntity<Void> userRoleHeader;
	private HttpEntity<Void> adminRoleHeader;
	private HttpEntity<Void> invalidHeader;

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
	public void listarClientes401() {
		ResponseEntity<ClienteModel> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.GET,
				this.invalidHeader, ClienteModel.class);
		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void listarClientes200() {
		List<ClienteModel> clientes = new ArrayList<>();
		clientes.add(new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date()));
		clientes.add(new ClienteModel("2", "025.478.895-40", "Luiz Inácio Lula da Silva", new Date()));
		clientes.add(new ClienteModel("3", "005.478.745-40", "Dilma Vana Russeff", new Date()));

		BDDMockito.when(this.clienteRepository.getAll()).thenReturn(clientes);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.GET,
				this.adminRoleHeader, String.class);
		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
	}

	@Test
	public void getClientePorID200() {
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		BDDMockito.when(this.clienteRepository.getByID("1", "120.356.895-40")).thenReturn(cliente);
		ResponseEntity<ClienteModel> response = this.testRestTemplate.exchange("/v1/cliente/{id}/{cpf}", HttpMethod.GET,
				this.adminRoleHeader, ClienteModel.class,"1","120.356.895-40");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void getClientePorIDNotFound404() {
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		BDDMockito.when(this.clienteRepository.getByID("1", "120.356.895-40")).thenReturn(cliente);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/{id}", HttpMethod.GET,
				this.adminRoleHeader, String.class,"1");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void salvarClienteCPFNull400() {
		ClienteModel cliente = new ClienteModel("1", null, "Saulo Ribeiro Machado", new Date());
		BDDMockito.when(this.clienteRepository.add(cliente)).thenReturn(cliente);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.POST,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O CPF é obrigatório");
	}

	@Test
	public void salvarClienteCPFQuantidadeDeCarateresMaiorQue15Null400() {
		ClienteModel cliente = new ClienteModel("1", "124.859.965-142", "Saulo Ribeiro Machado", new Date());
		BDDMockito.when(this.clienteRepository.add(cliente)).thenReturn(cliente);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.POST,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O campo de CPF deve ter no máximo 14 caracteres");
	}

	@Test
	public void salvarClienteNomeNull400() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40", null, new Date());
		BDDMockito.when(this.clienteRepository.add(cliente)).thenReturn(cliente);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.POST,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O Nome é obrigatório");
	}

	@Test
	public void salvarClienteNomeStringVazia400() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40", "", new Date());
		BDDMockito.when(this.clienteRepository.add(cliente)).thenReturn(cliente);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.POST,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O Nome não pode estar vazio");
	}

	@Test
	public void salvarClienteNomeMaisDe100Caracteres400() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40",
				"Cláudio Raul da Costa Cláudio Raul da Costa Cláudio Raul da Costa Cláudio Raul da Costa Cláudio Raul da Costa",
				new Date());
		BDDMockito.when(this.clienteRepository.add(cliente)).thenReturn(cliente);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.POST,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "O campo de nome deve ter no máximo 100 caracteres");
	}

	@Test
	public void salvarClienteDataNull400() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40", "Cláudio Raul da Costa", null);
		BDDMockito.when(this.clienteRepository.add(cliente)).thenReturn(cliente);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.POST,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("defaultMessage", "A Data de nascimento é obrigatório");
	}

	@Test
	public void salvarCliente200() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40", "Cláudio Raul da Costa", new Date());
		BDDMockito.when(this.clienteRepository.add(cliente)).thenReturn(cliente);
		ResponseEntity<ClienteModel> response = this.testRestTemplate.exchange("/v1/cliente/", HttpMethod.POST,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), ClienteModel.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deleteUserRoleCliente401() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40", "Cláudio Raul da Costa", new Date());
		BDDMockito.doNothing().when(this.clienteRepository).delete(cliente);
		ResponseEntity<ClienteModel> response = this.testRestTemplate.exchange("/v1/cliente/{id}/{cpf}", HttpMethod.DELETE,
				new HttpEntity<ClienteModel>(cliente, this.userRoleHeader.getHeaders()), ClienteModel.class,"1","120.045.859-40");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void deleteAdminRoleCliente200() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40", "Cláudio Raul da Costa", new Date());
		BDDMockito.doNothing().when(this.clienteRepository).delete(cliente);
		ResponseEntity<ClienteModel> response = this.testRestTemplate.exchange("/v1/cliente/{id}/{cpf}", HttpMethod.DELETE,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), ClienteModel.class,"1","120.045.859-40");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void deleteAdminRoleQuandoCLienteNaoExiste404() {
		ClienteModel cliente = new ClienteModel("1", "120.045.859-40", "Cláudio Raul da Costa", new Date());
		BDDMockito.doNothing().when(this.clienteRepository).delete(cliente);
		ResponseEntity<ClienteModel> response = this.testRestTemplate.exchange("/v1/cliente/{id}", HttpMethod.DELETE,
				new HttpEntity<ClienteModel>(cliente, this.adminRoleHeader.getHeaders()), ClienteModel.class,"2");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
	
	@TestConfiguration
	static class configTest {

		@Bean
		public RestTemplateBuilder restTemplateBuild() {
			return new RestTemplateBuilder();
		}
	}
}
