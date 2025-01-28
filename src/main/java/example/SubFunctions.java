package example;

import example.domain.Response;
import example.domain.game.Direction;
import example.domain.game.Item;
import example.domain.game.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubFunctions {
    public static List<Location> getNeighbors(Location startLocation, char[][] map, int skipChecking){
        List<Location> neighboursLocations = new ArrayList<>();

        if(skipChecking == 0){
            Location currentLocation = new Location(startLocation.row(),startLocation.column()+1);
            if(map[currentLocation.row()][currentLocation.column()] !='x' && map[currentLocation.row()][currentLocation.column()+1] != 'P'
            && map[currentLocation.row()-1][currentLocation.column()] != 'P' &&  map[currentLocation.row()+1][currentLocation.column()] != 'P' ){
                neighboursLocations.add(currentLocation);
            }
            currentLocation = new Location(startLocation.row(),startLocation.column()-1);
            if(map[currentLocation.row()][currentLocation.column()] !='x' && map[currentLocation.row()][currentLocation.column()-1] != 'P'
                    && map[currentLocation.row()-1][currentLocation.column()] != 'P' &&  map[currentLocation.row()+1][currentLocation.column()] != 'P' ){
                neighboursLocations.add(currentLocation);
            }
            currentLocation = new Location(startLocation.row()+1,startLocation.column());
            if(map[currentLocation.row()][currentLocation.column()] !='x' && map[currentLocation.row()][currentLocation.column()+1] != 'P'
                    && map[currentLocation.row()][currentLocation.column()-1] != 'P' &&  map[currentLocation.row()+1][currentLocation.column()] != 'P' ){
                neighboursLocations.add(currentLocation);
            }
            currentLocation = new Location(startLocation.row()-1,startLocation.column());
            if(map[currentLocation.row()][currentLocation.column()] !='x' && map[currentLocation.row()][currentLocation.column()+1] != 'P'
                    && map[currentLocation.row()-1][currentLocation.column()] != 'P' &&  map[currentLocation.row()][currentLocation.column()-1] != 'P' ){
                neighboursLocations.add(currentLocation);
            }
            return neighboursLocations;
        }

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

    public static int getItemValue(Collection<Response.StateLocations.ItemLocation> itemLocation, int row, int column){
        for (Response.StateLocations.ItemLocation item :itemLocation) {
            Item entity = item.entity();
            Location location = item.location();

            // Check if the entity is Gold and the location matches
            if (entity instanceof Item.Gold gold && location.row() == row && location.column() == column) {
                return gold.value();
            }
            else if (entity instanceof Item.Health health && location.row() == row && location.column() == column) {
                return health.value();
            }
        }
        return 0;
    }


}
