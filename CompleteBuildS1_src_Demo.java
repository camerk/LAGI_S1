import Records.RecordC;
import Snakes.Frame;



public class Demo {


    public static void main(String[] args) {
        RecordC x =  Frame.main(args);

        //int j = x.grids().dequeue()[5][17];

        Map game = new Map(x);
        System.out.println("END ");
    }
}
