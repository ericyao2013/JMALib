package com.microavia.jmalib.log;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * User: ton Date: 03.06.13 Time: 14:51
 */
public abstract class BinaryLogReader implements LogReader {
    protected ByteBuffer buffer;
    private FileChannel channel;
    private long channelPosition = 0;

    public BinaryLogReader(String fileName) throws IOException {
        buffer = ByteBuffer.allocate(8192);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.flip();
        channel = new RandomAccessFile(fileName, "r").getChannel();
    }

    @Override
    public void close() throws IOException {
        channel.close();
        channel = null;
    }

    public int fillBuffer() throws IOException {
        buffer.compact();
        int n = channel.read(buffer);
        buffer.flip();
        if (n < 0) {
            throw new EOFException();
        }
        channelPosition += n;
        return n;
    }

    protected void fillBuffer(int required) throws IOException {
        if (buffer.remaining() < required) {
            buffer.compact();
            int n = channel.read(buffer);
            buffer.flip();
            if (n < 0 || buffer.remaining() < required) {
                throw new EOFException();
            }
            channelPosition += n;
        }
    }

    protected long position() {
        return channelPosition - buffer.remaining();
    }

    protected void position(long pos) throws IOException {
        buffer.clear();
        channel.position(pos);
        channelPosition = pos;
        int n = channel.read(buffer);
        buffer.flip();
        if (n < 0) {
            throw new EOFException();
        }
        channelPosition += n;
    }
}
