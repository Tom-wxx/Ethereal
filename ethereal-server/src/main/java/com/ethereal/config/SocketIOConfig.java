package com.ethereal.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Slf4j
@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Value("${socketio.host}")
    private String host;

    @Value("${socketio.port}")
    private int port;

    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin("*");
        config.setPingTimeout(60000);
        config.setPingInterval(25000);

        server = new SocketIOServer(config);
        server.start();
        log.info("SocketIO Server started on port {}", port);
        return server;
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }
}
