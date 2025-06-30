// package com.gateway.Config;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.reactive.HandlerMapping;
// import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
// import org.springframework.web.reactive.socket.WebSocketHandler;
// import org.springframework.web.reactive.socket.WebSocketSession;
// import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

// @Configuration
// public class WebSocketConfiguration {

//     // Store all active sessions
//     private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

//     public Map<String, WebSocketSession> getSessions() {
//         return sessions;
//     }

//     @Bean
//     public HandlerMapping webSocketMapping(WebSocketHandler webSocketHandler) {
//         Map<String, WebSocketHandler> map = new HashMap<>();
//         map.put("/ws", webSocketHandler);

//         SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
//         mapping.setOrder(10);
//         mapping.setUrlMap(map);
//         return mapping;
//     }

//     @Bean
//     public WebSocketHandlerAdapter handlerAdapter() {
//         return new WebSocketHandlerAdapter();
//     }

//     @Bean
//     public WebSocketHandler webSocketHandler() {
//         return session -> {
//             sessions.put(session.getId(), session); // Add new session

//             return session.send(
//                 session.receive()
//                     .map(msg -> session.textMessage("Echo: " + msg.getPayloadAsText()))
//             ).doFinally(signal -> sessions.remove(session.getId())); // Cleanup on disconnect
//         };
//     }
// }
