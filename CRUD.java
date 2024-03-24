
import java.io.EOFException;
import java.io.RandomAccessFile;

public class CRUD {
    static void create(Partida p1) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        byte[] b;
        int uID = 0;
        // try {
        try {
            arq.seek(0);
            uID = arq.readInt(); 
        } catch (EOFException e) {
            uID = 0;
            arq.writeInt(uID);
        }
        try {
            p1.setID(++uID);
            arq.seek(0);
            arq.writeInt(uID);
            arq.seek(arq.length());
            arq.writeChars(" ");
            b = p1.toByteArray();
            arq.writeInt(b.length);
            arq.write(b);

            arq.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    static Partida read(int id) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        Partida p1 = new Partida();
        byte[] b;
        int len;

        arq.seek(0);
        if (id <= arq.readInt() && id > 0) {
            while (arq.getFilePointer() < arq.length()) {
                char temp = arq.readChar();
                if (temp != '*') {
                    len = arq.readInt();
                    b = new byte[len];
                    arq.read(b);
                    p1.fromByteArray(b);
                    if (p1.ehIgual(id)) {
                        arq.close();
                        return p1;
                    }
                } else {
                    len = arq.readInt();
                    b = new byte[len];
                    arq.read(b);
                }

            }
        } else {
            System.out.print("Id inválido");
            arq.seek(arq.length());
        }
        arq.close();
        return null;
    }

    static boolean update(int id, Data newData, String newMandante, String newVisitante, int newGMandante,
            int newGVisitante, String newTorneio) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        Partida p1 = new Partida();
        byte[] b;
        int len;

        arq.seek(0);
        if (id <= arq.readInt() && id > 0) {
            while (arq.getFilePointer() < arq.length()) {
                char temp = arq.readChar();
                long lapide = arq.getFilePointer() - 2;
                if (temp != '*') {
                    len = arq.readInt();
                    b = new byte[len];
                    arq.read(b);
                    p1.fromByteArray(b);
                    if (p1.ehIgual(id)) {
                        Partida p2 = new Partida(newData, newMandante, newVisitante, newGMandante, newGVisitante,
                                newTorneio);
                        p2.setID(p1.getID());
                        if (newMandante.length() + newVisitante.length() + newTorneio.length() <= p1.getMandante()
                                .length() + p1.getVisitante().length() + p1.getTorneio().length()) {
                            arq.seek(lapide);
                            arq.readChar();
                            arq.readInt();
                            b = p2.toByteArray();
                            arq.write(b);
                        } else {
                            arq.seek(lapide);
                            arq.writeChars("*");
                            arq.seek(arq.length());
                            arq.writeChars(" ");
                            b = p2.toByteArray();
                            arq.writeInt(b.length);
                            arq.write(b);
                        }
                        arq.close();
                        return true;
                    }
                } else {
                    len = arq.readInt();
                    b = new byte[len];
                    arq.read(b);
                }

            }
        } else {
            System.out.print("Id inválido");
            arq.seek(arq.length());
        }
        arq.close();

        return false;
    }

    static boolean delete(int id) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        Partida p1 = new Partida();
        byte[] b;
        int len;

        arq.seek(0);
        if (id <= arq.readInt() && id > 0) {
            while (arq.getFilePointer() < arq.length()) {
                char temp = arq.readChar();
                long lapide = arq.getFilePointer() - 2;
                if (temp != '*') {
                    len = arq.readInt();
                    b = new byte[len];
                    arq.read(b);
                    p1.fromByteArray(b);
                    if (p1.ehIgual(id)) {
                        arq.seek(lapide);
                        arq.writeChars("*");
                        arq.close();
                        return true;
                    }
                } else {
                    len = arq.readInt();
                    b = new byte[len];
                    arq.read(b);
                }

            }
        } else {
            System.out.print("Id inválido");
            arq.seek(arq.length());
        }
        arq.close();

        return false;
    }
}
