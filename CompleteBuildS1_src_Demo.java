import Records.RecordC;
import Snakes.Frame;


/*
    This Class runs the LG simulation and passes the results of to the graphics program.
*/
public class Demo {


    public static void main(String[] args) {
        RecordC x =  Frame.main(args); // Run Frame (simulation) and save results in X

        //int j = x.grids().dequeue()[5][17];

        Map game = new Map(x);// Run map with the input X from frame
        System.out.println("END ");
    }
}
