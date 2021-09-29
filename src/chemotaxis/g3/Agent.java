package chemotaxis.g3;

import java.util.Map;

import chemotaxis.sim.DirectionType;
import chemotaxis.sim.ChemicalCell;
import chemotaxis.sim.ChemicalCell.ChemicalType;
import chemotaxis.sim.Move;
import chemotaxis.sim.SimPrinter;

public class Agent extends chemotaxis.sim.Agent {

    /**
     * Agent constructor
     *
     * @param simPrinter  simulation printer
     *
     */
	public Agent(SimPrinter simPrinter) {
		super(simPrinter);
	}

    /**
     * Move agent
     *
     * @param randomNum        random number available for agents
     * @param previousState    byte of previous state
     * @param currentCell      current cell
     * @param neighborMap      map of cell's neighbors
     * @return                 agent move
     *
     */
	@Override
	public Move makeMove(Integer randomNum, Byte previousState, ChemicalCell currentCell, Map<DirectionType, ChemicalCell> neighborMap) {
		Move move = new Move();

		//see highest in hiarchy color is sees in its space or one immediately adjacent:
		//(highest) blue, green, red (lowest)
		//set that chemical to chosen chemical type
		/*
		store int in previousState as follows:
			- 0: previous was red, follow green
			- 1: previous was green, follow blue
			- 2: previous was blue, follow red (but reach local maximum first)
		 */
		ChemicalType highest_priority = ChemicalType.RED;

		// Check for local blue maximum condition



		if (previousState == 0) {
			highest_priority = ChemicalType.RED;
		} else if (previousState == 1) {
			highest_priority = ChemicalType.GREEN;
		} else if (previousState == 2) {
			highest_priority = ChemicalType.BLUE;
		}
		move.currentState = previousState;


		boolean max_reached = false;
		int max_count = 0;
		for (DirectionType directionType : neighborMap.keySet()) {
			if (neighborMap.get(directionType).getConcentration(highest_priority) <
					currentCell.getConcentration(highest_priority)) {
				max_count++;
			}
		}

		if (max_count == 4) {
			max_reached = true;
		}

		for(DirectionType directionType : neighborMap.keySet())
		{

			if(neighborMap.get(directionType).getConcentration(ChemicalType.BLUE) != 0 && previousState == 1
				&& max_reached) {

				highest_priority = ChemicalType.BLUE;
				move.currentState = (byte) 2;
				System.out.println("green to blue 2");
			}
			else if((neighborMap.get(directionType).getConcentration(ChemicalType.RED) != 0) && previousState == 2
					&& max_reached) {
				highest_priority = ChemicalType.RED;
				move.currentState = (byte) 0;
				System.out.println("blue to red 2");
			}
			else if((neighborMap.get(directionType).getConcentration(ChemicalType.GREEN) != 0) && previousState == 0
					&& max_reached) {

				highest_priority = ChemicalType.GREEN;
				move.currentState = (byte) 1;
				System.out.println("red to green 2");
			}
		}

		ChemicalType chosenChemicalType = highest_priority;



		//note: if all the concentrations are zero it will move in the direction
		//of the last direction iterated through
		double highestConcentration = currentCell.getConcentration(chosenChemicalType);
		int zero_count = 0;
		for (DirectionType directionType : neighborMap.keySet()) {
			if (neighborMap.get(directionType).getConcentration(chosenChemicalType) == 0) {
				zero_count++;
			}
			if (highestConcentration <= neighborMap.get(directionType).getConcentration(chosenChemicalType)) {
				highestConcentration = neighborMap.get(directionType).getConcentration(chosenChemicalType);
				move.directionType = directionType;
			}
		}

		/* all surrounding cells have no chemical
		Direction based on randNum%4
			- 0: right
			- 1: down
			- 2: left
			- 3: up
		*/
		if (zero_count == 4) {
			if (Math.abs(randomNum)%4 == 0) {
				move.directionType = DirectionType.EAST;
			} else if (Math.abs(randomNum)%4 == 1) {
				move.directionType = DirectionType.SOUTH;
			} else if (Math.abs(randomNum)%4 == 2) {
				move.directionType = DirectionType.WEST;
			} else if (Math.abs(randomNum)%4 == 3) {
				move.directionType = DirectionType.NORTH;
			}
		}
		return move;
	}
}