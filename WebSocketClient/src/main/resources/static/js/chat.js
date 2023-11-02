document.addEventListener('DOMContentLoaded', function() {
    // Элементы DOM
    const messageInput = document.getElementById('message-input');
    const sendButton = document.getElementById('send-message');
    const chatWindow = document.getElementById('output');
    const usernameInput = document.getElementById('username-input');
    const setUsernameButton = document.getElementById('set-username');
    const usernameContainer = document.getElementById('username-container');
    const currentUsernameDisplay = document.getElementById('current-username');


    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);
    let currentUsername = null;

    // Утилиты
    const appendMessage = (sender, content) => {
        chatWindow.innerHTML += `<div>${sender}: ${content}</div>`;
    };

    const setUsername = () => {
        const username = usernameInput.value.trim();
        if (username) {
            currentUsername = username;
            usernameContainer.style.display = "none";
            currentUsernameDisplay.textContent = `Nickname: ${currentUsername}`;
            currentUsernameDisplay.style.display = "block";
        } else {
            alert("Nickname can't be empty!");
        }
    };

    const sendMessage = () => {
        const message = messageInput.value.trim();
        if (!currentUsername) {
            alert("Please set a nickname first!");
            return;
        }

        if (message) {
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
                content: message,
                sender: currentUsername,
                type: "CHAT"
            }));
            messageInput.value = '';
        } else {
            alert("The message can't be empty!");
        }
    };

    // Подключение и обработчики
    stompClient.connect({}, (frame) => {
        console.log('Connected:', frame);
        stompClient.subscribe('/topic/public', (chatMessage) => {
            const { sender, content } = JSON.parse(chatMessage.body);
            appendMessage(sender, content);
        });
        stompClient.send("/app/chat.addUser", {}, JSON.stringify({ sender: "userFromClient", type: "JOIN" }));
    });

    // Слушатели событий
    setUsernameButton.addEventListener('click', setUsername);
    sendButton.addEventListener('click', sendMessage);
    messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });
});