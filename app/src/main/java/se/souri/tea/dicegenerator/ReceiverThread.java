package se.souri.tea.dicegenerator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class ReceiverThread extends Thread {
    private final DiceRollListener listener;
    private SocketChannel sock;

    public ReceiverThread(DiceRollListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            sock = SocketChannel.open();
            sock.connect(new InetSocketAddress("192.168.0.10", 23456)); //TODO: hårdkodad IP just nu
            ByteBuffer buf = ByteBuffer.allocate(1);
            while (sock.isConnected()) {
                if (sock.read(buf) == -1) {
                    break;
                }
                buf.flip();
                byte roll = buf.get();
                buf.compact();
                listener.onDiceRoll(roll);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void rollDiceAgain() { //skicka byte till servern
        new Thread(new Runnable() { //ny tråd då vi ej får röra nätverket från maintråden
            @Override
            public void run() {
                synchronized (sock) { // om vi råkar trycka innan det skrivits färdigt, vill vi undvika att
                    try {
                        ByteBuffer buf = ByteBuffer.allocate(1);
                        buf.put((byte) 0);
                        buf.flip();
                        sock.write(buf);
                        buf.compact();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }).start();
    }

}