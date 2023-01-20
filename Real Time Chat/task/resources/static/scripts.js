let input = document.getElementById("input-msg")
let send = document.getElementById("send-msg-btn")
let container = document.getElementById("messages")
let login = document.getElementById("send-username-btn")
let loginInput = document.getElementById("input-username")
let loginForm = document.getElementById("login-form")
let chatForm = document.getElementById("chat-form")
let users = document.getElementById("users")
let receiver = "PUBLIC";
let recipientDiv = document.getElementById("chat-with")
let publicButton = document.getElementById("public-chat-btn")
let currentChat = null;
let stompClient = null;
let username = null;
let download = null;


function onReceive(message) {
    let messageContainer = document.createElement("div");
    messageContainer.classList.add("message-container");


    let text = document.createElement("div");
    text.textContent = message.message;
    text.classList.add("message");

    let sender = document.createElement("div");
    sender.textContent = message.sender;
    sender.classList.add("sender");

    let date = document.createElement("div");
    date.textContent = message.date;
    date.classList.add("date")

    messageContainer.appendChild(text);
    messageContainer.appendChild(sender);
    messageContainer.appendChild(date);

    container.appendChild(messageContainer);
    messageContainer.scrollIntoView({behavior: "smooth"});
}

function chooseChat(chat) {
    if (currentChat !== null) {
        currentChat.unsubscribe();
    }
    container.replaceChildren()
    receiver = chat;


    let path

    if (chat === "PUBLIC") {
        path = '/topic/'
        recipientDiv.textContent = 'Public chat'
        download = stompClient.subscribe("/user/topic/PUBLIC", (messages) => {
            messages = JSON.parse(messages.body)
            for (let i in messages) {
                console.log(messages)
                onReceive(messages[i]);
            }
            download.unsubscribe()
        })
    } else {
        recipientDiv.textContent = chat;
        path = '/user/topic/direct/'
    }

    currentChat = stompClient.subscribe(path + chat, (messages) => {
        console.log("got from /user/topic/direct/" + chat)
        console.log(messages);
        messages = JSON.parse(messages.body)
        for (let i in messages) {
            console.log(messages)
            onReceive(messages[i]);
        }
    });


    stompClient.send("/app/direct/" + chat, {}, JSON.stringify({
        'username': chat
    }));

    Array.from(document.getElementById("users").children).forEach((e) => {
        e.lastElementChild.classList.remove("hidden");
    });

    if (chat !== "PUBLIC") {
        let counter = document.getElementById(chat).parentNode.lastElementChild;
        console.log(counter.classList);
        console.log(counter.parentNode.parentNode.children);

        counter.classList.add("hidden");
        counter.textContent = '0';
    }
}

function addUser(username) {
    let userContainer = document.createElement("div");
    userContainer.classList.add("user-container");

    let user = document.createElement("div");
    user.textContent = username;
    user.id = username;
    user.classList.add("user");

    let counter = document.createElement("div");
    counter.classList.add("new-message-counter");
    counter.textContent = '0';

    user.addEventListener("click", () => {
        chooseChat(username);
    })


    userContainer.appendChild(user);
    userContainer.appendChild(counter);

    users.appendChild(userContainer);
}

function onNotified(notification) {
    if (notification.username !== username) {
        if (notification.type === "LEAVE") {
            let cont = document.getElementById(notification.username).parentNode
            cont.parentNode.removeChild(cont);
        } else if (notification.type === "JOIN") {
            addUser(notification.username);
        }
    }
}

function connect() {
    let socket = new SockJS('/connection');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);


        fetch("/users").then(function (res) {
            return res.json();
        }).then((messages) => {
            console.log(JSON.stringify(messages))
            messages.forEach(message => {
                console.log(message);
                addUser(message.username);
            })
        });


        stompClient.subscribe('/topic/notify', (notification) => {
            console.log(notification);
            onNotified(JSON.parse(notification.body));
        });
        stompClient.subscribe('/user/topic/hello', (hey) => {
            console.log(hey);
        })
        stompClient.subscribe('/user/topic/direct', (mes) => {
            console.log(mes)

            mes = JSON.parse(mes.body);
            if (mes.receiver !== "PUBLIC" && mes.sender !== receiver) {
                let el = document.getElementById(mes.sender).parentNode.lastElementChild
                el.textContent = parseInt(el.textContent) + 1 + "";
                document.getElementById("users").prepend(document.getElementById(mes.sender).parentNode)
            }
        })

        chooseChat(receiver)

    });
}


function sendMessage(message) {
    stompClient.send("/app/message", {}, JSON.stringify({
        'receiver': receiver,
        'sender': username,
        'message': message,
        'date': new Date()
    }));
}


publicButton.addEventListener("click", () => {

    chooseChat("PUBLIC");
})


send.addEventListener("click", (e) => {
    if (input.value === "") return
    console.log(input.value);
    sendMessage(input.value);
    input.value = "";
});

login.addEventListener("click", (e) => {
    if (loginInput.value === "") return;
    console.log(loginInput.value);
    username = loginInput.value;
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({'username': username}))
    loginForm.classList.add("hidden");
    chatForm.classList.replace("hidden", "flex");
})

connect();



