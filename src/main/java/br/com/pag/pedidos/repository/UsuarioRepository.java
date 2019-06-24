package br.com.pag.pedidos.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.pag.pedidos.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, String> {

	/**
	 * Carrega os dados do usuário de acordo com seu login.
	 * 
	 * @param login Login de acesso ao sistema
	 * @return Dados do usuário associado ao login
	 */
	Usuario findByLogin(String login);
}
