MCP Client Search Gateway (Java)

Overview
- This repository contains Java program that launches a local MCP server over stdio and connects to it using the official Model Context Protocol (MCP) Java client.
- It initializes the client, lists the tools exposed by the server, and prints their names, descriptions, and input schemas.
- The client transports JSON‑RPC via STDOUT/STDIN and routes all human‑readable logs to STDERR to avoid protocol parsing errors.

Key use cases
- Quick sanity check for an MCP server: verify the server starts, initializes, and exposes tools correctly.
- Example template for integrating an MCP stdio server with a Java client (e.g., for custom agents or gateways).

Project layout
- Main class: src/main/java/in/sujeeth/Main.java
- Build: Maven (pom.xml)
- Minimal runtime dependency: spring-ai-starter-mcp-client (brings the MCP Java SDK and Jackson integration)

Prerequisites
- JDK 17+ installed and on PATH (project targets Java 17)
- Maven 3.9+
- An MCP server JAR you can run locally (the example expects a Spring Boot JAR)

How it works
1) Main.java builds ServerParameters to start your Spring Boot MCP server JAR
2) Additional Spring Boot arguments are passed so that:
3) ```
   - --logging.console.target=System.err ensures all logs go to STDERR
   - --spring.main.banner-mode=off disables the banner
   - --logging.level.root=WARN reduces log noise
   ```
   These keep STDOUT clean for MCP JSON‑RPC only, which is required by the MCP client transport.
3) The client uses StdioClientTransport with JacksonMcpJsonMapper(ObjectMapper) to read/write JSON‑RPC.
4) After initialize(), the client calls listTools() and prints each tool’s name, description, and input schema.

Configure the server JAR path
- In Main.java, update the hardcoded path to your server JAR:
  src/main/java/in/sujeeth/Main.java
  .args("-jar",
        "/absolute/path/to/your/search_engine_mcp-0.0.1-SNAPSHOT.jar",
        "--logging.console.target=System.err",
        "--spring.main.banner-mode=off",
        "--logging.level.root=WARN")

Build
- From the project root:
  mvn -q -DskipTests package

Run from your IDE
- Open the project, run the in.sujeeth.Main class.

What you should see
- Client logs indicating the MCP server process started.
- A list of tools printed to the console, for example:
  Available tools from MCP server:
  - Tool: search
    Description: Search the web for relevant results
    Schema: { ... }

Troubleshooting
- JsonParseException: Unexpected character (...) while reading inbound messages
  Cause: The server printed non‑JSON content (logs, banners) to STDOUT, which the client tried to parse as JSON‑RPC.
  Fix: Ensure Main.java passes these arguments to your Spring Boot server:
       --logging.console.target=System.err --spring.main.banner-mode=off --logging.level.root=WARN
       Also confirm the server (or any libraries) do not explicitly write to STDOUT.

- Server not found or fails to start
  - Verify the absolute path to your server JAR in Main.java.
  - Make sure Java can run the JAR from your user account.

- No tools are listed
  - Confirm your MCP server actually registers tools.
  - Check server logs (now on STDERR) for initialization or registration errors.

Notes
- This client is minimal and synchronous (McpSyncClient). It’s ideal for quick diagnostics and examples.
- The dependency spring-ai-starter-mcp-client pulls in Jackson and the MCP Java SDK. The client uses JacksonMcpJsonMapper with a default ObjectMapper.