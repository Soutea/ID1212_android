package main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class PlayerHandler {
    private final SocketChannel client;
    private final Random random = new Random();
    private final ByteBuffer outBuf = ByteBuffer.allocate(1000);

    PlayerHandler(SocketChannel client) {
        this.client = client;
        // måste bytas ut2000 för säkerhetsskull,
    }

    public void readMessage() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1);
        try {
            while (client.read(buf) > 0) {
                sendDiceRoll();
            }
            flush();
        } catch (IOException error) { // if error exit the gameround
            throw new RuntimeException(error);
        }
    }

    public boolean flush() throws IOException {
        outBuf.flip();
        client.write(outBuf);
        boolean rem = outBuf.hasRemaining();
        outBuf.compact();
        return rem;
    }

    public void sendDiceRoll() throws IOException {
        outBuf.put((byte)(random.nextInt(6) + 1)); //
        flush();
    }
}
