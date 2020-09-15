##POC Kafka x OSB

###O que é Apache Kafka? 

Para entender melhor sobre Apache Kafka, podemos começar entendendo o fluxo básico do Kafka, que pode ser resumido em três ações bem simples:

* Você produz uma mensagem.
* Essa mensagem é anexada em um tópico.
* Você então consome essa mensagem.

[![GraphQL Utilization](http://soaone.com.br/osb/basic_kafka_arquitecture.png "GraphQL Utilization")](http://soaone.com.br/osb/basic_kafka_arquitecture.png "Kafka Arquitetura Básica")

*Se você quer mover e transformar um grande volume de dados em tempo real entre diferentes sistemas, então Apache Kafka pode ser exatamente o que você precisa.*

###Conceitos

* ####Mensagens

Mensagem é o principal recurso do Kafka. Todos os eventos do Kafka podem ser resumidos em mensagens, sendo consumidas e produzidas através de tópicos. Uma mensagem pode ser desde uma simples String com “Hello World!” ou até mesmo um JSON contendo um objeto do seu domínio.

* ####Tópicos

Um tópico é como categorizamos grupos de mensagens dentro do Kafka. Todas as mensagens enviadas para o Kafka permanecem em um tópico. Como comentado sobre Event Sourcing, mensagens são imutáveis e ordenadas.

* ####Producer

Um Kafka Producer é responsável por enviar uma mensagem para um tópico específico. De forma simples, você pode produzir uma mensagem em um tópico.

Uma vez que uma mensagem é produzida em um tópico o próprio Kafka organiza a mensagem em uma partição, garantindo sempre a ordem das mensagens produzidas, como citado anteriormente.

* ####Consumer

Temos os tópicos, e as mensagens dentro dos tópicos. Com o Kafka Consumer é possível ler essas mensagens de volta. Importante entender que, ao ler uma mensagem com o consumer, a mensagem não é retirada do tópico.

* ####Apache Zookeeper

O Zookeeper é um serviço centralizado para, entre outras coisas, coordenação de sistemas distribuídos. O Kafka é um sistema distribuído, e consequentemente delega diversas funções de gerenciamento e coordenação para o Zookeeper.

* ####Kafka Brokers | Kafka Clusters
[![GraphQL Utilization](http://soaone.com.br/osb/kafka_brooker.png "GraphQL Utilization")](http://soaone.com.br/osb/kafka_brooker.png "Kafka Brooker")

Você pode rodar o Kafka local na sua máquina onde sua própria máquina teria um Kafka Broker formando um Kafka Cluster, como pode subir n instâncias de Kafka Brokers e todas estarem no mesmo Kafka Cluster. Com isso é possível escalar sua aplicação, e replicar os dados entre os Brokers.

###Arquitetura proposta

Tendo em vista a dificuldade de criação de `customized transport` no Oracle Service Bus e em contrapartida, a facilidade de criação de Producer/Consumer Kafka, no Java Spring Boot, a arquitetura proposta para a POC em questão foi a de se utilizar o Midleware Weblogi Server do Oracle Service Bus (OSB), como servidor para deploy das aplicações (WAR) desenvolvidas em Spring Boot.

Foram criados 2 aplições WEB em Spring Boot:

1. Producer
2. Consumer

O aplicativo web `Producer` foi publicado como uma `API REST` com método `POST` onde recebe um documento `JSON` e publica a mensagem em um tópico do Kafka.

O aplicativo web `Consumer` tem um listener Kafka que fica escutando no tópico do Kafka e chama uma `API REST` publicada no contexto do OSB.

A integração inicia chamando uma `API REST` do OSB, que por sua vez chama a o `Consumer` do Spring Boot, publicando uma mensagem no tópico do Kafka. Um `listenter` consome a mensagem do tópico do Kafka e chama uma `API REST` do OSB que grava um registro em um banco de dados.

[![GraphQL Utilization](http://soaone.com.br/osb/arquitetura_osb_kafka.png "GraphQL Utilization")](http://soaone.com.br/osb/arquitetura_osb_kafka.png "Arquitetura proposta")

###Ambiente necessário para POC

Para viabilizar essa POC o ambiente necessário foi o seguinte:

* 2 container docker para Kafka e Zookeper
* Banco de dados Oracle XE
* Oracle Fusion Middleware 12c

####Subindo os containers Docker

Criar arquivo nomeado como docker-compose.yml com o conteúdo abaixo:

	version: "3"
	
	services:
	  zookeeper:
	    image: wurstmeister/zookeeper:3.4.6
	    ports:
	      - "2181:2181"
	      - "2888:2888"
	      - "3888:3888"
	  kafka:
	    image: wurstmeister/kafka:2.12-2.3.0
	    depends_on:
	      - zookeeper
	    ports:
	      - "9092:9092"
	    environment:
	      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
	      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
	      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

Executar comando:

`docker-compose -f docker-compose.yml up -d`

[![GraphQL Utilization](http://soaone.com.br/osb/containers_kafka.png "Containers")](http://soaone.com.br/osb/containers_kafka.png "Containers")

####Criar tópico no Kafka

O próximo passo é a criação de um tópico no Apache Kafka. Para isso vamos usar o Konductor, que é uma interface gáfica amigável para criação e configuração dos tópicos.

[![GraphQL Utilization](http://soaone.com.br/osb/conduktor.gif "Conduktor")](http://soaone.com.br/osb/containers_kafka.png "Conduktor")

Com o ambiente preparado e o tópico do Kafka criado, vamos para criação dos projetos.

###Passos para reprodução da POC

* 1 - Acessar https://start.spring.io/ criar projeto conforme imagem abaixo:

[![GraphQL Utilization](http://soaone.com.br/osb/pom_producer.png "Producer")](http://soaone.com.br/osb/pom_producer.png "Arquitetura Producer")

* 2 - Importar projeto no Eclipse
* 3 - criar diretório no projeto em ...src\main\webapp\WEB-INF
* 4 - criar arquivo weblogic.xml e dispatcherServlet-servlet.xml

 Conteúdo de **weblogic.xml**

    <?xml version="1.0" ecoding="UTF-8"?>
            <wls:weblogic-web-app
                   xmlns:wls="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app
                   http://xmlns.oracle.com/weblogic/weblogic-web-app/1.4/weblogic-web-app.xsd">
                <wls:context-root>/myweb</wls:context-root>
                <wls:container-descriptor>
                    <wls:prefer-application-packages>
                        <wls:package-name>org.slf4j.*</wls:package-name>
                        <wls:package-name>org.springframework.*</wls:package-name>
            <wls:package-name>com.fasterxml.jackson.*</wls:package-name>
                    </wls:prefer-application-packages>
                </wls:container-descriptor>
            </wls:weblogic-web-app>
    
Conteúdo de **dispatcherServlet-servlet.xml**

    ?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">
    </beans>
    
Os mesmos passos devem ser seguidos para criação do `Consumer`.

Quando o projeto estiver concluído deve-se gerar o .WAR pelo Maven e depois proceder com a implantação do projeto no Weblogic do OSB.

*Os fontes dos projetos serão postados no final  desse documento.*

###Implantar WAR como aplicativo no Weblogic

Siga os passos do exemplo abaixo para publicar o aplicativo no Weblogic:

[![GraphQL Utilization](http://soaone.com.br/osb/implantar_war.gif "Implantar aplicativo")](http://soaone.com.br/osb/implantar_war.gif "Implantar aplicativo")
 
###Projetos OSB

Para a poc foram criados 2 projetos no OSB, um para publicar a mensagem no Kafka, chamando o aplicativo Spring Boot e outro para gravar os dados do documento JSON no banco de dados. Esse segundo é chamado pelo outro aplicativo Spring Boot, que fica listener na fila Kafka.

[![GraphQL Utilization](http://soaone.com.br/osb/projetos_osb.png "Projetos OSB")](http://soaone.com.br/osb/projetos_osb.png "Projetos OSB")

*Os fontes dos projetos serão postados no final  desse documento.*

###POC em ação

[![GraphQL Utilization](http://soaone.com.br/osb/osb_spring_kafka.gif "GraphQL Utilization")](http://soaone.com.br/osb/osb_spring_kafka.gif "POC em ação")

###Recursos do projeto

* Poc Kafka x OSB – https://github.com/graphql/graphiql

