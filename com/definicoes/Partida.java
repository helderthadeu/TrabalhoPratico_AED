package com.definicoes;

/**
 * Arquivo que contém a classe da Partida e utilizada para datas.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.encriptar.Criptografar;

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
        data = new Data();
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
        return this.data;
    }

    public int getAno() {
        return this.data.getAno();
    }

    public void setData(int ano, int mes, int dia) {
        this.data = new Data(dia, mes, ano);
    }

    public void setDatacomString(String dataString) {
        int aux1 = Integer.parseInt(dataString.substring(0, 4));
        int aux2 = Integer.parseInt(dataString.substring(5, 7));
        int aux3 = Integer.parseInt(dataString.substring(8, 10));

        this.data = new Data(aux1, aux2, aux3);

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
        return "id: " + this.id + "| Data: " + this.data.toString() + "| Mandante: " + this.mandante + " | Visitante: "
                + this.visitante + "| Placar: " + this.golsMandante + " X " + this.golsVisitante + "| Torneio: "
                + this.torneio;
    }

    public String toStringLimpa() {
        return this.id + "@" + this.data.toDias() + "@" + this.mandante + "@" + this.visitante + "@"
                + this.golsMandante
                + "@" + this.golsVisitante
                + "@" + this.torneio + "&";
    }

    public void stringToPartida(String partida) {
        String dados[] = partida.split("@");
        this.id = Integer.parseInt(dados[0]);
        this.data.toDate(Integer.parseInt(dados[1]));
        this.mandante = dados[2];
        this.visitante = dados[3];
        this.golsMandante = Integer.parseInt(dados[4]);
        this.golsVisitante = Integer.parseInt(dados[5]);
        this.torneio = dados[6];

    }

    public long getDias() {
        return data.toDias();
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
        dos.writeLong(data.toDias());
        dos.writeUTF(Criptografar.criptografarSenhas(this.mandante));
        dos.writeUTF(Criptografar.criptografarSenhas(this.visitante));
        dos.writeInt(this.golsMandante);
        dos.writeInt(this.golsVisitante);
        dos.writeUTF(this.torneio);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        // this.data = new Data(dis.readInt(), dis.readInt(), dis.readInt());
        this.data = new Data(dis.readLong());

        this.mandante = Criptografar.descriptografarSenhas(dis.readUTF());
        this.visitante = Criptografar.descriptografarSenhas(dis.readUTF());
        this.golsMandante = dis.readInt();
        this.golsVisitante = dis.readInt();
        this.torneio = dis.readUTF();

    }

    static public String puxaPartida(long pos, RandomAccessFile file) throws IOException {
        Partida temp = null;
        byte bt[];
        file.seek(pos);
        char lapide = ' ';
        int tamElemento = 0;
        if (file.getFilePointer() < file.length()) {

            lapide = file.readChar();
            tamElemento = file.readInt();
            bt = new byte[tamElemento];
            file.read(bt);

            temp = new Partida();
            temp.fromByteArray(bt);

        }

        return lapide + "@" + tamElemento + "@" + temp.toStringLimpa();
    }

}
