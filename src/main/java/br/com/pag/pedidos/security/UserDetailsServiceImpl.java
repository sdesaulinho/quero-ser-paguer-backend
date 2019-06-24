package br.com.pag.pedidos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import br.com.pag.pedidos.model.Usuario;
import br.com.pag.pedidos.repository.UsuarioRepository;

@Repository
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UsuarioRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = this.repository.findByLogin(username);

		if (usuario == null) {
			throw new UsernameNotFoundException(
					"O usuário com o login [ " + username + " ] não foi encontrado na base de dados.");
		}
		return new User(usuario.getLogin(), usuario.getPassword(),usuario.getAuthorities());
	}

}
