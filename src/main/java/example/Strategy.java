package example;

import example.domain.Response;
import example.domain.game.Direction;
import example.domain.game.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

        while (index < positionList.size() && distance.getLast() < 90) { //break if gold found or no more moves are possible - UPDATE when no more moves are possible
            Location currentLocation = positionList.get(index);
            List<Location> neighbors = getNeighbors(currentLocation, map, index);


            for (Location neighbor : neighbors) {
                if(!positionList.contains(neighbor)) {
                    char cell = map[neighbor.row()][neighbor.column()];
                    switch (cell) {
                        case ' '-> {
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
                            if(distance.get(index) + 1 < DistanceFromEntity.getDistanceFromEntity(map, neighbor,'P')) {
                                //numberOfGold.add(numberOfGold.get(index)+1);
                                numberOfGold.add(numberOfGold.get(index)+SubFunctions.getItemValue(itemLocations, neighbor.row(), neighbor.column())); //TODO: testing
                            }
                            else{
                                numberOfGold.add(1);
                                //numberOfGold.add(numberOfGold.get(index)+SubFunctions.getItemValue(itemLocations, neighbor.row(), neighbor.column())); //TODO: testing

                            }
                        }
                        case 'H' -> {
                            positionList.add(neighbor);
                            previousIndex.add(index);
                            distance.add(distance.get(index) + 1);
                            numberOfGold.add(numberOfGold.get(index));
                            //add health only if my player is the nearest one
                            if(distance.get(index) + 1 < DistanceFromEntity.getDistanceFromEntity(map, neighbor,'P')) {
                                //numberOfHealth.add(numberOfHealth.get(index)+1);
                                numberOfHealth.add(numberOfHealth.get(index)+SubFunctions.getItemValue(itemLocations, neighbor.row(), neighbor.column())); //TODO: testing
                            }
                            else{
                                numberOfHealth.add(0);
                            }


                        }
                    }
                }
            }
            index++;
        }

        /*for (int i = 0; i < (long) numberOfGold.size(); i++) {
            System.out.print(numberOfGold.get(i)+", ");
        }
        System.out.println("");
        for (int i = 0; i < (long) numberOfGold.size(); i++) {
            System.out.print(distance.get(i)+", ");
        }*/

        //no safe move
        if(positionList.size() == 1) {
            return null;
        }

        int maxGold = numberOfGold.getFirst();
        int maxIndexGold = 0;
        int maxHealth = numberOfHealth.getFirst();
        int maxIndexHealth = 0;

        index = 0;
        for (Integer x : numberOfGold) {
            if (x > maxGold) {
                maxGold = x;
                maxIndexGold = index;
            }
            index++;
        }
        //System.out.println("");
        //System.out.println("goldDistance"+distance.get(maxIndexGold));
        if(maxGold < 2){
            index = 0;
            maxHealth = numberOfHealth.getFirst();
            for (Integer x : numberOfHealth) {
                if (x > maxHealth) {
                    maxHealth = x;
                    maxIndexHealth = index;
                }
                index++;
            }
            System.out.println("healthDistance"+distance.get(maxIndexHealth));
        }
        System.out.println("healthDistance"+distance.get(maxIndexHealth));

        int maxIndex = 0;

        if(maxGold < 2 && maxHealth > 1){
            maxIndex = maxIndexHealth;
        }
        else{
            maxIndex = maxIndexGold;
        }


        backIndex = previousIndex.get(maxIndex);
        finalLocation = positionList.get(maxIndex);
        if(backIndex == -1){
            return null;
        }
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
