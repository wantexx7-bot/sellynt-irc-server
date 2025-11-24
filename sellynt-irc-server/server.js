const WebSocket = require("ws");

const PORT = process.env.PORT || 3000;

// Создаём WebSocket сервер
const server = new WebSocket.Server({ port: PORT });

let clients = [];

server.on("connection", socket => {
    clients.push(socket);
    console.log("Client connected. Total:", clients.length);

    socket.on("message", data => {
        const msg = data.toString();
        console.log("Received:", msg);

        // Рассылаем всем клиентам
        for (let client of clients) {
            if (client.readyState === WebSocket.OPEN) {
                client.send(msg);
            }
        }
    });

    socket.on("close", () => {
        clients = clients.filter(c => c !== socket);
        console.log("Client disconnected. Total:", clients.length);
    });

    socket.on("error", (err) => {
        console.log("Socket error:", err);
    });
});

console.log("IRC WebSocket server running on port", PORT);
