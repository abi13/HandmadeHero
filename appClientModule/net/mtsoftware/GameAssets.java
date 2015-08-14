package net.mtsoftware;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 
 * @author mtutaj
 *
 */
public class GameAssets {

	public byte[] readEntireFile(String fileName) throws IOException {
		
		Path path = FileSystems.getDefault().getPath(fileName); 
		return Files.readAllBytes(path);
	}
	
	public void writeEntireFile(String fileName, byte[] data) throws IOException {
		Path path = FileSystems.getDefault().getPath(fileName); 
		Files.write(path, data);
	}
}
