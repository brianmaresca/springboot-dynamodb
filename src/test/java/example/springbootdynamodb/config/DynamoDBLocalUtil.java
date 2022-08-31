package example.springbootdynamodb.config;

import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import java.net.ServerSocket;
import lombok.SneakyThrows;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBLocalUtil {

  public static DynamoDbClient createLocalDynamoClient(AmazonDynamoDBLocal dynamoDBLocal) {
    return dynamoDBLocal.dynamoDbClient();
  }

  public static AmazonDynamoDBLocal createLocalDynamo() {
    return DynamoDBEmbedded.create();
  }

  @SneakyThrows
  public static DynamoDBProxyServer createProxyServer(String portString) {
    DynamoDBProxyServer server = ServerRunner.createServerFromCommandLineArgs(
        new String[]{
            "-inMemory",
            "-port", portString
        });

    server.start();
    return server;
  }

  @SneakyThrows
  public static String getFreePortString() {
    ServerSocket socket = new ServerSocket(0);
    String freePortString = Integer.toString(socket.getLocalPort());
    socket.close();
    return freePortString;
  }

}
