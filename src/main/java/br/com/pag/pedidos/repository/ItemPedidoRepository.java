package br.com.pag.pedidos.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

import br.com.pag.pedidos.model.ItemPedidoModel;


@Repository
public class ItemPedidoRepository {


	@Autowired
	private DynamoDBMapper mapper;

	/**
	 * Salva no dynamodb os dados de um item do pedido.
	 * 
	 * @param itemPedido Item do pedido que será persistido na base de dados
	 */
	public ItemPedidoModel add(ItemPedidoModel itemPedido) {
		this.mapper.save(itemPedido);
		return itemPedido;
	}

	/**
	 * Recupera todos os dados da tabela.
	 * 
	 * @return Lista com todos os registros encontrados na tabela de item do pedido.
	 */
	public List<ItemPedidoModel> getAll() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		PaginatedScanList<ItemPedidoModel> iList = mapper.scan(ItemPedidoModel.class, scanExpression);
		return iList;
	}

	
	/**
	 * Recupera da base de dados do dynamodb um determinado registro de acordo com
	 * seu ID.
	 * 
	 * @param id ID do item do pedido que será recuperado da base de dados do dynamodb.
	 * @return Dados carregados do dynamodb
	 */
	public ItemPedidoModel getByID(String id) {
		ItemPedidoModel itemPedido = this.mapper.load(ItemPedidoModel.class, id);
		return itemPedido;
	}

	/**
	 * Realiza a exclusão de um item do pedido da base de dados.
	 * 
	 * @param itemPedido Pedido que será removido da base de dados.
	 */
	public void delete(ItemPedidoModel itemPedido) {
		this.mapper.delete(itemPedido);
	}
}
