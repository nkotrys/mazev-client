package example;

import example.domain.game.Direction;
import example.domain.game.Location;

import java.util.ArrayList;
import java.util.List;

public class SubFunctions {
    public static List<Location> getNeighbors(Location startLocation, char[][] map){
        List<Location> neighboursLocations = new ArrayList<>();
        Location currentLocation = new Location(startLocation.row(),startLocation.column()+1);
        if(map[currentLocation.row()][currentLocation.column()] !='x'){
            neighboursLocations.add(currentLocation);
        }
        currentLocation = new Location(startLocation.row(),startLocation.column()-1);
        if(map[currentLocation.row()][currentLocation.column()] !='x'){
            neighboursLocations.add(currentLocation);
        }
        currentLocation = new Location(startLocation.row()+1,startLocation.column());
        if(map[currentLocation.row()][currentLocation.column()] !='x'){
            neighboursLocations.add(currentLocation);
        }
        currentLocation = new Location(startLocation.row()-1,startLocation.column());
        if(map[currentLocation.row()][currentLocation.column()] !='x'){
            neighboursLocations.add(currentLocation);
        }
        return neighboursLocations;
    }


    public static Direction getDirection(Location startLocation, Location endLocation){
        if(endLocation.column() == startLocation.column() + 1){
            return Direction.Right;
        }
        else if(endLocation.column() == startLocation.column() - 1){
            return Direction.Left;
        }
        else if(endLocation.row() == startLocation.row() - 1){
            return Direction.Up;
        }
        else{
            return Direction.Down;
        }

    }
}
