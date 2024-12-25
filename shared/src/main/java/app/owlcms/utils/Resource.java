/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class Resource implements Comparable<Resource> {

    String fileName;
    Path filePath;
	Logger logger = (Logger) LoggerFactory.getLogger(Resource.class);

    public Resource(String fileName, Path filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    @Override
    public int compareTo(Resource o) {
        return ObjectUtils.compare(toString(), o.toString());
    }

    public byte[] getByteArray() throws IOException {
        return IOUtils.toByteArray(getStream());
    }

    public String getFileName() {
        return fileName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public InputStream getStream() throws IOException {
        return Files.newInputStream(filePath);
    }

    @Override
    public String toString() {
        return getFileName();
    }
    
    public String normalizedName() {
        String string = fileName != null ? fileName.replace('\\', '/') : null;
		return string;
    }
}
