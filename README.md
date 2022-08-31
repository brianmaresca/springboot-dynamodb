# Spring boot + aws sdk v2 + dynamodb example application

## Main Features

- spring data "ish" dynamodb @Repository classes. create a repository class like BooksRepository,
  extending DynamoDbRepository
- tables get created on startup, dynamodb annotations allow the table to be configured appropriately
- custom dynamodb converter provider class to serialize/deserialize enum values into their string
  form, as indicated by @JsonValue
- logging filter to create a request id for every api request and include in every log statement

# Testing

- tests that extend AbstractBaseTest start up an embedded locally dynamodb server that backs the
  tests
- must do a one time mvn build to pull down sql lite libraries needed by embedded dynamodb local

# Running the application locally

- before starting the application, run `docker-compose up` from the `dynamodblocal-docker` directory
- start the application with the "dev" profile 