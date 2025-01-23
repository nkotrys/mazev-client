package example;

import example.domain.Response;
import example.domain.game.Direction;
import example.domain.game.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static example.SubFunctions.getDirection;
import static example.SubFunctions.getNeighbors;

public class Strategy {
    public static Direction dijkstry(char[][] map, Location startLocation, Collection<Response.StateLocations.ItemLocation> itemLocations) {
        int index = 0;
        int backIndex = -1;
        //two lists initialization - first with Locations, second with index of previous element (the one from where we came here)
        List<Location> positionList = new ArrayList<>();
        List<Integer> previousIndex = new ArrayList<>();
        List<Integer> distance = new ArrayList<>();
        List<Integer> numberOfGold = new ArrayList<>();  //number of gold to could get in this move
        List<Integer> numberOfHealth = new ArrayList<>();
        positionList.add(startLocation);
        previousIndex.add(-1);
        distance.add(0);
        numberOfGold.add(0);
        numberOfHealth.add(0);

        Location finalLocation = null;

        while (index < positionList.size()) { //break if gold found or no more moves are possible - UPDATE when no more moves are possible
            Location currentLocation = positionList.get(index);
            List<Location> neighbors = getNeighbors(currentLocation, map);

            for (Location neighbor : neighbors) {
                if(!positionList.contains(neighbor)) {
                    char cell = map[neighbor.row()][neighbor.column()];
                    switch (cell) {
                        case ' ' -> {
                            positionList.add(neighbor);
                            previousIndex.add(index);
                            distance.add(distance.get(index) + 1);
                            numberOfGold.add(numberOfGold.get(index));
                            numberOfHealth.add(numberOfHealth.get(index));
                        }
                        case 'G' -> {
                            positionList.add(neighbor);
                            previousIndex.add(index);
                            distance.add(distance.get(index) + 1);
                            numberOfHealth.add(numberOfHealth.get(index));
                            //add gold only if my player is the nearest one
                            if(distance.get(index) + 1 < DistanceFromPlayer.getDistanceFromPlayer(map, neighbor)) {
                                //numberOfGold.add(numberOfGold.get(index)+1);
                                numberOfGold.add(numberOfGold.get(index)+SubFunctions.getItemValue(itemLocations, neighbor.row(), neighbor.column())); //TODO: testing
                            }
                            else{
                                //numberOfGold.add(0);  //TODO: CHECK
                                numberOfGold.add(numberOfGold.get(index)+SubFunctions.getItemValue(itemLocations, neighbor.row(), neighbor.column())); //TODO: testing

                            }
                        }
                        case 'H' -> {
                            positionList.add(neighbor);
                            previousIndex.add(index);
                            distance.add(distance.get(index) + 1);
                            numberOfGold.add(numberOfGold.get(index));
                            //add health only if my player is the nearest one
                            if(distance.get(index) + 1 < DistanceFromPlayer.getDistanceFromPlayer(map, neighbor)) {
                                //numberOfHealth.add(numberOfHealth.get(index)+1);
                                numberOfHealth.add(numberOfHealth.get(index)+SubFunctions.getItemValue(itemLocations, neighbor.row(), neighbor.column())); //TODO: testing
                            }
                            else{
                                numberOfHealth.add(0);  //TODO: CHECK
                            }
                        }
                    }
                }
            }
            index++;
        }

        for (int i = 0; i < (long) numberOfGold.size(); i++) {
            System.out.print(numberOfGold.get(i)+", ");
        }
        //TODO: add some weight for health and also consider here
        int max = numberOfGold.getFirst();
        int maxIndex = 0;
        index = 0;
        for (Integer x : numberOfGold) {
            if (x > max) {
                max = x;
                maxIndex = index;
            }
            index++;
        }
        if(maxIndex == 0){
            index = 0;
            max = numberOfHealth.getFirst();
            for (Integer x : numberOfHealth) {
                if (x > max) {
                    max = x;
                    maxIndex = index;
                }
                index++;
            }
        }
        if (maxIndex == 0){//TODO:
            return null;
        }
        backIndex = previousIndex.get(maxIndex);
        finalLocation = positionList.get(maxIndex);
        if(backIndex == 0){
            System.out.println("move" + getDirection(startLocation,finalLocation));
            return getDirection(startLocation,finalLocation);
        }
        //back to the startLocation to determine the first move
        else{
            while(backIndex != 0){
                index = backIndex;
                backIndex = previousIndex.get(index);
            }
        }
        finalLocation = positionList.get(index);
        System.out.println("move" + getDirection(startLocation,finalLocation));
        return getDirection(startLocation,finalLocation);

    }
}
