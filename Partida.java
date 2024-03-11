
/**
 * Arquivo que contém a classe da Partida e utilizada para datas.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Partida {
    protected int id;
    protected Data data;
    protected String mandante;
    protected String visitante;
    protected int golsMandante;
    protected int golsVisitante;
    protected String torneio;

    public Partida() {

    }

    public Partida(Data data, String mandante, String visitante, int golsMandante, int golsVisitante, String torneio) {
        this.data = new Data(data.getDia(), data.getMes(), data.getAno());
        this.mandante = mandante;
        this.visitante = visitante;
        this.golsMandante = golsMandante;
        this.golsVisitante = golsVisitante;
        this.torneio = torneio;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public Data getData() {
        return data;
    }

    public void setData(int dia, int mes, int ano) {
        this.data = new Data(dia, mes, ano);
    }

    public String getMandante() {
        return mandante;
    }

    public void setMandante(String mandante) {
        this.mandante = mandante;
    }

    public String getVisitante() {
        return visitante;
    }

    public void setVisitante(String visitante) {
        this.visitante = visitante;
    }

    public int getGolsMandante() {
        return golsMandante;
    }

    public void setGolsMandante(int golsMandante) {
        this.golsMandante = golsMandante;
    }

    public int getGolsVisitante() {
        return golsVisitante;
    }

    public void setGolsVisitante(int golsVisitante) {
        this.golsVisitante = golsVisitante;
    }

    public String getTorneio() {
        return torneio;
    }

    public void setTorneio(String torneio) {
        this.torneio = torneio;
    }

    public String toString() {
        return "id: " + this.id + "| Data: " + this.toString() + "| Mandante: " + this.mandante + " | Visitante: "
                + this.visitante + "| Placar: " + this.golsMandante + " X " + this.golsVisitante + "| Torneio: "
                + this.torneio;
    }

    /**
     * 
     * @param id id do elemente a ser comparado
     * @return true caso os elementos sejam iguais
     */

    public boolean ehIgual(int id) {
        return id == this.id;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeInt(this.data.getDia());
        dos.writeInt(this.data.getMes());
        dos.writeInt(this.data.getAno());
        dos.writeUTF(this.mandante);
        dos.writeUTF(this.visitante);
        dos.writeInt(this.golsMandante);
        dos.writeInt(this.golsVisitante);
        dos.writeUTF(this.torneio);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.data = new Data(dis.readInt(), dis.readInt(), dis.readInt());
        this.mandante = dis.readUTF();
        this.visitante = dis.readUTF();
        this.golsMandante = dis.readInt();
        this.golsVisitante = dis.readInt();
        this.torneio = dis.readUTF();

    }
}
