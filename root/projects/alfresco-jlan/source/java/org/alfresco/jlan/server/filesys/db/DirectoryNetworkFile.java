/*
 * Copyright (C) 2006-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.jlan.server.filesys.db;

import java.io.IOException;

import org.alfresco.jlan.server.filesys.cache.FileState;

/**
 * Directory Network File Class
 * 
 * <p>The directory network file is used by file loader implementations that only store file data and do not
 * store the filesystem structure.
 *
 * @author gkspencer
 */
public class DirectoryNetworkFile extends DBNetworkFile {

	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param fid int
	 * @param did int
	 */
	public DirectoryNetworkFile(String name, int fid, int did) {
		super(name,fid,0,did);
	}
	
	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param fid int
	 * @param did int
	 * @param state FileState
	 */
	public DirectoryNetworkFile(String name, int fid, int did, FileState state) {
		super(name,fid,0,did);
		setFileState(state);
	}
	
	/**
	 * Open the file
	 * 
	 * @param createFlag boolean
	 * @exception IOException
	 */
	public void openFile(boolean createFlag)
		throws IOException {
	}

	/**
   * Read from the file.
   *
   * @param buf byte[]
   * @param len int
   * @param pos int
   * @param fileOff int
   * @return     Length of data read.
   * @exception IOException
	 */
	public int readFile(byte[] buf, int len, int pos, long fileOff)
		throws IOException {
		return 0;
	}

	/**
   * Write a block of data to the file.
   *
   * @param buf byte[]
   * @param len int
   * @param pos int
   * @param fileOff long
   * @exception IOException
	 */
	public void writeFile(byte[] buf, int len, int pos, long fileOff)
		throws IOException {
	}

	/**
	 * Flush any buffered output to the file
	 * 
	 * @throws IOException
	 */
	public void flushFile()
		throws IOException {
	}
		
	/**
   * Seek to the specified file position.
   *
   * @param pos long
   * @param typ int
   * @return long
   * @exception IOException
	 */
	public long seekFile(long pos, int typ)
		throws IOException {
		return 0L;
	}

	/**
	 * Truncate the file to the specified file size
	 * 
	 * @param siz long
	 * @exception IOException
	 */
	public void truncateFile(long siz)
		throws IOException {
	}

	/**
	 * Close the file
	 */
	public void closeFile() {
	}
}
