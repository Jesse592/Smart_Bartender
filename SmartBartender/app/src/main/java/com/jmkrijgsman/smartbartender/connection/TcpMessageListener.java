package com.jmkrijgsman.smartbartender.connection;

public interface TcpMessageListener {
    void OnMessageReceived(final String message);
}
