package com.index;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Hashing {

    private RandomAccessFile diretorio;
    private RandomAccessFile buckets;
    private int pGeral = 0;
    private final int tamBucket = 6;

    public Hashing() throws IOException {
        if (!(new File("db\\diretorio.db")).exists() && !(new File("db\\buckets.db")).exists()) {
            diretorio = new RandomAccessFile("db\\diretorio.db", "rw");
            buckets = new RandomAccessFile("db\\buckets.db", "rw");

            diretorio.seek(0);
            buckets.seek(0);

            diretorio.writeShort(pGeral);
            diretorio.writeLong(0);

            buckets.writeShort(0);
            buckets.writeShort(0);
        } else {
            diretorio = new RandomAccessFile("db\\diretorio.db", "rw");
            buckets = new RandomAccessFile("db\\buckets.db", "rw");

            diretorio.seek(0);
            buckets.seek(0);

            pGeral = diretorio.readShort();
        }

    }

    public void inserir(int id, long pos) throws IOException {

        int hashing = hashCode(id);

        diretorio.seek(hashing * 8 + 2);
        long bucketAtual = diretorio.readLong();
        buckets.seek(bucketAtual);

        int tam = buckets.readShort();
        int pLocal = buckets.readShort();

        if (tam < tamBucket) {
            buckets.seek(buckets.getFilePointer() + tam * 12);
            buckets.writeInt(id);
            buckets.writeLong(pos);
            buckets.seek(bucketAtual);
            tam++;
            buckets.writeShort(tam);
        } else if (pLocal < pGeral) {
            buckets.seek(bucketAtual + 2);
            pLocal++;
            buckets.writeShort(pLocal);

            alteraPonteiros(bucketAtual, pLocal);
            distribuiBucket(bucketAtual);
            inserir(id, pos);
        } else if (pLocal >= pGeral) {

            // diretorio.seek(0);
            // pGeral++;
            // diretorio.writeShort(pGeral);
            aumentaDiretorio();
            inserir(id, pos);

        }

    }

    /**
     * Define o código de hash para um determinado elemento baseado em seu id
     * 
     * @param id id do elemento.
     * @return Retorna o código descoberto
     */
    private int hashCode(int id) {

        return (int) (id % Math.pow(2, (double) pGeral));

    }

    /**
     * Altera os apontamentos do ponteiro após a mudança de uma profundidade local
     * 
     * @param pos    Apontamento atual da profundidade local no diretório
     * @param pLocal novo Plocal
     * @throws IOException
     */
    private void alteraPonteiros(long pos, int pLocal) throws IOException {
        diretorio.seek(2);
        long tempPos = diretorio.readLong();

        while (tempPos != pos) {
            tempPos = diretorio.readLong();
        }

        tempPos = (long) (Math.pow(2, (double) (pLocal - 1)));
        int max = (int) (Math.pow(2, (double) (pGeral - pLocal)));

        diretorio.seek(diretorio.getFilePointer() + tempPos * 8);
        diretorio.seek(diretorio.getFilePointer() - 8);

        for (int i = 0; i < max; i++) {
            diretorio.writeLong(criaBucket(pLocal));
            diretorio.seek(diretorio.getFilePointer() + tempPos * 8);

        }

    }

    /**
     * Cria um bucket Vazio
     * 
     * @return Retorna a posição do bucket criado
     * @throws IOException
     */
    private long criaBucket(int pLocal) throws IOException {

        buckets.seek(buckets.length());
        long retorno = buckets.getFilePointer();
        buckets.writeShort(0);
        buckets.writeShort(pLocal);

        for (int i = 0; i < tamBucket; i++) {
            buckets.writeInt(0);
            buckets.writeLong(0);
        }

        return retorno;
    }

    /**
     * Distriui os elementos de um bucket novamente.
     * 
     * @param pos Posição do bucket
     * @throws IOException
     */
    private void distribuiBucket(long pos) throws IOException {
        buckets.seek(pos);
        int tam = buckets.readShort();
        buckets.readShort();
        int ids[] = new int[tam];
        long poss[] = new long[tam];
        for (int i = 0; i < tam; i++) {
            ids[i] = buckets.readInt();
            poss[i] = buckets.readLong();
        }

        buckets.seek(pos);
        buckets.writeShort(0);
        buckets.readShort();
        for (int i = 0; i < tam; i++) {
            inserir(ids[i], poss[i]);
        }

    }

    /**
     * Aumenta o diretório
     * 
     * @throws IOException
     */
    private void aumentaDiretorio() throws IOException {
        diretorio.seek(0);
        pGeral++;
        diretorio.writeShort(pGeral);

        int max = (int) (Math.pow(2, (pGeral - 1)));
        long tempPos[] = new long[max];
        // diretorio.seek(2);
        for (int i = 0; i < max; i++) {
            tempPos[i] = diretorio.readLong();
        }
        for (int i = 0; i < max; i++) {
            diretorio.writeLong(tempPos[i]);
        }

    }

    /**
     * Realiza a busca de um determinado elemento
     * 
     * @param id Id do elemento buscado
     * @return Retorna a posição do elemento no arquivo de dados
     * @throws IOException
     */
    public long read(int id) throws IOException {
        int hashing = hashCode(id);

        diretorio.seek(hashing * 8 + 2);
        long bucketAtual = diretorio.readLong();
        buckets.seek(bucketAtual);

        int tam = buckets.readShort();
        buckets.readShort();
        for (int i = 0; i < tam; i++) {
            int tempID = buckets.readInt();
            if (tempID == id) {
                return buckets.readLong();
            }
            buckets.seek(buckets.getFilePointer() + 8);
        }

        return -1;
    }

    /**
     * Realiza a busca de um determinado elemento
     * 
     * @param id Id do elemento buscado
     * @return Retorna a posição do elemento no arquivo de bucket
     * @throws IOException
     */
    private long readHash(int id) throws IOException {
        int hashing = hashCode(id);

        diretorio.seek(hashing * 8 + 2);
        long bucketAtual = diretorio.readLong();
        buckets.seek(bucketAtual);

        int tam = buckets.readShort();
        buckets.readShort();
        for (int i = 0; i < tam; i++) {
            int tempID = buckets.readInt();
            if (tempID == id) {
                return buckets.getFilePointer();
            }
            buckets.seek(buckets.getFilePointer() + 8);
        }

        return -1;
    }

    /**
     * Realiza a remoção de um elemento
     * 
     * @param id Id do elemento a ser removido
     * @return retorna true em caso de sucesso
     * @throws IOException
     */
    public boolean delete(int id) throws IOException {
        int hashing = hashCode(id);

        diretorio.seek(hashing * 8 + 2);
        long bucketAtual = diretorio.readLong();
        buckets.seek(bucketAtual);

        int tam = buckets.readShort();
        buckets.readShort();
        int tempID = 0;
        int posNaPag = -1;
        while (tempID != id) {
            tempID = buckets.readInt();
            buckets.seek(buckets.getFilePointer() + 8);
            posNaPag++;
        }
        if (tempID != id) {
            return false;
        }

        long temp = buckets.getFilePointer() - 12;
        int tempIDs[] = new int[tam - (posNaPag + 1)];
        long tempPos[] = new long[tam - (posNaPag + 1)];
        for (int i = posNaPag + 1, j = 0; i < tam; i++, j++) {
            tempIDs[j] = buckets.readInt();
            tempPos[j] = buckets.readLong();
        }
        buckets.seek(temp);
        for (int i = posNaPag + 1, j = 0; i < tam; i++, j++) {
            buckets.writeInt(tempIDs[j]);
            buckets.writeLong(tempPos[j]);
        }

        buckets.seek(bucketAtual);
        tam--;
        buckets.writeShort(tam);
        return true;
    }

    /**
     * Atualiza a posição de um elemento no arquivo de indice
     * 
     * @param id      Ido do elemento a ser atualizado
     * @param novaPos Nova posição apontada
     * @throws IOException
     */
    public void update(int id, long novaPos) throws IOException {
        readHash(id);
        buckets.writeLong(novaPos);
    }
}
