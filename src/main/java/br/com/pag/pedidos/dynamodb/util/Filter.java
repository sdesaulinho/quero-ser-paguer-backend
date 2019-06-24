package br.com.pag.pedidos.dynamodb.util;

public class Filter {

	private String attributeName;

	private String attributeValue;

	private Operation operation;

	private AttributeType attributeType;

	public Filter() {
		super();
	}

	public Filter(String attributeName, String attributeValue, Operation operation, AttributeType attributeType) {
		super();
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.operation = operation;
		this.attributeType = attributeType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	public enum Operation {
		EQ, GE, LE, GT, LT, CONTAINS, STARTS_WITH
	}

	public enum AttributeType {
		STRING, NUMBER
	}
}