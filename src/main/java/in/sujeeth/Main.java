package in.sujeeth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;

public class Main {
    public static void main(String[] args) {
        // Configure the MCP server process (Java JAR path as provided)
        var server = ServerParameters.builder("java")
                .args("-jar",
                        "/home/sujeeth/projects/spring-ai-projects/search_engine_mcp/target/search_engine_mcp-0.0.1-SNAPSHOT.jar",
                        // Redirect console logs to STDERR so STDOUT is reserved for MCP JSON-RPC
                        "--logging.console.target=System.err",
                        // Reduce noise: hide the Spring banner
                        "--spring.main.banner-mode=off",
                        // Optional: lower default log level (still goes to STDERR)
                        "--logging.level.root=WARN")
                .build();

        // Create stdio transport for MCP
        var transport = new StdioClientTransport(server, new JacksonMcpJsonMapper(new ObjectMapper()));

        // Build a synchronous MCP client
        McpSyncClient client = McpClient.sync(transport).build();

        // Initialize the client
        client.initialize();

        // List tools available from the MCP server and print them
        var listToolsResult = client.listTools();
        System.out.println("Available tools from MCP server:");
        listToolsResult.tools().stream().forEach(tool -> {
            System.out.println("- Tool: " + tool.name());
            System.out.println("  Description: " + tool.description());
            System.out.println("  Schema: " + tool.inputSchema());
        });

        // Note: depending on SDK version, explicit shutdown may not be required.
    }
}
