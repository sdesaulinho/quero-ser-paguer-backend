 # Define a imagem base
 FROM openjdk:8u212-jre-stretch

 # Define  quem está criando e vai manter a imagem
 MAINTAINER Saulo Ribeiro Machado <saulo.ribeiro.machado@gmail.com>
 
 #Argumento a ser passado
 ARG JAR_NAME
 
 #Ajustar timezone
 ENV TZ=America/Sao_Paulo
 RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
 
 #Variavel de ambiente para execucao do CMD
 ENV JAR_NAME ${JAR_NAME}
 RUN echo "Nome do JAR: ${JAR_NAME}"
 
 #Copiar arquivo gerado para dentro da imagem
 RUN mkdir /app
 COPY "target/${JAR_NAME}.jar" "/app/${JAR_NAME}.jar"
 RUN ls -lh /app/${JAR_NAME}.jar

 #Diretorio de trabalho
 WORKDIR /app
 
 #Expor portas de acesso
 EXPOSE 8080
 
#Iniciar
CMD java \
    -Xms256M -XX:MaxMetaspaceSize=256M \
    -XX:+UseParallelGC -XX:+UseStringDeduplication -XX:+OptimizeStringConcat \
    -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Stack=true \
    -Duser.language=pt -Duser.country=BR -Duser.timezone=America/Sao_Paulo \
    -jar ${JAR_NAME}.jar