# spring-sse

build with 
  mvn package

then run with 
  java -jar target/spring-sse-0.0.1-SNAPSHOT.jar
  
and consume SSE using curl
  curl http://localhost:8080/sse
  
  and terminate it with cntr+c
  
one of ten executions will give your sseEmitter() method is called twice  
