/* Copyright (c) 2007-2015 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import java.io.*;
import java.net.*;
import java.util.*;

import minesweeper.Board;

/**
 * Multiplayer Minesweeper server.
 */
public class MinesweeperServer {

    // System thread safety argument
    //   Considering the system a whole, the server initializes one instance of Board and the thread safety of part of the 
    //   system depends on the thread safety of Board. Since board is a thread-safe type, the only thing left to consider are the methods
    //   that access non constant rep fields (systemBoard and numberOfUsers). numberOfUsers  only updates upon the connection or disconnection
    //   of a client. This happens upon the creation of the thread. Hence, no concurrency issues. Finally, systemBoard field can only be 
    //   modified in handlerequest which is synchronized with the object systemBoard as a lock. Hence, all handling of requests are atomic.
    private static int numberOfUsers=0;
    private static  Board systemBoard;
    /** Default server port. */
    private static final int DEFAULT_PORT = 4444;
    /** Maximum port number as defined by ServerSocket. */
    private static final int MAXIMUM_PORT = 65535;
    /** Default square board size. */
    private static final int DEFAULT_SIZE = 10;

    /** Socket for receiving incoming connections. */
    private final ServerSocket serverSocket;
    /** True if the server should *not* disconnect a client after a BOOM message. */
    private final boolean debug;

    //Abstraction function, rep invariant, rep exposure
    //Abstraction Function
    // Maps number of users and a systemBoard to a minesweeper game with specific number of players.
    //Representation invariant
    //number of users must be >=0. Since, the adding and removal happen per thread creation and destruction. It's 
    // not possible to have <0 number of users. Check rep not needed.
    //Rep exposure
    //No representation exposure since no method returns numberOfUsers nor systemBoard. Notice that numberOfUsers
    // is immutable (primitive) and systemBoard is a mutable field, but its rep is never leaked (never returned) nor modified in this class.

    /**
     * Make a MinesweeperServer that listens for connections on port.
     * 
     * @param port port number, requires 0 <= port <= 65535
     * @param debug debug mode flag
     * @throws IOException if an error occurs opening the server socket
     */
    public MinesweeperServer(int port, boolean debug) throws IOException {
        serverSocket = new ServerSocket(port);
        this.debug = debug;
    }

    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {

        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();
            numberOfUsers++;
            System.out.println("connected with a client");
            new Thread(new Runnable(){
                public void run(){
                    try {
                        handleConnection(socket);
                    } catch (IOException ioe) {
                        ioe.printStackTrace(); // but don't terminate serve()
                    } finally {
                        try {
                            socket.close();
                            numberOfUsers--;
                            System.out.println("disconnected with a client");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();    
        }  


    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            try {
                out.println("Welcome to Minesweeper. Board: "+systemBoard.getX()+" columns by "+systemBoard.getY()+" rows. Players: "+numberOfUsers+" including you. Type 'help' for help.");
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    String output = handleRequest(line);

                    if (output != null) {
                        if(output.equals("bye")){
                            socket.close();
                        }
                        else{
                            if(output.equals("BOOM!") && debug){
                                out.println(output);
                            }
                            if (output.equals("BOOM!") && !debug){
                                out.println(output);
                                socket.close();
                            }
                            if (!output.equals("BOOM!")){
                                out.println(output);
                            }

                        }


                    }
                }
            } finally {
                out.close();
                in.close();
            }
        

    }

    /**
     * Handler for client input, performing requested operations and returning an output message.
     * 
     * @param input message from client
     * @return message to client, or null if none
     */
    private String handleRequest(String input) {
        synchronized (systemBoard){
            String regex = "(look)|(help)|(bye)|"
                    + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
            if ( ! input.matches(regex)) {
                System.out.println(input);
                String help="Yo you need help?,use the command: help,look,bye,dig,flag,deflag\n";
                return help;
            }
            String[] tokens = input.split(" ");
            if (tokens[0].equals("look")) {
                return systemBoard.toString();
            } else if (tokens[0].equals("help")) {
                // 'help' request
                String help="Yo you need help?,use the command: help,look,bye,dig,flag,deflag";
                return help;
            } else if (tokens[0].equals("bye")) {
                return "bye";
            } else {
                int x = Integer.parseInt(tokens[1]);
                int y = Integer.parseInt(tokens[2]);
                if (tokens[0].equals("dig")) {
                    if (!systemBoard.isTouched(x, y)){
                        if (systemBoard.hasBomb(x, y))
                        {
                            systemBoard.dig(x,y);
                            return "BOOM!";
                        }
                        else{
                            systemBoard.dig(x, y);
                            return systemBoard.toString();
                        }     
                    }
                    else{
                        return systemBoard.toString();
                    }



                } else if (tokens[0].equals("flag")) {
                    systemBoard.flag(x, y);
                    return systemBoard.toString();
                } else if (tokens[0].equals("deflag")) {
                    systemBoard.deflag(x, y);
                    return systemBoard.toString();
                }
            }
            throw new UnsupportedOperationException();   
        }

    }

    /**
     * Start a MinesweeperServer using the given arguments.
     * 
     * <br> Usage:
     *      MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]
     * 
     * <br> The --debug argument means the server should run in debug mode. The server should disconnect a
     *      client after a BOOM message if and only if the --debug flag was NOT given.
     *      Using --no-debug is the same as using no flag at all.
     * <br> E.g. "MinesweeperServer --debug" starts the server in debug mode.
     * 
     * <br> PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the server
     *      should be listening on for incoming connections.
     * <br> E.g. "MinesweeperServer --port 1234" starts the server listening on port 1234.
     * 
     * <br> SIZE_X and SIZE_Y are optional integer arguments specifying that a random board of size
     *      SIZE_X*SIZE_Y should be generated.
     * <br> E.g. "MinesweeperServer --size 42,58" starts the server initialized with a random board of size
     *      42*58.
     * 
     * <br> FILE is an optional argument specifying a file pathname where a board has been stored. If this
     *      argument is given, the stored board should be loaded as the starting board.
     * <br> E.g. "MinesweeperServer --file boardfile.txt" starts the server initialized with the board stored
     *      in boardfile.txt.
     * 
     * <br> The board file format, for use with the "--file" option, is specified by the following grammar:
     * <pre>
     *   FILE ::= BOARD LINE+
     *   BOARD ::= X SPACE Y NEWLINE
     *   LINE ::= (VAL SPACE)* VAL NEWLINE
     *   VAL ::= 0 | 1
     *   X ::= INT
     *   Y ::= INT
     *   SPACE ::= " "
     *   NEWLINE ::= "\r?\n"
     *   INT ::= [0-9]+
     * </pre>
     * 
     * <br> If neither --file nor --size is given, generate a random board of size 10x10.
     * 
     * <br> Note that --file and --size may not be specified simultaneously.
     * 
     * @param args arguments as described
     */
    public static void main(String[] args) {
        // Command-line argument parsing is provided. Do not change this method.
        boolean debug = false;
        int port = DEFAULT_PORT;
        int sizeX = DEFAULT_SIZE;
        int sizeY = DEFAULT_SIZE;
        Optional<File> file = Optional.empty();

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--debug")) {
                        debug = true;
                    } else if (flag.equals("--no-debug")) {
                        debug = false;
                    } else if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > MAXIMUM_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else if (flag.equals("--size")) {
                        String[] sizes = arguments.remove().split(",");
                        sizeX = Integer.parseInt(sizes[0]);
                        sizeY = Integer.parseInt(sizes[1]);
                        file = Optional.empty();
                    } else if (flag.equals("--file")) {
                        sizeX = -1;
                        sizeY = -1;
                        file = Optional.of(new File(arguments.remove()));
                        if ( ! file.get().isFile()) {
                            throw new IllegalArgumentException("file not found: \"" + file + "\"");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]");
            return;
        }

        try {
            runMinesweeperServer(debug, file, sizeX, sizeY, port);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Start a MinesweeperServer running on the specified port, with either a random new board or a
     * board loaded from a file.
     * 
     * @param debug The server will disconnect a client after a BOOM message if and only if debug is false.
     * @param file If file.isPresent(), start with a board loaded from the specified file,
     *             according to the input file format defined in the documentation for main(..).
     * @param sizeX If (!file.isPresent()), start with a random board with width sizeX.
     * @param sizeY If (!file.isPresent()), start with a random board with height sizeY.
     * @param port The network port on which the server should listen.
     * @throws IOException if a network error occurs
     */
    public static void runMinesweeperServer(boolean debug, Optional<File> file, int sizeX, int sizeY, int port) throws IOException {
        
        if (file.isPresent()){
            Board board= new Board(file.get());
            systemBoard=board;
        }
        else{
            if (sizeX>0 && sizeY>0)
            {
                Board board= new Board(sizeX,sizeY);
                systemBoard=board;
            }
            else{
                Board board= new Board(DEFAULT_SIZE,DEFAULT_SIZE);
                systemBoard=board;
            }
        }
        MinesweeperServer server = new MinesweeperServer(port, debug);
        server.serve();
        
    }
    
    
    
    
}
