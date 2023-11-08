document.addEventListener('DOMContentLoaded', function() {
    // DOM elements retrieval
    const messageInput = document.getElementById('message-input');
    const sendButton = document.getElementById('send-message');
    const chatWindow = document.getElementById('output');
    const usernameInput = document.getElementById('username-input');
    const setUsernameButton = document.getElementById('set-username');
    const usernameContainer = document.getElementById('username-container');
    const currentUsernameDisplay = document.getElementById('current-username');
    const connectionStatus = document.getElementById('connection-status');

    // WebSocket setup using SockJS and Stomp
    const socket = new SockJS('http://localhost:8081/ws');
    const stompClient = Stomp.over(socket);
    let currentUsername = null;

    // Function to update the connection status display
    function updateConnectionStatus(connected) {
        console.log('Updating connection status to:', connected);
        connectionStatus.textContent = connected ? 'Connected' : 'Disconnected';
        connectionStatus.className = connected ? 'status-connected' : 'status-disconnected';
        sendButton.disabled = !connected;
        console.log(connectionStatus.textContent, connectionStatus.className);
    }

    // Function to append a message to the chat window
    function appendMessage(sender, content) {
        if (sender && content) {
            const messageDiv = document.createElement('div');
            const senderSpan = document.createElement('span');
            const contentSpan = document.createElement('span');

            messageDiv.classList.add('message');
            senderSpan.classList.add('sender');
            contentSpan.classList.add('content');

            senderSpan.textContent = sender + ': ';
            contentSpan.textContent = content;

            messageDiv.appendChild(senderSpan);
            messageDiv.appendChild(contentSpan);
            chatWindow.appendChild(messageDiv);

            chatWindow.scrollTop = chatWindow.scrollHeight;
        }
    }

    // Function to set the current user's username
    function setUsername() {
        const username = usernameInput.value.trim();
        if (username) {
            currentUsername = username;
            usernameContainer.style.display = "none";
            currentUsernameDisplay.textContent = `Nickname: ${currentUsername}`;
            currentUsernameDisplay.style.display = "block";

            stompClient.send("/app/chat.addUser", {}, JSON.stringify({ sender: currentUsername, type: "JOIN" }));
        } else {
            alert("Nickname can't be empty!");
        }
    }

    // Function to send a message using the chat
    function sendMessage() {
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
    }

    // WebSocket connection and event handlers setup
    function setupWebSocket() {
        stompClient.connect({}, function(frame) {
            console.log('Connected:', frame);
            updateConnectionStatus(true);

            stompClient.subscribe('/topic/public', function(chatMessage) {
                const { sender, content } = JSON.parse(chatMessage.body);
                appendMessage(sender, content);
            });
        }, function(error) {
            console.log('Connection error:', error);
            updateConnectionStatus(false);
        });

        // Listening for the 'beforeunload' event to handle page close by the user
        window.addEventListener('beforeunload', function() {
            if (stompClient) {
                stompClient.disconnect(function() {
                    console.log('Disconnected on page unload.');
                    updateConnectionStatus(false);
                });
            }
        });
    }

    // Event listeners setup
    function setupEventListeners() {
        setUsernameButton.addEventListener('click', setUsername);
        sendButton.addEventListener('click', sendMessage);
        messageInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                sendMessage();
            }
        });
    }

    // Initialization function to set up event listeners and the WebSocket connection
    function initializeChat() {
        setupEventListeners();
        setupWebSocket();
    }

    // Start the chat application
    initializeChat();
});