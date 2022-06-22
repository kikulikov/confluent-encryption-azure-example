# Confluent Encryption with Azure Vault

## Generate the KeyVault and keys

```
# Create the Resource Group
$> az group create --location francecentral --resource-group PLAYPEN 

$ Create a KeyVault
$> az keyvault create --location francecentral --name kk-vault --resource-group PLAYPEN --network-acls-ips 0.0.0.0/0 

# Create a key in the vault
$> az keyvault key create --name wrappingkey --vault-name kk-vault

# For the application Kafka Producer, create a user to access the key only for encryption
$> az ad sp create-for-rbac -n "e2e-producer" --skip-assignment

{
  "appId": "ff1d6f32-40fc-477a-91b2-3ddee8175da1",
  "displayName": "E2EProducer",
  "name": "ff1d6f32-40fc-477a-91b2-3ddee8175da1",
  "password": "XXX",
  "tenant": "8bac639d-2c4e-43d6-9387-197ea85d40dd"
}

# Grant the required permission
$> az keyvault set-policy --name kk-vault --spn ff1d6f32-40fc-477a-91b2-3ddee8175da1 --key-permissions encrypt get

# For the application Kafka Consumer, create a user to access the key only for decryption
$> az ad sp create-for-rbac -n "e2e-consumer" --skip-assignment
{
  "appId": "1cbebbc2-6a06-498a-8dc6-2efd3c2bc406",
  "displayName": "E2EConsumer",
  "name": "1cbebbc2-6a06-498a-8dc6-2efd3c2bc406",
  "password": "XXX",
  "tenant": "8bac639d-2c4e-43d6-9387-197ea85d40dd"
}

# Grant the required permission
$> az keyvault set-policy --name kk-vault --spn 1cbebbc2-6a06-498a-8dc6-2efd3c2bc406 --key-permissions decrypt get

```

## Rotate the keys

```
# To rotate a key, the command is identical to key creation
# The previous version would still be available until it's deleted
# It can be deleted manually, or through an expiration date
$> az keyvault key create --name wrappingkey --vault-name kk-vault 
```

## Available Main classes

* `io.confluent.Producer` - Produces one message in the `e2e` topic
* `io.confluent.Consumer` - Consumes one batch of messages from the `e2e` topic
* `io.confluent.Main` - Execute Producer than the Consumer

## Importing the required libraries 

```bash
mvn install:install-file -Dfile=confluent-encryption-common-6.0.x-1.0.14.jar -DgroupId=io.confluent.confluent-encryption -DartifactId=confluent-encryption-common -Dversion=6.0.x-1.0.14 -Dpackaging=jar
mvn install:install-file -Dfile=confluent-encryption-kafka-6.0.x-1.0.14.jar -DgroupId=io.confluent.confluent-encryption -DartifactId=confluent-encryption-kafka -Dversion=6.0.x-1.0.14 -Dpackaging=jar
mvn install:install-file -Dfile=confluent-encryption-serializer-6.0.x-1.0.14.jar -DgroupId=io.confluent.confluent-encryption -DartifactId=confluent-encryption-serializer -Dversion=6.0.x-1.0.14 -Dpackaging=jar
mvn install:install-file -Dfile=confluent-encryption-azure-6.0.x-1.0.14.jar -DgroupId=io.confluent.confluent-encryption -DartifactId=confluent-encryption-azure -Dversion=6.0.x-1.0.14 -Dpackaging=jar
```

## Building the application

```shell
mvn clean package # builds and run the tests
mvn clean package -DskipTests # builds but ignores the tests
```

## Running the application

```
# Producer only
java -cp target/confluent-encryption-ubs-1.0-SNAPSHOT-jar-with-dependencies.jar io.confluent.Producer src/main/resources/producer.properties

# Consumer only
java -cp target/confluent-encryption-ubs-1.0-SNAPSHOT-jar-with-dependencies.jar io.confluent.Consumer src/main/resources/consumer.properties

# Producer and then Consumer
java -cp target/confluent-encryption-ubs-1.0-SNAPSHOT-jar-with-dependencies.jar io.confluent.Main src/main/resources/producer.properties src/main/resources/consumer.properties
```

## Kafka Connect

```shell
confluent-hub install confluentinc/kafka-connect-s3:10.0.8

plugin.path=/usr/share/java,/Users/kirill.kulikov/confluent/confluent-7.0.1/share/confluent-hub-components
```
