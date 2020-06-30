tag1:
    cd {$path}/Board/out/production/Board
    rmiregistry

tag2:
    java -Djava.rmi.server.codebase=file:"{$path}/Board/out/production/Board" -jar ../../artifacts/Server_jar/ServerStart.jar 

tag3-N:
    java -Djava.rmi.server.codebase=file:"{$path}/Board/out/production/Board" -jar ../../artifacts/ClientStart_jar/ClientStart.jar 