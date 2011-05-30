/* Copyright (C) 2009-2011 Andrew Semprebon */
package com.droiddice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/**
 * represents a number of dice that are combined in a given way (currentlt, added)
 */
public class DiceSet {

	ArrayList<Die> mDice;
	String mName;

	// TODO: Not sure why this is defined in this class...
    public static final Die[] DIE_TYPES = new Die[] { 
    	new SimpleDie(4),
    	new SimpleDie(6),
    	new SimpleDie(8),
    	new SimpleDie(10),
    	new SimpleDie(12),
    	new SimpleDie(20),
    	new SimpleDie(100),
    	new AdjustmentDie(+1),
    	new AdjustmentDie(-1) 
    };
    
    public static final DiceSet DEFAULT_DICE_SET = new DiceSet("d6");

	/* Constructors */
	
	/**
	 * Create a set from a string, such as 4d8+1d6
	 * 
	 * @param string string representing dice
	 */
	public DiceSet(String string) {
		mDice = diceStringToArray(string);
		setName(string);
	}
	
	/**
	 * Convert a string dice specification into an array of dice 
	 * 
	 * @param string dice string ("d4+2d6")
	 * @return array of dice ([d4, d6, d6])
	 */
	private static ArrayList<Die> diceStringToArray(String string) {
		String[] dieTypes = string.replace("-", "+-").split("\\+");
		ArrayList<Die> dieSizes = new ArrayList<Die>();
		if (string.length() > 0) {
			for (String dieType : dieTypes) {
				int dPos = dieType.indexOf('d');
				if (dPos == -1) {
					if (dieType.length() > 0) {
						dieSizes.add(new AdjustmentDie(Integer.parseInt(dieType)));
					}
				} else {
					int count = (dPos == 0) ? 1 : Integer.parseInt(dieType.substring(0, dPos));
					int size = Integer.parseInt(dieType.substring(dPos+1));
					for (int j = 0; j < count; ++j) {
						dieSizes.add(new SimpleDie(size));
					}
				}
			}
		}
		return dieSizes;
	}

	/* Properties */

	public void setDiceSizesAsString(String string) {
		mDice = diceStringToArray(string);
	}
	
	public int getResult() {
		int total = 0;
		for (Die die : mDice) {
			total += die.getValue();
		}
		return total;
	}
	
	public String getDisplayResult () {
		StringBuilder ret = new StringBuilder();
		int total = 0;
		for (Die die : mDice) {
			total += die.getValue();
			ret.append(die.getDisplayValue());
			ret.append(" + ");
		}
		ret.delete(ret.length() - 3, ret.length()-1);
		ret.append(" = ");
		ret.append(total);
		return ret.toString();
	}

	public int getCount() {
		return mDice.size();
	}

	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}

	/* Conversions */
	/**
	 * Generates the dice specification string that can be used to recreate the dice set
	 */
	public String toString() {
		StringBuffer result = new StringBuffer("");
		Die lastDie = null;
		int count = 0;
		for (Die die : mDice) {
			if (!die.sameType(lastDie)) {
				addDieToString(result, count, lastDie);
				count = 1;
				lastDie = die;
			} else {
				++count;
			}
		}
		addDieToString(result, count, lastDie);
		return result.toString().replace("++", "+").replace("+-", "-");
	}

	private void addDieToString(StringBuffer result, int count, Die lastDie) {
		if (lastDie != null) {
			if (result.length() != 0) {
				result.append("+");
			}
			result.append(defaultName(count, lastDie));
		}
	}
	
	private String defaultName(int count, Object die) {
		return (count > 1) ? (count + die.toString()) : die.toString();
	}
	
	public List<Die> asList() {
		return Collections.unmodifiableList(mDice);
	}

	/* Actions */
	public void roll() {
		for (Die die : mDice) {
			die.roll();
		}
	}

	public void add(String string) {
		mDice.addAll((new DiceSet(string)).mDice);
		AdjustmentDie adjustment = null;
		for (Die die : mDice) {
			if (die instanceof AdjustmentDie) {
				if (adjustment == null) {
					adjustment = (AdjustmentDie) die;
				} else {
					adjustment.setValue(adjustment.getValue() + die.getValue());
					mDice.remove(die);
				}
			}
		}
	}
	
	public void remove(Die die) {
		mDice.remove(die);
	}

	/* psuedo-properties - probably should make these true indexed properties */
	
	public int sizeOf(int i) {
		if (i >= mDice.size()) {
			return 0;
		}
		Die die = mDice.get(i);
		return  die.getMax() + 1 - die.getMin();
	}

	public int valueOf(int i) {
		return (i < mDice.size()) ? mDice.get(i).getValue() : 0;
	}

}
