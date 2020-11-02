import org.junit.Test;
import static org.junit.Assert.*;

public class IntSetTest {

    /**
     * We have 13 tests for each method and the constructor. 
     */

    @Test
    public void isEmpty() {

        IntSet intSet = new IntSet(10);
        assertTrue (intSet.isEmpty());

        intSet.add(5);
        assertFalse (intSet.isEmpty());

        intSet.remove(5);
        assertTrue (intSet.isEmpty());
    }

    @Test
    public void has() {

        IntSet intSet = new IntSet(10);
        intSet.add(3);
        intSet.add(5);

        assertTrue (intSet.has(3));
        assertFalse (intSet.has(1));
        assertTrue (intSet.has(5));
    }

    @Test
    public void add() {

        IntSet intSet = new IntSet(10);
        intSet.add(7);
        intSet.add(9);
        intSet.add(11);

        assertTrue (intSet.has(7));
        assertFalse (intSet.has(2));
        assertTrue (intSet.has(11));
    }

    @Test
    public void remove() {

        IntSet intSet = new IntSet(10);
        intSet.add(12);
        intSet.add(12);

        intSet.add(16);
        intSet.remove(12);
        intSet.remove(30);

        assertFalse (intSet.has(12));
        assertTrue (intSet.has(16));
        assertFalse (intSet.has(30));
    }

    @Test
    public void intersect() {

        IntSet intSet1 = new IntSet(10);
        IntSet intSet2 = new IntSet(10);
        IntSet intersect;

        intSet1.add(2);
        intSet1.add(4);
        intSet1.add(6);

        intSet2.add(3);
        intSet2.add(4);
        intSet2.add(6);
        intSet2.add(7);

        intersect = intSet1.intersect(intSet2);
        assertTrue(intersect.has(4));
        assertTrue(intersect.has(6));
        assertFalse(intersect.has(2));

        try {
            intersect = intSet1.intersect(null);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void union() {

        IntSet intSet1 = new IntSet(10);
        IntSet intSet2 = new IntSet(10);
        IntSet union;

        intSet1.add(2);
        intSet1.add(4);
        intSet1.add(6);

        intSet2.add(3);
        intSet2.add(4);
        intSet2.add(6);
        intSet2.add(7);

        union = intSet1.union(intSet2);
        assertTrue(union.has(4));
        assertTrue(union.has(6));
        assertTrue(union.has(7));
        assertFalse(union.has(1));

        try {
            union = intSet1.union(null);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void difference() {

        IntSet intSet1 = new IntSet(10);
        IntSet intSet2 = new IntSet(10);

        intSet1.add(2);
        intSet1.add(4);
        intSet1.add(6);
        intSet1.add(9);

        intSet2.add(3);
        intSet2.add(4);
        intSet2.add(6);
        intSet2.add(7);

        IntSet diff1 = intSet1.difference(intSet2);
        assertTrue(diff1.has(2));
        assertTrue(diff1.has(9));

        IntSet diff2 = intSet2.difference(intSet1);
        assertTrue(diff2.has(3));
        assertTrue(diff2.has(7));

        intSet2.remove(7);
        diff2 = intSet2.difference(intSet1);
        assertFalse(diff2.has(7));

        try {
            diff1 = intSet1.difference(null);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void symmDiff() {

        IntSet intSet1 = new IntSet(10);
        IntSet intSet2 = new IntSet(10);

        intSet1.add(2);
        intSet1.add(4);
        intSet1.add(6);
        intSet1.add(9);

        intSet2.add(3);
        intSet2.add(4);
        intSet2.add(6);
        intSet2.add(7);

        IntSet symm = intSet1.symmDiff(intSet2);
        assertTrue(symm.has(2));
        assertTrue(symm.has(9));
        assertTrue(symm.has(3));
        assertTrue(symm.has(7));

        intSet2.remove(7);
        symm = intSet2.difference(intSet1);
        assertFalse(symm.has(7));

        try {
            symm = intSet1.symmDiff(null);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void getArray() {

        IntSet intSet = new IntSet(10);
        intSet.add(2);
        intSet.add(4);
        intSet.add(6);

        int[] arr1 = {2, 4, 6};
        int[] arr2 = {2, 4, 7};

        for(int i = 0; i<arr1.length; i++) {
            if(intSet.getArray()[i] != arr1[i]){
                assertTrue(false);
            }
        }

        for(int i = 0; i<arr1.length; i++) {
            if(intSet.getArray()[i] != arr2[i]){
                assertFalse(false);
            }
        }
        assertTrue(true);
    }

    @Test
    public void getCount() {

        IntSet intSet = new IntSet(10);
        intSet.add(2);
        intSet.add(4);
        intSet.add(6);

        assertTrue(intSet.getArray().length==intSet.getCount());
        assertFalse(intSet.getCount()==4);

        intSet.add(7);
        assertTrue(intSet.getCount()==4);
    }

    @Test
    public void getCapacity() {

        IntSet intSet = new IntSet(10);
        assertTrue(intSet.getCapacity()==10);
        assertFalse(intSet.getCapacity()==11);
    }

    @Test
    public void toStringTest() {

        IntSet intSet = new IntSet(10);
        intSet.add(2);
        intSet.add(4);
        intSet.add(6);

        assertTrue(intSet.toString().equals("{2, 4, 6}"));

        intSet.add(9);
        assertTrue(intSet.toString().equals("{2, 4, 6, 9}"));

        intSet.remove(4);
        assertTrue(intSet.toString().equals("{2, 6, 9}"));
        assertFalse(intSet.toString().equals("{2, 4, 6, 9}"));
    }

    @Test
    public void IntSet(){
        IntSet set = new IntSet(10);
        assertTrue(set.getCapacity()==10);
        assertFalse(set.getCapacity()==9);

        assertTrue(set.isEmpty());
    }
}
