package br.com.pag.pedidos.repository;

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

import br.com.pag.pedidos.model.ProdutoModel;


@Repository
public class ProdutoRepository {

	@Autowired
	private DynamoDBMapper mapper;

	/**
	 * Salva no dynamodb os dados de um produto.
	 * 
	 * @param produto Produto que será persistido na base de dados
	 */
	public ProdutoModel add(ProdutoModel produto) {
		this.mapper.save(produto);
		return produto;
	}

	/**
	 * Salva no dynamodb os dados de um produto.
	 * 
	 * @param produto Produto que terá os dados alterados na base de dados
	 */
	public void set(ProdutoModel produto) throws ConditionalCheckFailedException {
		this.mapper.save(produto, this.buildExpressionDynamoDB(produto));
	}

	/**
	 * Recupera todos os dados da tabela.
	 * 
	 * @return Lista com todos os registros encontrados na tabela de produto.
	 */
	public List<ProdutoModel> getAll() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		PaginatedScanList<ProdutoModel> iList = mapper.scan(ProdutoModel.class, scanExpression);
		return iList;
	}

	
	/**
	 * Recupera da base de dados do dynamodb um determinado registro de acordo com
	 * seu ID.
	 * 
	 * @param id ID do produto que será recuperado da base de dados do dynamodb.
	 * @return Dados carregados do dynamodb
	 */
	public ProdutoModel getByID(String id) {
		return this.mapper.load(ProdutoModel.class, id);
	}

	/**
	 * Realiza a exclusão de um produto da base de dados.
	 * 
	 * @param produto Produto que será removido da base de dados.
	 */
	public void delete(ProdutoModel produto) {
		this.mapper.delete(produto);
	}
	
	/**
	 * Realiza a construção de uma condição para salvar um registro na base de
	 * dados.
	 * 
	 * @param produto Dados do produto que será usado na comparação dos valores da
	 *                expressão
	 * @return Expressão construida
	 */
	private DynamoDBSaveExpression buildExpressionDynamoDB(final ProdutoModel produto) {
		DynamoDBSaveExpression expression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<>();
		expectedAttributes.put("id", new ExpectedAttributeValue(new AttributeValue(produto.getId().toString()))
				.withComparisonOperator(ComparisonOperator.EQ));
		expression.setExpected(expectedAttributes);
		return expression;
	}
}
