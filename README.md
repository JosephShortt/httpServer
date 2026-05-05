# httpServer

## Simple HTTP server
This simple http server written entirely in java utilising java.io and java.net from the java standard library handles requests including GET and POST requests.
The server receives listens for connections on port 8080 and parses the request line and headers. 
The server is also multi threaded making use of java threads to handle multiple client requests.

## The server responds to the following endpoints
1. GET http:localhost:8080/
2. GET http:localhost:8080/hello
3. GET http:localhost:8080/about
4. curl -X POST http:localhost:8080/echo -d "message"