package com.droiddice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.util.Log;

public class DiceSet {

	ArrayList<Die> mDice;
	String mName;

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
	 * Create a set from an array of die sizes
	 * 
	 * @param maxValues array of die sizes
	 */
	public DiceSet(Integer[] maxValues) {
		setDiceSizes(maxValues);
		mName = toString();
	}

	private void setDiceSizes(Integer[] maxValues) {
		mDice = new ArrayList<Die>();
		for (int i = 0; i < maxValues.length; ++i) {
			Die die = new SimpleDie(maxValues[i]);
			mDice.add(die);
		}
	}
	
	/* Constructor helpers */
	private static Integer[] filledArray(int count, int value) {
		Integer[] result = new Integer[count];
		Arrays.fill(result, value);
		return result;
	}
	
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
	
	/* Properties */

	public void setDiceSizesAsString(String string) {
		mDice = diceStringToArray(string);
	}
	
	public List<Die> asList() {
		return Collections.unmodifiableList(mDice);
	}

	private String defaultName(int count, Object die) {
		return (count > 1) ? (count + die.toString()) : die.toString();
	}
	
	public void roll() {
		for (Die die : mDice) {
			die.roll();
		}
	}

	public int getResult() {
		int total = 0;
		for (Die die : mDice) {
			total += die.getValue();
		}
		return total;
	}

	public int getCount() {
		return mDice.size();
	}

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

	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}

	public int countOf(String s) {
		int count = 0;
		for (Die die : mDice) {
			if (die.toString().equals(s)) {
				++count;
			}
		}
		return count;
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
}
