import socket

HOST = "192.168.178.213"  # Make IP static or update field dynamicly
PORT = 65432

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with conn:
        print(f"Connected by {addr}")
        conn.send("Connected to server\n".encode())
        while True:
            data = conn.recv(1024)
            if not data:
                break
            conn.sendall(data)