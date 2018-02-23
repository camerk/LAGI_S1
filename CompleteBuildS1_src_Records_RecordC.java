package Records;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
This class add cloning capabilities to the Record Object
********DO NOT ALTER WITHOUT TALKING TO CAMERON***********
 */

@SuppressWarnings("serial")
public class RecordC implements Cloneable, Serializable {//Implentation of cloning capabilities

    public Record rec = new Record();

    public RecordC clone() {
        try {
            return (RecordC) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public RecordC deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (RecordC) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}