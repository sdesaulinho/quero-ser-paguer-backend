package br.com.pag.pedidos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * Classe responsável por realizar o mapeamento das configurações descritas no
 * arquivos application.yml do spring.
 * 
 * @author Saulo Machado
 *
 */
@Configuration
public class DynamoBDConfig {

	/** Chave de acesso do usuário criado na aws para manipulação do dynamobd */
	@Value("${amazon.access.key}")
	private String awsChaveAcesso;
	/** Senha de acesso do usuário criado na aws para manipulação do dynamobd */
	@Value("${amazon.access.password}")
	private String awsSenhaAcesso;
	/** Região onde a conta está registrada */
	@Value("${amazon.region}")
	private String awsRegiao;
	/** URL para manipular o dnymanodb */
	@Value("${amazon.end-point.url}")
	private String dynamoDBURLEndPoint;

	/**
	 * Metodo responsável pela criação de um mapper entre os objetos de dominio e o
	 * dynamodb. Com esse é objeto é possivel realizar a manipulação dos dados
	 * de uma tabela do dynamodb, como por exemplo: criar,editar,excluir e listar os dodos.
	 * Porém não é permitido realizar criação ou exclusão da tabela.
	 * 
	 * @return Objeto com o métodos de manipulação do dynamodb.
	 */
	@Bean
	public DynamoDBMapper mapper() {
		return new DynamoDBMapper(this.amazonDynamoDBConfig());
	}

	/**
	 * Mátodo responsável pela criação do objeto que será usado para criação da
	 * conexão com o dynamodb.
	 * 
	 * @return Objeto com as configrações para acesso ao dinamodb.
	 */
	private AmazonDynamoDB amazonDynamoDBConfig() {
		return AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration(this.dynamoDBURLEndPoint, this.awsRegiao))
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(this.awsChaveAcesso, this.awsSenhaAcesso)))
				.build();
	}
}
