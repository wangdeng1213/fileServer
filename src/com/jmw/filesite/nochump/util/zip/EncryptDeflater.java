package com.jmw.filesite.nochump.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import com.jmw.filesite.nochump.util.extend.ZipCrypto;

public class EncryptDeflater extends FilterOutputStream
{
  protected Deflater def;
  protected byte[] buf;
  private boolean closed = false;

  boolean usesDefaultDeflater = false;

  protected String password = null;

  public EncryptDeflater(OutputStream out, Deflater def, int size)
  {
    super(out);
    if ((out == null) || (def == null))
      throw new NullPointerException();
    if (size <= 0) {
      throw new IllegalArgumentException("buffer size <= 0");
    }
    this.def = def;
    this.buf = new byte[size];
  }

  public EncryptDeflater(OutputStream out, Deflater def)
  {
    this(out, def, 512);
  }

  public EncryptDeflater(OutputStream out)
  {
    this(out, new Deflater());
    this.usesDefaultDeflater = true;
  }

  public void write(int b)
    throws IOException
  {
    byte[] buf = new byte[1];
    buf[0] = (byte)(b & 0xFF);
    write(buf, 0, 1);
  }

  public void write(byte[] b, int off, int len)
    throws IOException
  {
    if (this.def.finished()) {
      throw new IOException("write beyond end of stream");
    }
    if ((off | len | off + len | b.length - (off + len)) < 0)
      throw new IndexOutOfBoundsException();
    if (len == 0) {
      return;
    }
    if (!this.def.finished()) {
      this.def.setInput(b, off, len);
      while (!this.def.needsInput())
        deflate();
    }
  }

  public void finish()
    throws IOException
  {
    if (!this.def.finished()) {
      this.def.finish();
      while (!this.def.finished())
        deflate();
    }
  }

  public void close()
    throws IOException
  {
    if (!this.closed) {
      finish();
      if (this.usesDefaultDeflater)
        this.def.end();
      this.out.close();
      this.closed = true;
    }
  }

  protected void writeExtData(EncryptZipEntry entry) throws IOException
  {
    byte[] extData = new byte[12];
    ZipCrypto.InitCipher(this.password);
    for (int i = 0; i < 11; i++)
      extData[i] = (byte)Math.round(256.0F);
    extData[11] = (byte)(int)(entry.time >> 8 & 0xFF);
    extData = ZipCrypto.EncryptMessage(extData, 12);
    this.out.write(extData, 0, extData.length);
  }

  protected void deflate()
    throws IOException
  {
    int len = this.def.deflate(this.buf, 0, this.buf.length);
    if (len > 0) {
      if (this.password != null)
      {
        byte[] crypto = ZipCrypto.EncryptMessage(this.buf, len);
        this.out.write(crypto, 0, len);
        return;
      }
      this.out.write(this.buf, 0, len);
    }
  }
}