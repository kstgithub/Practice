package com.kst.funddemo.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 全局共用的对文件操作的方法
 * 
 */
public class ToolsFile {

	/**
	 * Inputstream->byte[]方法
	 * 
	 * @param inputstream
	 * @return
	 * @throws IOException
	 */
	public static final byte[] readBytes(InputStream inputstream)
			throws IOException {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		byte bys[] = null;
		try {
			byte abyte0[] = new byte[1024];
			int readlength;
			while (-1 != (readlength = inputstream.read(abyte0))) {
				bytearrayoutputstream.write(abyte0, 0, readlength);
			}
			bys = bytearrayoutputstream.toByteArray();
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			bytearrayoutputstream.close();
		}
		return bys;
	}
	
}
