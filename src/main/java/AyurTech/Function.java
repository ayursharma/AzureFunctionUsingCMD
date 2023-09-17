package AyurTech;

import java.sql.*;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class Function {
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        String connectionString = "jdbc:mysql://localhost:3306/ayurdb?user=root&password=Ratlam@123";
        try (Connection connection = DriverManager.getConnection(connectionString);
             Statement statement = connection.createStatement()) {
            // Perform database operations
            // Example: Execute a query
            String sqlQuery = "SELECT * FROM ayurtb";
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                StringBuilder responseBody = new StringBuilder();
                while (resultSet.next()) {
                    responseBody.append("ID: ").append(resultSet.getString("ID")).append(", ")
                            .append("Name: ").append(resultSet.getString("Name")).append(", ")
                            .append("City: ").append(resultSet.getString("City")).append(", ")
                            .append("Status: ").append(resultSet.getString("Status")).append("\n ");
                }
                return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "text/plain; charset=utf-8")
                        .body(responseBody.toString())
                        .build();
            }
        } catch (SQLException ex) {
           context.getLogger().severe("Error connecting to MySQL database: " + ex.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @FunctionName("HttpPostExample")
    public HttpResponseMessage handlePostRequest(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a POST request.");
        Optional<String> requestBody = request.getBody();

        if (!requestBody.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Missing request body.")
                    .build();
        }
        String requestBodyContent = requestBody.get();

        String connectionString = "jdbc:mysql://localhost:3306/ayurdb?user=root&password=Ratlam@123";
        try (Connection connection = DriverManager.getConnection(connectionString);
             Statement statement = connection.createStatement()) {

            String insertQuery = "INSERT INTO ayurtb (ID, Name, City, Status) VALUES (5, 'Tonny', 'Satna', 'Teacher')";
            statement.executeUpdate(insertQuery);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body("POST request processed successfully.")
                    .build();
        } catch (SQLException ex) {
            context.getLogger().severe("Error connecting to MySQL database: " + ex.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }}
