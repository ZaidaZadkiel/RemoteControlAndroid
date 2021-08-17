package de.pro_open.remotecontrol;

import JavaUtils.TCPManager.TcpConnection;

public abstract class TCPCallback {
 abstract void callback(TcpConnection result);
 abstract void reject(String message);
}
