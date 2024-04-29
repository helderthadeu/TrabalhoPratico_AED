
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CRUD {
    /**
     * Cria um elemento de Partida no arquivo de dados
     * 
     * @param p1 partida a ser inserida
     * @return retorna a posição da partida
     * @throws Exception
     */
    static public long create(Partida p1) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        byte[] b;
        int uID = 0;
        long posElemento = 0;
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
            posElemento = arq.getFilePointer();
            arq.writeChars(" ");
            b = p1.toByteArray();
            arq.writeInt(b.length);
            arq.write(b);

            arq.close();
        } catch (Exception e) {
            e.getMessage();
        }

        return posElemento;
    }

    /**
     * Realiza a leitura por busca sequencia de um elemento através de seu ID
     * 
     * @param id id do elemento procurado
     * @return retorna o elemento lido como um objeto partida
     * @throws Exception
     */
    static public Partida read(int id) throws Exception {
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

    /**
     * Realiza a leitura através de uma posição recebida por parâmetro
     * 
     * @param id      id do elemento procurado
     * @param posicao Posição do elemento a ser lido
     * @return retorna o elemento lido como um objeto partida
     * @throws IOException
     */
    static public Partida read(int id, long posicao) throws IOException {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");
        Partida p1 = new Partida();
        byte[] b;
        int len;

        arq.seek(posicao);

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

        arq.close();
        return null;
    }

    /**
     * Atualiza um elemento
     * 
     * @param id            Id do elemento procurado
     * @param newData       Nova data da Partida
     * @param newMandante   Novo nome de Mandante
     * @param newVisitante  Novo nome de Visitante
     * @param newGMandante  Novo valor de gols do mandante
     * @param newGVisitante Novo valor de gols do Visitante
     * @param newTorneio    Novo nome do torneio
     * @return Retorna true em caso de sucesso
     * @throws Exception
     */
    static public long update(int id, Data newData, String newMandante, String newVisitante, int newGMandante,
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
                    long tempPos = -1;
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
                            tempPos = arq.getFilePointer();
                            arq.writeChars(" ");
                            b = p2.toByteArray();
                            arq.writeInt(b.length);
                            arq.write(b);
                        }
                        arq.close();
                        return tempPos;
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

        return 0;
    }

    /**
     * Deleta o elemento após uma busca sequencial
     * 
     * @param id Id do elemento a ser deletado
     * @return Retorna true em caso de sucesso
     * @throws Exception
     */
    static public boolean delete(int id) throws Exception {
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

    /**
     * Deleta o elemento apontado pelo parâmetro
     * 
     * @param id  Id do elemento a ser deletado
     * @param pos Posição do elemento a ser deletado
     * @return Retorna true em caso de sucesso
     * @throws IOException
     */
    static public boolean delete(int id, long pos) throws IOException {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");

        arq.seek(pos);
        if (id <= arq.readInt() && id > 0) {
            arq.writeChars("*");
            arq.close();
            return true;

        } else {
            System.out.print("Id inválido");
            arq.seek(arq.length());
        }
        arq.close();

        return false;
    }

    /**
     * Busca o último Id inserido
     * 
     * @return retorna o último id
     * @throws IOException
     */
    static public int getLastId() throws IOException {
        RandomAccessFile arq = new RandomAccessFile("db\\banco.db", "rw");

        arq.seek(0);
        int retorno = arq.readInt();
        arq.close();
        return retorno;
    }
}
