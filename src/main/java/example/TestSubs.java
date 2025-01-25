
package example;
import example.domain.game.Direction;
import example.domain.game.Location;
import java.util.ArrayList;
import java.util.List;
import static example.SubFunctions.getDirection;
import static example.SubFunctions.getNeighbors;

public class TestSubs {


        public static Direction dijkstry(char[][] map, Location startLocation) {
            int index = 0;
            int backIndex = -1;
            //two lists initialization - first with Locations, second with index of previous element (the one from we came here
            List<Location> positionList = new ArrayList<>();
            List<Integer> previousIndex = new ArrayList<>();
            positionList.add(startLocation);
            previousIndex.add(-1);
            Location finalLocation = null;
            boolean isGoldFound = false;
            while (!isGoldFound && index < positionList.size()) { //break if gold found or no more moves are possible
                Location currentLocation = positionList.get(index);
                System.out.println(currentLocation);
                List<Location> neighbors = getNeighbors(currentLocation, map, index);
                outer: for (Location neighbor : neighbors) {
                    if(!positionList.contains(neighbor)) {
                        char cell = map[neighbor.row()][neighbor.column()];
                        switch (cell) {
                            case ' ' -> {
                                positionList.add(neighbor);
                                previousIndex.add(index);
                            }
                            case 'G' -> {
                                isGoldFound = true;
                                backIndex = index;
                                finalLocation = neighbor;
                                break outer;
                            }
                        }
                    }
                }
                index++;
            }
            //what should be done here? TODO
            if (!isGoldFound) {
                return Direction.Up;
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
