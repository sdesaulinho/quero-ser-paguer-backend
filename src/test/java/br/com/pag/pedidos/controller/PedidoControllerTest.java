package br.com.pag.pedidos.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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
import br.com.pag.pedidos.model.PedidoModel;
import br.com.pag.pedidos.repository.PedidoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PedidoControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private PedidoRepository pedidoRepository;

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
	public void listarPedidosComStatusCode401() {
		ResponseEntity<PedidoModel> response = this.testRestTemplate.exchange("/v1/pedido/", HttpMethod.GET,
				this.invalidHeader, PedidoModel.class);
		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void listarPedidosComStatusCode200() {
		List<PedidoModel> pedidos = new ArrayList<>();
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		pedidos.add(new PedidoModel("1", cliente, new BigDecimal(1000)));
		pedidos.add(new PedidoModel("2", cliente, new BigDecimal(1500)));
		pedidos.add(new PedidoModel("3", cliente, new BigDecimal(2000)));

		BDDMockito.when(this.pedidoRepository.getAll()).thenReturn(pedidos);
		ResponseEntity<String> response = this.testRestTemplate.exchange("/v1/pedido/", HttpMethod.GET,
				this.adminRoleHeader, String.class);
		assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
	}

	@Test
	public void getPedidoPorIDComStatusCode200() {
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		PedidoModel pedido = new PedidoModel("1", cliente, new BigDecimal(1000));
		BDDMockito.when(this.pedidoRepository.getByID("1")).thenReturn(pedido);
		ResponseEntity<ClienteModel> response = this.testRestTemplate.exchange("/v1/pedido/{id}", HttpMethod.GET,
				this.adminRoleHeader, ClienteModel.class,"1");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void salvarPedidoComStatusCode200() {
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		PedidoModel pedido = new PedidoModel("1", cliente, new BigDecimal(1000));
		BDDMockito.when(this.pedidoRepository.add(pedido)).thenReturn(pedido);
		ResponseEntity<PedidoModel> response = this.testRestTemplate.exchange("/v1/pedido/", HttpMethod.POST,
				new HttpEntity<PedidoModel>(pedido, this.adminRoleHeader.getHeaders()), PedidoModel.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deleteUserRolePedidoComStatusCode401() {
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		PedidoModel pedido = new PedidoModel("1", cliente, new BigDecimal(1000));
		BDDMockito.doNothing().when(this.pedidoRepository).delete(pedido);
		ResponseEntity<PedidoModel> response = this.testRestTemplate.exchange("/v1/pedido/{id}", HttpMethod.DELETE,
				new HttpEntity<PedidoModel>(pedido, this.userRoleHeader.getHeaders()), PedidoModel.class,"1");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void deleteAdminRolePedicoComStatusCode200() {
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		PedidoModel pedido = new PedidoModel("1", cliente, new BigDecimal(1000));
		BDDMockito.doNothing().when(this.pedidoRepository).delete(pedido);
		ResponseEntity<PedidoModel> response = this.testRestTemplate.exchange("/v1/pedido/{id}", HttpMethod.DELETE,
				new HttpEntity<PedidoModel>(pedido, this.adminRoleHeader.getHeaders()), PedidoModel.class,"1");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void deleteAdminRoleQuandoPedidoNaoExiste404() {
		ClienteModel cliente = new ClienteModel("1", "120.356.895-40", "Jair Messias Bolsonaro", new Date());
		PedidoModel pedido = new PedidoModel("1", cliente, new BigDecimal(1000));
		BDDMockito.doNothing().when(this.pedidoRepository).delete(pedido);
		ResponseEntity<PedidoModel> response = this.testRestTemplate.exchange("/v1/pedido", HttpMethod.DELETE,
				new HttpEntity<PedidoModel>(pedido, this.adminRoleHeader.getHeaders()), PedidoModel.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
}
