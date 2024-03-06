
import java.io.EOFException;
import java.io.RandomAccessFile;

public class CRUD {
    static void create(Livro l1) throws Exception {
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
            l1.setID(++uID);
            arq.seek(0);
            arq.writeInt(uID);
            arq.seek(arq.length());
            arq.writeChars(" ");
            b = l1.toByteArray();
            arq.writeInt(b.length);
            arq.write(b);

            arq.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    static Livro read(int id) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        Livro l1 = new Livro();
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
                    l1.fromByteArray(b);
                    if (l1.ehIgual(id)) {
                        arq.close();
                        return l1;
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

    static boolean update(int id, String newName, int newPages) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        Livro l1 = new Livro();
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
                    l1.fromByteArray(b);
                    if (l1.ehIgual(id)) {
                        Livro l2 = new Livro(newName, newPages);
                        l2.setID(l1.getID());
                        if (newName.length() <= l1.getName().length()) {
                            arq.seek(lapide);
                            arq.readChar();
                            arq.readInt();
                            b = l2.toByteArray();
                            arq.write(b);
                        } else {
                            arq.seek(lapide);
                            arq.writeChars("*");
                            arq.seek(arq.length());
                            arq.writeChars(" ");
                            b = l2.toByteArray();
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
        Livro l1 = new Livro();
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
                    l1.fromByteArray(b);
                    if (l1.ehIgual(id)) {
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
