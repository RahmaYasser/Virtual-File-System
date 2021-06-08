import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Try {
    public static void main(String[] args) {
        Disk.Blocks = new ArrayList<>((Arrays.asList(new Boolean[20]))); // for initializing false
        Collections.fill(Disk.Blocks, Boolean.FALSE);

        for (int i = 7; i < 14; i++) {
            Disk.Blocks.set(i,true);
        }
        Main.root.contiguousAllocation(3);
    }
}
