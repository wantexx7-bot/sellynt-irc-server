import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IRCServer extends WebSocketServer {
    private static Set<WebSocket> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public IRCServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        System.out.println("Новое подключение: " + conn.getRemoteSocketAddress());

        // Уведомляем всех о новом пользователе
        broadcast("JOIN:User" + connections.size());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        System.out.println("Отключение: " + conn.getRemoteSocketAddress());

        // Уведомляем всех о выходе пользователя
        broadcast("LEAVE:User" + (connections.size() + 1));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Получено сообщение: " + message);

        if (message.startsWith("CHAT:")) {
            // Просто пересылаем сообщение всем клиентам
            broadcast(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Ошибка WebSocket: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket сервер запущен на порту 8887");
    }

    private void broadcast(String message) {
        for (WebSocket conn : connections) {
            conn.send(message);
        }
    }

    public static void main(String[] args) {
        IRCServer server = new IRCServer(8887);
        server.start();
    }
}