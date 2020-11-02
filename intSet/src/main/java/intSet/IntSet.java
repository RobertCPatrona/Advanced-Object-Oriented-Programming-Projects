import java.util.ArrayList;

/**
 * Representation of a finite set of integers.
 * 
 * @invariant getCount() >= 0
 * @invariant getCount() <= getCapacity()
 */
public class IntSet {

	private int capacity;
	private ArrayList<Integer> set;

	/**
	 * We added 2 new methods: "difference" which returns the difference between 2 sets.
	 * Second, "symmDiff" returns the symmetric difference between 2 sets.
	 */

	/**
	 * Creates a new set with 0 elements.
	 * 
	 * @param capacity
	 *            the maximal number of elements this set can have
	 * @pre capacity >= 0
	 * @post getCount() == 0
	 * @post getCapacity() == capacity
	 */
	public IntSet(int capacity) {
		try {
			this.capacity = capacity;
			set = new ArrayList<Integer>(capacity);
		} catch(IllegalArgumentException e) {
			this.capacity = 0;
			set = new ArrayList<Integer>(capacity);
			System.out.println("Error: negative capacity. Setting capacity to 0.");
		}
	}

	/**
	 * Test whether the set is empty.
	 * 
	 * @return getCount() == 0
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	/**
	 * Test whether a value is in the set
	 * 
	 * @return exists int v in getArray() such that v == value
	 */
	public boolean has(int value) {
		return set.contains(value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @pre getCount() < getCapacity()
	 * @post has(value)
	 * @post !this@pre.has(value) implies (getCount() == this@pre.getCount() + 1)
	 * @post this@pre.has(value) implies (getCount() == this@pre.getCount())
	 */
	public void add(int value) {
		if (set.size() == capacity) {
			System.out.println("Max capacity reached. Cannot add.");
		} else if (!set.contains(value)) {
			set.add(value);
		}
	}

	/**
	 * Removes a value from the set.
	 * 
	 * @post !has(value)
	 * @post this@pre.has(value) implies (getCount() == this@pre.getCount() - 1)
	 * @post !this@pre.has(value) implies (getCount() == this@pre.getCount())
	 */
	public void remove(int value) {
		set.remove((Integer) value);
	}

	/**
	 * Returns the intersection of this set and another set.
	 * 
	 * @param other
	 *            the set to intersect this set with
	 * @return the intersection
	 * @pre other != null
	 * @post forall int v: (has(v) and other.has(v)) implies return.has(v)
	 * @post forall int v: return.has(v) implies (has(v) and other.has(v))
	 */
	public IntSet intersect(IntSet other) {

		if (other==null) {
			throw new NullPointerException("Other set is null");
		}

		IntSet intersect = new IntSet(capacity);
		for (Integer value : this.set) {
			if (other.has(value)) {
				intersect.add(value);
			}
		}
		return intersect;
	}

	/**
	 * Returns the union of this set and another set.
	 * 
	 * @param other
	 *            the set to union this set with
	 * @return the union
	 * @pre other != null
	 * @post forall int v: has(v) implies return.has(v)
	 * @post forall int v: other.has(v) implies return.has(v)
	 * @post forall int v: return.has(v) implies (has(v) or other.has(v))
	 */
	public IntSet union(IntSet other) {

		if (other==null) {
			throw new NullPointerException("Other set is null");
		}

		IntSet union = new IntSet(capacity);
		for (Integer value : this.set) {
			union.add(value);
		}
		for (Integer value : other.set) {
			union.add(value);
		}
		return union;
	}

	public IntSet difference(IntSet other) {
        if (other==null) {
            throw new NullPointerException("Other set is null");
        }

        IntSet diff = new IntSet(capacity);
        for (Integer value : this.set) {
            if (!other.has(value)) {
                diff.add(value);
            }
        }
        return diff;
    }

    public IntSet symmDiff(IntSet other) {
        if (other==null) {
            throw new NullPointerException("Other set is null");
        }

		IntSet diff1 = this.difference(other);
		IntSet diff2 = other.difference(this);

        return diff1.union(diff2);
    }

	/**
	 * Returns a representation of this set as an array
	 * 
	 * @post return.length == getCount()
	 * @post forall int v in return: has(v)
	 */
	public int[] getArray() {
		int[] array = new int[set.size()];
		for (int i=0; i<set.size(); i++) {
			array[i] = set.get(i);
		}
		return array;
	}

	/**
	 * Returns the number of elements in the set.
	 */
	public int getCount() {
		return set.size();
	}

	/**
	 * Returns the maximal number of elements in the set.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns a string representation of the set. The empty set is represented
	 * as {}, a singleton set as {x}, a set with more than one element like {x,
	 * y, z}.
	 */
	@Override
	public String toString() {
		StringBuffer string = new StringBuffer("{");

		for (int i=0; i<set.size(); i++) {

			if (i == set.size()-1) {
				string.append(set.get(i).toString());
			} else {
				string.append(set.get(i).toString() + ", ");
			}
		}
		string.append("}");
		return string.toString();
	}

}
