package example;

import example.domain.game.Location;

import java.util.ArrayList;

import java.util.List;

import static example.SubFunctions.getNeighbors;

public class DistanceFromPlayer {
    public static int getDistanceFromPlayer(char[][] map, Location goldLocation) {
        int index = 0;
        //two lists initialization - first with Locations, second with distance
        List<Location> positionList = new ArrayList<>();
        List<Integer> distance = new ArrayList<>();
        positionList.add(goldLocation);
        distance.add(0);
        int distanceFromPlayer = 0;

        boolean isPlayer = false;

        while (!isPlayer) { //break if Player found
            Location currentLocation = positionList.get(index);
            List<Location> neighbors = getNeighbors(currentLocation, map);

            for (Location neighbor : neighbors) {
                if(!positionList.contains(neighbor)) {
                    char cell = map[neighbor.row()][neighbor.column()];

                    positionList.add(neighbor);
                    distance.add(distance.get(index) + 1);

                    if(cell == 'P') {
                        isPlayer = true;
                        distanceFromPlayer = distance.get(index) + 1;
                    }
                }
            }
            index++;
        }
        return distanceFromPlayer;
    }
}
