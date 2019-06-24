package br.com.pag.pedidos.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;

import br.com.pag.pedidos.dynamodb.util.Filter;
import br.com.pag.pedidos.dynamodb.util.Filter.AttributeType;
import br.com.pag.pedidos.dynamodb.util.Filter.Operation;
import br.com.pag.pedidos.dynamodb.util.FilterExpression;
import br.com.pag.pedidos.model.PedidoModel;

@Repository
public class PedidoRepository {

	@Autowired
	private DynamoDBMapper mapper;

	/**
	 * Salva no dynamodb os dados de um pedido.
	 * 
	 * @param pedido Pedido que será persistido na base de dados
	 */
	public PedidoModel add(PedidoModel pedido) {
		this.mapper.save(pedido);
		return pedido;
	}

	/**
	 * Salva no dynamodb os dados de um pedido.
	 * 
	 * @param pedido Pedido que terá os dados alterados na base de dados
	 */
	public void set(PedidoModel pedido) throws ConditionalCheckFailedException {
		this.mapper.save(pedido, this.buildExpressionDynamoDB(pedido));
	}

	/**
	 * Recupera todos os dados da tabela.
	 * 
	 * @return Lista com todos os registros encontrados na tabela de pedido.
	 */
	public List<PedidoModel> getAll() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		PaginatedScanList<PedidoModel> iList = mapper.scan(PedidoModel.class, scanExpression);
		return iList;
	}

	/**
	 * Recupera todos os pedidos de um determinado cliente.
	 * 
	 * @param cpf CPF do cliente que serão carregados os pedido
	 * @return Lista com todos os registros encontrados na tabela de pedido
	 *         associados ao CPF informado.
	 */
	public List<PedidoModel> getByCPF(String cpf) {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter("cliente.cpf",cpf,Operation.EQ,AttributeType.STRING));
		FilterExpression filterExpression = new FilterExpression(filters);
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression(filterExpression.getFilterExpression())
				.withExpressionAttributeNames(filterExpression.getAttributeNames())
				.withExpressionAttributeValues(filterExpression.getAttributeValues());

		PaginatedScanList<PedidoModel> iList = mapper.scan(PedidoModel.class, scanExpression);
		return iList;
	}

	/**
	 * Recupera da base de dados do dynamodb um determinado registro de acordo com
	 * seu ID.
	 * 
	 * @param id ID do pedido que será recuperado da base de dados do dynamodb.
	 * @return Dados carregados do dynamodb
	 */
	public PedidoModel getByID(String id) {
		PedidoModel pedido = this.mapper.load(PedidoModel.class, id);
		return pedido;
	}

	/**
	 * Realiza a exclusão de um pedido da base de dados.
	 * 
	 * @param pedido Pedido que será removido da base de dados.
	 */
	public void delete(PedidoModel pedido) {
		this.mapper.delete(pedido);
	}

	/**
	 * Realiza a construção de uma condição para salvar um registro na base de
	 * dados.
	 * 
	 * @param pedido Dados do pedido que será usado na comparação dos valores da
	 *               expressão
	 * @return Expressão construida
	 */
	private DynamoDBSaveExpression buildExpressionDynamoDB(final PedidoModel pedido) {
		DynamoDBSaveExpression expression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<>();
		expectedAttributes.put("id", new ExpectedAttributeValue(new AttributeValue(pedido.getId().toString()))
				.withComparisonOperator(ComparisonOperator.EQ));
		expression.setExpected(expectedAttributes);
		return expression;
	}
}
