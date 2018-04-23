/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MalformedObjectNameException;
import net.server.Server;
import server.maps.MapleMap;

/**
 *
 * @author Administrator
 */
public class Debugger {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.rmi.NotBoundException
     * @throws javax.management.InstanceAlreadyExistsException
     * @throws javax.management.MalformedObjectNameException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, NotBoundException, InstanceAlreadyExistsException, MalformedObjectNameException {

        System.setProperty("wzpath", "wz\\");
       //Shaikat and Omar 
       Server.main(args);

    }

}
