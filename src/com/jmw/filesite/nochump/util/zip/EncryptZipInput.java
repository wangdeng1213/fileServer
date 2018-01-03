package com.jmw.filesite.nochump.util.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import com.jmw.filesite.nochump.util.extend.ZipCrypto;

public class EncryptZipInput extends EncryptInflater
  implements ZipConstants
{
  private EncryptZipEntry entry;
  private CRC32 crc = new CRC32();
  private long remaining;
  private byte[] tmpbuf = new byte[512];
  private static final int STORED = 0;
  private static final int DEFLATED = 8;
  private boolean closed = false;

  private boolean entryEOF = false;

  private byte[] b = new byte[256];

  private void ensureOpen()
    throws IOException
  {
    if (this.closed)
      throw new IOException("Stream closed");
  }

  public EncryptZipInput(InputStream in, String password)
  {
    super(new PushbackInputStream(in, 512), new Inflater(true), 512);
    this.usesDefaultInflater = true;
    if (in == null) {
      throw new NullPointerException("in is null");
    }
    this.password = password;
  }

  public EncryptZipEntry getNextEntry()
    throws IOException
  {
    ensureOpen();
    if (this.entry != null) {
      closeEntry();
    }
    this.crc.reset();
    this.inf.reset();
    if ((this.entry = readLOC()) == null) {
      return null;
    }
    if (this.entry.method == 0) {
      this.remaining = this.entry.size;
    }
    this.entryEOF = false;
    return this.entry;
  }

  public void closeEntry()
    throws IOException
  {
    ensureOpen();
    while (read(this.tmpbuf, 0, this.tmpbuf.length) != -1);
    this.entryEOF = true;
  }

  public int available()
    throws IOException
  {
    ensureOpen();
    if (this.entryEOF) {
      return 0;
    }
    return 1;
  }

  public int read(byte[] b, int off, int len)
    throws IOException
  {
    ensureOpen();
    if ((off < 0) || (len < 0) || (off > b.length - len))
      throw new IndexOutOfBoundsException();
    if (len == 0) {
      return 0;
    }

    if (this.entry == null) {
      return -1;
    }
    switch (this.entry.method) {
    case 8:
      len = super.read(b, off, len);
      if (len == -1) {
        readEnd(this.entry);
        this.entryEOF = true;
        this.entry = null;
      } else {
        this.crc.update(b, off, len);
      }
      return len;
    case 0:
      if (this.remaining <= 0L) {
        this.entryEOF = true;
        this.entry = null;
        return -1;
      }
      if (len > this.remaining) {
        len = (int)this.remaining;
      }
      len = this.in.read(b, off, len);
      if (len == -1) {
        throw new ZipException("unexpected EOF");
      }
      this.crc.update(b, off, len);
      this.remaining -= len;
      return len;
    }
    throw new InternalError("invalid compression method");
  }

  public long skip(long n)
    throws IOException
  {
    if (n < 0L) {
      throw new IllegalArgumentException("negative skip length");
    }
    ensureOpen();
    int max = (int)Math.min(n, 2147483647L);
    int total = 0;
    while (total < max) {
      int len = max - total;
      if (len > this.tmpbuf.length) {
        len = this.tmpbuf.length;
      }
      len = read(this.tmpbuf, 0, len);
      if (len == -1) {
        this.entryEOF = true;
        break;
      }
      total += len;
    }
    return total;
  }

  public void close()
    throws IOException
  {
    if (!this.closed) {
      super.close();
      this.closed = true;
    }
  }

  private EncryptZipEntry readLOC()
    throws IOException
  {
    try
    {
      readFully(this.tmpbuf, 0, 30);
    } catch (EOFException e) {
      return null;
    }
    if (get32(this.tmpbuf, 0) != 67324752L) {
      return null;
    }

    int len = get16(this.tmpbuf, 26);
    if (len == 0) {
      throw new ZipException("missing entry name");
    }
    int blen = this.b.length;
    if (len > blen) {
      do
        blen *= 2;
      while (len > blen);
      this.b = new byte[blen];
    }
    readFully(this.b, 0, len);
    EncryptZipEntry e = createZipEntry(getUTF8String(this.b, 0, len));

    e.version = get16(this.tmpbuf, 4);
    e.flag = get16(this.tmpbuf, 6);

    e.method = get16(this.tmpbuf, 8);
    e.time = get32(this.tmpbuf, 10);
    if ((e.flag & 0x8) == 8)
    {
      if (e.method != 8)
        throw new ZipException(
          "only DEFLATED entries can have EXT descriptor");
    }
    else {
      e.crc = get32(this.tmpbuf, 14);
      e.csize = get32(this.tmpbuf, 18);
      e.size = get32(this.tmpbuf, 22);
    }
    len = get16(this.tmpbuf, 28);
    if (len > 0) {
      byte[] bb = new byte[len];
      readFully(bb, 0, len);
      e.extra = bb;
    }

    if (this.password != null) {
      byte[] extaData = new byte[12];
      readFully(extaData, 0, 12);
      ZipCrypto.InitCipher(this.password);
      extaData = ZipCrypto.DecryptMessage(extaData, 12);
      if (extaData[11] != (byte)(int)(e.crc >> 24 & 0xFF)) {
        if ((e.flag & 0x8) != 8)
          throw new ZipException("The password did not match.");
        if (extaData[11] != (byte)(int)(e.time >> 8 & 0xFF)) {
          throw new ZipException("The password did not match.");
        }
      }
    }
    return e;
  }

  private static String getUTF8String(byte[] b, int off, int len)
  {
    int count = 0;
    int max = off + len;
    int i = off;
    while (i < max) {
      int c = b[(i++)] & 0xFF;
      switch (c >> 4)
      {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        count++;
        break;
      case 12:
      case 13:
        if ((b[(i++)] & 0xC0) != 128) {
          throw new IllegalArgumentException();
        }
        count++;
        break;
      case 14:
        if (((b[(i++)] & 0xC0) != 128) || 
          ((b[(i++)] & 0xC0) != 128)) {
          throw new IllegalArgumentException();
        }
        count++;
        break;
      case 8:
      case 9:
      case 10:
      case 11:
      default:
        throw new IllegalArgumentException();
      }
    }
    if (i != max) {
      throw new IllegalArgumentException();
    }

    char[] cs = new char[count];
    i = 0;
    while (off < max) {
      int c = b[(off++)] & 0xFF;
      switch (c >> 4)
      {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        cs[(i++)] = (char)c;
        break;
      case 12:
      case 13:
        cs[(i++)] = (char)((c & 0x1F) << 6 | b[(off++)] & 0x3F);
        break;
      case 14:
        int t = (b[(off++)] & 0x3F) << 6;
        cs[(i++)] = (char)((c & 0xF) << 12 | t | b[(off++)] & 0x3F);
        break;
      case 8:
      case 9:
      case 10:
      case 11:
      default:
        throw new IllegalArgumentException();
      }
    }
    return new String(cs, 0, count);
  }

  protected EncryptZipEntry createZipEntry(String name)
  {
    return new EncryptZipEntry(name);
  }

  private void readEnd(EncryptZipEntry e)
    throws IOException
  {
    int n = this.inf.getRemaining();
    if (n > 0) {
      ((PushbackInputStream)this.in).unread(this.buf, this.len - n, n);
    }
    if ((e.flag & 0x8) == 8)
    {
      readFully(this.tmpbuf, 0, 16);
      long sig = get32(this.tmpbuf, 0);
      if (sig != 134695760L) {
        e.crc = sig;
        e.csize = get32(this.tmpbuf, 4);
        e.size = get32(this.tmpbuf, 8);
        ((PushbackInputStream)this.in).unread(this.tmpbuf, 11, 
          4);
      } else {
        e.crc = get32(this.tmpbuf, 4);
        e.csize = get32(this.tmpbuf, 8);
        if (e.flag == 9)
          e.csize -= 12L;
        e.size = get32(this.tmpbuf, 12);
      }
    }
    if (e.size != this.inf.getBytesWritten()) {
      throw new ZipException("invalid entry size (expected " + e.size + 
        " but got " + this.inf.getBytesWritten() + " bytes)");
    }
    if (e.csize != this.inf.getBytesRead()) {
      throw new ZipException("invalid entry compressed size (expected " + 
        e.csize + " but got " + this.inf.getBytesRead() + " bytes)");
    }
    if ((e.crc & 0xffffffffL) != this.crc.getValue())
      throw new ZipException("invalid entry CRC (expected 0x" + 
        Long.toHexString(e.crc) + " but got 0x" + 
        Long.toHexString(this.crc.getValue()) + ")");
  }

  private void readFully(byte[] b, int off, int len)
    throws IOException
  {
    while (len > 0) {
      int n = this.in.read(b, off, len);
      if (n == -1) {
        throw new EOFException();
      }
      off += n;
      len -= n;
    }
  }

  private static final int get16(byte[] b, int off)
  {
    return b[off] & 0xFF | (b[(off + 1)] & 0xFF) << 8;
  }

  private static final long get32(byte[] b, int off)
  {
    return get16(b, off) | get16(b, off + 2) << 16;
  }
}