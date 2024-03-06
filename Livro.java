import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Livro {
    protected int id;
    protected String name;
    protected int pages;

    public Livro() {

    }

    public Livro(String name, int pages) {
        this.name = name;
        this.pages = pages;
    }
    
    public void setID(int id){
        this.id=id;
    }
    
    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getPages(){
        return this.pages;
    }

    public String toString() {
        return "id: " + this.id + "| name: " + this.name + " | pages: " + this.pages;
    }
    
    public boolean ehIgual(int id){
        return id == this.id;
    }
    
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.name);
        dos.writeInt(this.pages);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.name = dis.readUTF();
        this.pages = dis.readInt();

    }
}
