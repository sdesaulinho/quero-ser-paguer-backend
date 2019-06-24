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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;

import br.com.pag.pedidos.dynamodb.util.Filter;
import br.com.pag.pedidos.dynamodb.util.FilterExpression;
import br.com.pag.pedidos.dynamodb.util.Filter.AttributeType;
import br.com.pag.pedidos.dynamodb.util.Filter.Operation;
import br.com.pag.pedidos.model.ClienteModel;
import br.com.pag.pedidos.service.exception.BussinesException;

@Repository
public class ClienteRepository {

	@Autowired
	private DynamoDBMapper mapper;

	/**
	 * Salva no dynamodb os dados de um cliente.
	 * 
	 * @param cliente Dados do cliente que será persistido na base de dados
	 * @throws BussinesException Caso ja exista algum cliente com o mesmo cpf na
	 *                           base de dados.
	 */
	public ClienteModel add(ClienteModel cliente) throws BussinesException {
		ClienteModel clienteModel = getByCPF(cliente.getCpf());
		if (clienteModel == null) {
			this.mapper.save(cliente);
		} else {
			throw new BussinesException(
					"O CPF [ " + cliente.getCpf() + " ] informado já está cadastrado na base de dados");
		}
		return cliente;
	}

	/**
	 * Salva no dynamodb os dados de um cliente.
	 * 
	 * @param cliente Dados do cliente que serão alterados na base de dados
	 * @throws BussinesException Caso o cliente que está sendo alterado não seja
	 *                           encontrado na base de dados
	 */
	public void set(ClienteModel cliente) throws BussinesException {
		try {
			this.mapper.save(cliente, this.buildExpressionCheckIDDynamoDB(cliente));
		} catch (ConditionalCheckFailedException e) {
			throw new BussinesException(
					"Não existe nenhum cliente com o ID [ " + cliente.getId() + " ] cadastrado na base de dados. Não é permitido alterar o CPF do cliente. ");
		}

	}

	/**
	 * Recupera todos os dados da tabela.
	 * 
	 * @return Lista com todos os registros encontrados na tabela de clientes.
	 */
	public List<ClienteModel> getAll() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		List<ClienteModel> iList = mapper.scan(ClienteModel.class, scanExpression);
		return iList;
	}

	/**
	 * Recupera da base de dados do dynamodb um determinado registro de acordo com
	 * seu ID.
	 * 
	 * @param id ID do cliente que será recuperado da base de dados do dynamodb.
	 * @return Dados carregados do dynamodb
	 */
	public ClienteModel getByID(String id, String cpf) {
		return this.mapper.load(ClienteModel.class, id, cpf);
	}

	/**
	 * Recupera da base de dados do dynamodb um determinado registro de acordo com
	 * seu CPF.
	 * 
	 * @param cpf CPF do cliente que será recuperado da base de dados do dynamodb.
	 * @return Dados carregados do dynamodb
	 */
	public ClienteModel getByCPF(String cpf) {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter("cpf", cpf, Operation.EQ, AttributeType.STRING));
		FilterExpression filterExpression = new FilterExpression(filters);
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression(filterExpression.getFilterExpression())
				.withExpressionAttributeNames(filterExpression.getAttributeNames())
				.withExpressionAttributeValues(filterExpression.getAttributeValues());

		List<ClienteModel> iList = mapper.scan(ClienteModel.class, scanExpression);
		if (iList.size() > 0) {
			return iList.get(0);
		}
		return null;
	}

	/**
	 * Realiza a exclusão de um cliente da base de dados.
	 * 
	 * @param cliente Cliente que será removido da base de dados
	 */
	public void delete(ClienteModel cliente) {
		this.mapper.delete(cliente);
	}

	/**
	 * Realiza a construção de uma condição para salvar um registro na base de
	 * dados.
	 * 
	 * @param cliente Dados do cliente que serão usados na comparação dos valores da
	 *                expressão
	 * @return Expressão construida
	 */
	private DynamoDBSaveExpression buildExpressionCheckIDDynamoDB(final ClienteModel cliente) {
		DynamoDBSaveExpression expression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<>();
		expectedAttributes.put("id", new ExpectedAttributeValue(new AttributeValue(cliente.getId()))
				.withComparisonOperator(ComparisonOperator.EQ));
		expression.setExpected(expectedAttributes);
		return expression;
	}

}
