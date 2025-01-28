package example;

import example.domain.game.Direction;
import example.domain.game.Location;

import java.util.ArrayList;
import java.util.List;

import static example.SubFunctions.getDirection;
import static example.SubFunctions.getNeighbors;

public class StrategyOld {
    public static Direction dijkstry(char[][] map, Location startLocation) {
        int index = 0;
        int backIndex = -1;
        //two lists initialization - first with Locations, second with index of previous element (the one from we came here
        List<Location> positionList = new ArrayList<>();
        List<Integer> previousIndex = new ArrayList<>();
        List<Integer> distance = new ArrayList<>();
        positionList.add(startLocation);
        previousIndex.add(-1);
        distance.add(0);


        Location finalLocation = null;
        Location finalGoldTempLocation = null;
        Location finalHealthLocation = null;
        boolean isGoldFound = false;
        boolean isGoldTempFound = false;
        boolean isHealthTempFound = false;
        int healthIndex = 0;
        int goldTempIndex = 0;

        while (!isGoldFound && index < positionList.size()) { //break if gold found or no more moves are possible
            Location currentLocation = positionList.get(index);
            List<Location> neighbors = getNeighbors(currentLocation, map, index);

            outer: for (Location neighbor : neighbors) {
                if(!positionList.contains(neighbor)) {
                    char cell = map[neighbor.row()][neighbor.column()];
                    switch (cell) {
                        case 'H' -> {
                            if(!isHealthTempFound){
                                healthIndex = index;
                                isHealthTempFound = true;
                                finalHealthLocation  = neighbor;
                            }
                            positionList.add(neighbor);
                            previousIndex.add(index);
                            distance.add(distance.get(index) + 1);

                        }
                        case ' ' -> {
                            positionList.add(neighbor);
                            previousIndex.add(index);
                            distance.add(distance.get(index) + 1);
                        }
                        case 'G' -> {
                            if(!isGoldTempFound){
                                goldTempIndex = index;
                                isGoldTempFound = true;
                                finalGoldTempLocation = neighbor;
                            }
                            if(distance.get(index) + 1 < DistanceFromEntity.getDistanceFromEntity(map, neighbor,'P')) {
                                distance.add(distance.get(index) + 1);
                                isGoldFound = true;
                                backIndex = index;
                                finalLocation = neighbor;
                                break outer;
                            }
                            else {
                                positionList.add(neighbor);
                                previousIndex.add(index);
                                distance.add(distance.get(index) + 1);
                            }

                        }
                    }
                }
            }
            index++;
        }

        if (!isGoldFound) {
            if(isHealthTempFound){
                backIndex = healthIndex;
                finalLocation = finalHealthLocation;
            }
            else if(isGoldTempFound){
                backIndex = goldTempIndex;
                finalLocation = finalGoldTempLocation;
            }
            else{
                return null;
            }
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