Software Use:

XAMPP server
Netbeans IDE





Steps:

1. Run server once. 
2. Run clients several times. Then, give some input and see how every client can see it. Hence, broadcasring is already implemented.
3. Open the TCPserver.java file.
4. The void run() function runs continously. The server is awaiting new clients all the time (as seen in the code).
5. synchronized void handle(int ID, String input) ->>>>> this is the most important function. whenever server receives a string, this function activates/runs.
6. Look up how if it receives ".bye", it closes that client.
7. Otherwise, it sends the string to all clients (broadcasting).
8. Think how u can change the code for multicast and unicast. DO IT YOURSELF. YOU STUDY IN 4-2. NOT IN 2-1.
9. This handle function should suffice for the offline. You can look up other functions if you like, however you will not need to change them anyway.
10. Open up ChatServerThread.java.
11. It has a void run() too, which calls the handle function mentioned above whenever it receives a string. 
12. So the flow is: client -> run() in ChatServerThread -> handle() in TCPServer. This should be enough for server.

13. How to do so many things in the offline then???
14. Use commands. 
15. For example, a client sends "PM Mythoss" to server. Server looks up the ID 'Mythoss' and see if they are friends. If so, server starts passing message between them.
16. How does server understands that it is a private message command??
17. Use split function to get the first word as the command. Use If-Else block to write codes for all the commands.

18. Similarly, look up the handle() and run() in the TCPClient code. Same logic applies there. You will understand them easily if u understand the code for the server.


