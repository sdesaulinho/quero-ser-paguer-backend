package br.com.pag.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PagpedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagpedidosApplication.class, args);
		/** Esse trecho está comentado, pois caso queira salvar um novo usuário na base de dados
		 * vai precisar salvar a senha criptografada, dessa forma essa linha mostra a senha criptografada
		 * a se salva na base de dados. PS.: Será necesssario fazer o import da class BCryptPasswordEncoder quando descomentar  */
		//System.out.println("Senha: "+ new BCryptPasswordEncoder().encode("root"));
	}

}
