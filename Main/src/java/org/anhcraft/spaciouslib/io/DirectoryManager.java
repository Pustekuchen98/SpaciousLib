package org.anhcraft.spaciouslib.io;

import java.io.File;

/**
 * A class helps you to manage a specified directory
 */
public class DirectoryManager {
    private File directory;

    /**
     * Creates a new DirectoryManager instance
     * @param directory represents the directory as a File object
     */
    public DirectoryManager(File directory){
        this.directory = directory;
    }

    /**
     * Creates a new DirectoryManager instance
     * @param path the path of the directory
     */
    public DirectoryManager(String path){
        this.directory = new File(path);
    }

    /**
     * Creates the specified directory and its parent directories if them doesn't exist
     * @return this object
     */
    public DirectoryManager mkdirs(){
        if(!this.directory.exists()){
            this.directory.mkdirs();
        }
        return this;
    }

    /**
     * Deletes the specified directory if it exists
     * @return this object
     */
    public DirectoryManager delete(){
        if(this.directory.exists()){
            this.directory.delete();
        }
        return this;
    }
}
