package br.com.pag.pedidos.model;

import java.math.BigDecimal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

/**
 * Classe responsável pelo mapeamento da tabela pedido do dynamodb para um
 * objeto de dominio java.
 * 
 * @author Saulo Machado
 *
 */
@DynamoDBDocument
public class ItemPedidoModel {

	private ProdutoModel produto;
	private BigDecimal valor;


	public ItemPedidoModel() {
		super();
	}

	public ItemPedidoModel(ProdutoModel produto, BigDecimal valor) {
		super();
		this.produto = produto;
		this.valor = valor;
	}


	@DynamoDBAttribute
	public ProdutoModel getProduto() {
		return produto;
	}

	public void setProduto(ProdutoModel produto) {
		this.produto = produto;
	}

	@DynamoDBAttribute
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

}
