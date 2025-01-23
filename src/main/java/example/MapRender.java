package example;

import example.domain.Response;
import example.domain.game.Item;
import example.domain.game.Location;
import example.domain.game.Player;

import java.util.Collection;

public class MapRender {
    public static char isItem(Collection<Response.StateLocations.ItemLocation> itemLocation , int row, int column){
        Location position;
        for(Response.StateLocations.ItemLocation item :itemLocation){
            position = item.location();
            if(position.row() == row && position.column() == column){
                switch (item.entity()) {
                    case Item.Gold ignored -> {
                        return 'G';
                        //return (char) SubFunctions.getGoldValue(itemLocation,position.row(),position.column() );
                    }
                    case  Item.Health ignored -> {
                        return 'H';
                    }
                }
            }
        }
        return ' ';
    }

    public static char isPlayer(Collection<Response.StateLocations.PlayerLocation> playerLocation ,int row, int column, Player myPlayer){
        Location position;
        for(Response.StateLocations.PlayerLocation singlePlayer : playerLocation){
            position = singlePlayer.location();
            if(position.row() == row && position.column() == column){
                if(singlePlayer.entity().equals(myPlayer)){
                    return 'O';
                }
                else if(singlePlayer.entity() instanceof Player.HumanPlayer){
                    return 'P';
                }
                return 'D';
            }
        }
        return ' ';
    }

    public static Location myPlayerLocation(Collection<Response.StateLocations.PlayerLocation> playerLocation, Player myPlayer){
        Location position = null;
        for(Response.StateLocations.PlayerLocation singlePlayer : playerLocation) {
            position = singlePlayer.location();
            if (singlePlayer.entity().equals(myPlayer)) {
                return position;
            }
        }
        return position;
    }
}
