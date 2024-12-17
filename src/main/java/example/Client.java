package example;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.domain.Request;
import example.domain.Response;
import example.domain.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Client {
//    private static final String HOST = "35.208.184.138";
    private static final String HOST = "35.208.184.138";
    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        new Client().startClient();
    }

    public void startClient() {
        try (final var socket = new Socket(HOST, PORT);
             final var is = socket.getInputStream();
             final var isr = new InputStreamReader(is);
             final var reader = new BufferedReader(isr);
             final var os = socket.getOutputStream();
             final var osr = new OutputStreamWriter(os);
             final var writer = new BufferedWriter(osr)) {
            logger.info("Connected to server at {}:{}", HOST, PORT);

            {
                final var json = objectMapper.writeValueAsString(new Request.Authorize("5748"));
                writer.write(json);
                writer.newLine();
                writer.flush();
                logger.info("Sent command: {}", json);
            }

            Cave cave = null;
            Player player = null;
            Collection<Response.StateLocations.ItemLocation> itemLocations;
            Collection<Response.StateLocations.PlayerLocation> playerLocations;
            char[][] map = new char[0][0];

            while (!Thread.currentThread().isInterrupted()) {
                final var line = reader.readLine();
                if (line == null) {
                    break;
                }



                final var response = objectMapper.readValue(line, Response.class);
                switch (response) {
                    case Response.Authorized authorized -> {
                        player = authorized.humanPlayer();
                        logger.info("authorized: {}", authorized);
                    }
                    case Response.Unauthorized unauthorized -> {
                        logger.error("unauthorized: {}", unauthorized);
                        return;
                    }
                    case Response.StateCave stateCave -> {
                        cave = stateCave.cave();
                        logger.info("cave: {}", cave);
                        map = new char[cave.rows()][cave.columns()];
                        for (int i = 0; i < map.length; i++) {
                            for (int j = 0; j < map[0].length; j++) {
                                if(cave.rock(i,j)){
                                    map[i][j] = 'x';
                                }
                                else{
                                    map[i][j]= ' ';
                                }
                            }
                        }
                    }
                    case Response.StateLocations stateLocations -> {
                        itemLocations = stateLocations.itemLocations();
                        playerLocations = stateLocations.playerLocations();
                        logger.info("itemLocations: {}", itemLocations);
                        logger.info("playerLocations: {}", playerLocations);
                        for (int i = 0; i < map.length; i++) {
                            for (int j = 0; j < map[0].length; j++) {
                                if(map[i][j] != 'x'){
                                    var item = isItem(itemLocations,i,j);
                                    if(item != ' ' ){
                                        map[i][j] = item;
                                    }
                                    else if(isPlayer(playerLocations,i,j, player) != ' '){
                                        map[i][j] = isPlayer(playerLocations,i,j, player);
                                    }
                                    else{
                                        map[i][j] = ' ';
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < map.length; i++) {
                            for (int j = 0; j < map[0].length; j++) {
                                System.out.print(map[i][j]);
                                }
                            System.out.println();
                            }
                        Location myLocation = myPlayerLocation(playerLocations,player);

                        final var cmd =  new Request.Command(dijkstry(map,myLocation, cave.rows(), cave.columns())); //new Request.Command(Direction.Up);
                        final var cmdJson = objectMapper.writeValueAsString(cmd);
                        writer.write(cmdJson);
                        writer.newLine();
                        writer.flush();
                        logger.info("Sent command: {}", cmd);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error in client operation", e);
        } finally {
            logger.info("Client exiting");
        }
    }

    public char isItem(Collection<Response.StateLocations.ItemLocation> itemLocation ,int row, int column){
        Location position;
        for(Response.StateLocations.ItemLocation item :itemLocation){
            position = item.location();
            if(position.row() == row && position.column() == column){
                switch (item.entity()) {
                    case Item.Gold gold -> {
                        return 'G';
                    }
                    case  Item.Health health-> {
                        return 'H';
                    }
                }
            }
        }
        return ' ';
    }

    public char isPlayer(Collection<Response.StateLocations.PlayerLocation> playerLocation ,int row, int column, Player myPlayer){
        Location position;
        for(Response.StateLocations.PlayerLocation singlePlayer : playerLocation){
            position = singlePlayer.location();
            if(position.row() == row && position.column() == column){
                if(singlePlayer.entity().equals(myPlayer)){
                    return 'O';
                }
                return 'P';
            }
        }
        return ' ';
    }

    public Location myPlayerLocation(Collection<Response.StateLocations.PlayerLocation> playerLocation, Player myPlayer){
        Location position = null;
        for(Response.StateLocations.PlayerLocation singlePlayer : playerLocation) {
            position = singlePlayer.location();
            if (singlePlayer.entity().equals(myPlayer)) {
                return position;
            }
        }
        return position;
    }

    public Direction dijkstry(char[][] map, Location startLocation, int caveRows, int caveColumns){
        int index = 0;
        List<Location> positionList = new ArrayList<Location>();
        positionList.add(startLocation);
        List<Integer> previousIndex = new ArrayList<Integer>();
        previousIndex.add(-1);
        Location finalLocation = null;
        boolean isGold = false;
        List<Location> neighbors = new ArrayList<>();
        List<Location> foundLocations = new ArrayList<>();
        int backIndex = -1;
        //list of already found locations
        //foundLocations.add(startLocation);

        while(!isGold){
            neighbors = getNeighbors(positionList.get(index));
            outer: for(Location currentLocation : neighbors){
                //if(!foundLocations.contains(currentLocation)) {
                if(!positionList.contains(currentLocation) && currentLocation.row() < caveRows && 0 < currentLocation.row()  && currentLocation.column() < caveColumns && 0 < currentLocation.column() ) {
                    switch (map[currentLocation.row()][currentLocation.column()]) {

                        case ' ' -> {
                            addLocation(currentLocation, index, positionList, previousIndex, foundLocations);
                            System.out.println("empty");
                            System.out.println(isGold);
                        }
                        case 'G' -> {
                            System.out.println("gold");
                            System.out.println(isGold);
                            backIndex = index;
                            finalLocation = currentLocation;
                            isGold = true;
                            break outer;
                        }
                        case 'x' -> {
                            System.out.println("x");
                            System.out.println(isGold);
                            addLocation(currentLocation, -1, positionList, previousIndex, foundLocations);
                            break;
                        }
                    }
                }
                index++;
            }
        }
        if(backIndex == 0){
            return getDirection(startLocation,finalLocation);
        }
        else{
            while(backIndex != 0){
                index = backIndex;
                backIndex = previousIndex.get(index);
            }
        }
        finalLocation = positionList.get(index);
        return getDirection(startLocation,finalLocation);

    }
    public List<Location> getNeighbors(Location startLocation){
        List<Location> neighboursLocations = new ArrayList<>();
        neighboursLocations.add(new Location(startLocation.row(),startLocation.column()+1));
        neighboursLocations.add(new Location(startLocation.row(),startLocation.column()-1));
        neighboursLocations.add(new Location(startLocation.row()+1,startLocation.column()));
        neighboursLocations.add(new Location(startLocation.row()-1,startLocation.column()));
        return neighboursLocations;
    }

    public void addLocation(Location locationToAdd, Integer previousLocationIndex, List<Location> positionList, List<Integer> previousIndex, List<Location> foundLocations){
        positionList.add(locationToAdd);
        previousIndex.add(previousLocationIndex);
        //foundLocations.add(locationToAdd);

    }
    public Direction getDirection(Location startLocation, Location endLocation){
        if(endLocation.column() == startLocation.column() + 1){
            return Direction.Right;
        }
        else if(endLocation.column() == startLocation.column() - 1){
            return Direction.Left;
        }
        else if(endLocation.row() == startLocation.row() - 1){
            return Direction.Down;
        }
        else{
            return Direction.Up;
        }

    }

}