package com.example;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            String response = """
                <html>
                <head>
                    <title>CI/CD POC</title>
                    <style>
                        body {
                            font-family: Arial;
                            text-align: center;
                            background-color: #f4f4f4;
                            margin-top: 50px;
                        }
                        h1 {
                            color: #2c3e50;
                        }
                        p {
                            font-size: 18px;
                        }
                    </style>
                </head>
                <body>
                    <h1> CI/CD POC Successful!</h1>
                    <p>Java app deployed </p>
                    <p>GitHub+jenkins+DockerHub</p>
                </body>
                </html>
            """;

            exchange.getResponseHeaders().add("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            System.out.println("Served HTML page");
        });

        server.start();
        System.out.println("Server started on port 8080");
    }
}
