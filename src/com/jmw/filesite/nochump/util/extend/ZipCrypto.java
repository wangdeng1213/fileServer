package com.jmw.filesite.nochump.util.extend;

import com.jmw.filesite.nochump.util.zip.Crc32;

public class ZipCrypto
{
  private static long[] _Keys = { 305419896L, 591751049L, 878082192L };

  private static short MagicByte()
  {
    int t = (int)(_Keys[2] & 0xFFFF | 0x2);
    t = t * (t ^ 0x1) >> 8;
    return (short)t;
  }

  private static void UpdateKeys(short byteValue)
  {
    _Keys[0] = Crc32.update(_Keys[0], byteValue);
    short key0val = (byte)(int)_Keys[0];
    if ((byte)(int)_Keys[0] < 0) {
      key0val = (short)(key0val + 256);
    }
    _Keys[1] += key0val;
    _Keys[1] *= 134775813L;
    _Keys[1] += 1L;
    _Keys[2] = Crc32.update(_Keys[2], (byte)(int)(_Keys[1] >> 24));
  }

  public static void InitCipher(String passphrase)
  {
    _Keys[0] = 305419896L;
    _Keys[1] = 591751049L;
    _Keys[2] = 878082192L;
    for (int i = 0; i < passphrase.length(); i++)
      UpdateKeys((byte)passphrase.charAt(i));
  }

  public static byte[] DecryptMessage(byte[] cipherText, int length)
  {
    byte[] PlainText = new byte[length];
    for (int i = 0; i < length; i++)
    {
      short m = MagicByte();
      byte C = (byte)(cipherText[i] ^ m);
      if (C < 0) {
        UpdateKeys((short)((short)C + 256));
        PlainText[i] = (byte)(short)((short)C + 256);
      } else {
        UpdateKeys(C);
        PlainText[i] = C;
      }
    }
    return PlainText;
  }

  public static byte[] EncryptMessage(byte[] plaintext, int length)
  {
    byte[] CipherText = new byte[length];
    for (int i = 0; i < length; i++)
    {
      byte C = plaintext[i];
      CipherText[i] = (byte)(plaintext[i] ^ MagicByte());
      UpdateKeys(C);
    }
    return CipherText;
  }
}