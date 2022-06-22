# Command to generate KeyVault and the required account

```
# Create the Resource Group
$> az group create --location francecentral --resource-group UBS 

$ Create a KeyVault
$> az keyvault create --location francecentral --name ubsconfluente2etest --resource-group UBS --network-acls-ips 0.0.0.0/0 

# Create a key in the vault
$> az keyvault key create --name wrappingkey --vault-name ubsconfluente2etest

# For the application Kafka Producer, create a user to access the key only for encryption
$> az ad sp create-for-rbac -n "E2EProducer" --skip-assignment

{
  "appId": "ff1d6f32-40fc-477a-91b2-3ddee8175da1",
  "displayName": "E2EProducer",
  "name": "ff1d6f32-40fc-477a-91b2-3ddee8175da1",
  "password": "T5-3bWFaeSy~R22QT7kjv1pUuiHigNsdVl",
  "tenant": "8bac639d-2c4e-43d6-9387-197ea85d40dd"
}

# Grant the required permission
$> az keyvault set-policy --name ubsconfluente2etest --spn ff1d6f32-40fc-477a-91b2-3ddee8175da1 --key-permissions encrypt get

# For the application Kafka Consumer, create a user to access the key only for decryption
$> az ad sp create-for-rbac -n "E2EConsumer" --skip-assignment
{
  "appId": "1cbebbc2-6a06-498a-8dc6-2efd3c2bc406",
  "displayName": "E2EConsumer",
  "name": "1cbebbc2-6a06-498a-8dc6-2efd3c2bc406",
  "password": "2oA2R65YuM05xo4d3z.Qi03IAzU_hiZ05z",
  "tenant": "8bac639d-2c4e-43d6-9387-197ea85d40dd"
}

# Grant the required permission
$> az keyvault set-policy --name ubsconfluente2etest --spn 1cbebbc2-6a06-498a-8dc6-2efd3c2bc406 --key-permissions decrypt get

```

# Command to rotate a key

```
# To rotate a key, the command is identical to key creation
# The previous version would still be available until it's deleted
# It can be deleted manually, or through an expiration date
$> az keyvault key create --name wrappingkey --vault-name ubsconfluente2etest 
```

# How to execute

```
mvn package
java -jar target/confluent-encryption-ubs-1.0-SNAPSHOT-shaded.jar
```

# Main classes available

* `io.confluent.Producer` - Produces one message in the `e2e` topic
* `io.confluent.Consumer` - Consumes one batch of messages from the `e2e` topic
* `io.confluent.Main` - Execute Producer than the Consumer

# Kafka Connect

```shell
confluent-hub install confluentinc/kafka-connect-s3:10.0.8

plugin.path=/usr/share/java,/Users/kirill.kulikov/confluent/confluent-7.0.1/share/confluent-hub-components
```
