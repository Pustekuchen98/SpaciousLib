package org.anhcraft.spaciouslib.database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Database extends Database{
    /**
     * Creates a new connection for a H2 database using the Embedded Mode
     * @param file the database file
     * @param user the username of the user
     * @param pass the password of the user
     */
    public void connectFile(File file, String user, String pass) throws SQLException, ClassNotFoundException {
        if(conn == null && state == null){
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:file:"+file.getAbsolutePath(), user, pass);
            state = conn.createStatement();
        }
    }

    /**
     * Creates a new connection for a H2 database using the Embedded Mode<br>
     * Uses "~/" to point to the default user home directory<br>
     * Uses a relative path to point to the current working directory
     * @param path the path of the database file
     * @param user the name of the user
     * @param pass the password of the user
     */
    public void connectFile(String path, String user, String pass) throws SQLException, ClassNotFoundException {
        if(conn == null && state == null){
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:file:"+path, user, pass);
            state = conn.createStatement();
        }
    }

    /**
     * Creates a new connection for a H2 In-Memory database
     * @param database the name of the database
     */
    public void connectMemory(String database, String user, String pass) throws SQLException, ClassNotFoundException {
        if(conn == null && state == null){
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:mem:"+database);
            state = conn.createStatement();
        }
    }

    /**
     * Creates a new connection for a H2 database using the Server Mode
     * @param file the database file in the remote server
     * @param user the username of the user
     * @param pass the password of the user
     * @param host the IP address or hostname of the remote server
     * @param port the TCP/IP port which is listening by the remote server
     */
    public void connectRemoteFile(File file, String user, String pass, String host, int port) throws SQLException, ClassNotFoundException {
        if(conn == null && state == null){
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:tcp://"+host+":"+port+"/"+file.getAbsolutePath(), user, pass);
            state = conn.createStatement();
        }
    }

    /**
     * Creates a new connection for a H2 database using the Server Mode<br>
     * Uses "~/" to point to the default user home directory in the remote server
     * @param path the path of the database file
     * @param user the name of the user
     * @param pass the password of the user
     * @param host the IP address or hostname of the remote server
     * @param port the TCP/IP port which is listening by the remote server
     */
    public void connectRemoteFile(String path, String user, String pass, String host, int port) throws SQLException, ClassNotFoundException {
        if(conn == null && state == null){
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:tcp://"+host+":"+port+"/"+path, user, pass);
            state = conn.createStatement();
        }
    }

    /**
     * Creates a new connection for a H2 In-Memory database in the remote server
     * @param database the name of the database
     * @param host the IP address or hostname of the remote server
     * @param port the TCP/IP port which is listening by the remote server
     */
    public void connectRemoteMemory(String database, String host, int port) throws SQLException, ClassNotFoundException {
        if(conn == null && state == null){
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:tcp://"+host+":"+port+"/mem:"+database);
            state = conn.createStatement();
        }
    }
}
