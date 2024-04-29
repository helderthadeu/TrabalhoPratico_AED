package com.index;

/**
 * Arvore
 * Estrutura dos arquivos: 
 * int para raiz
 * short quantidade de elementos na página || long para endereço filho || int para ID do elemento || long para endereço do elemento
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Arvore {

    private RandomAccessFile arq; // Acessa o arquivo
    private long raiz = 0; // Posição atual da raiz
    private final int ordem = 10; // Define a ordem da árvore

    /**
     * Instancia a árvore
     * 
     * @throws Exception
     */
    public Arvore() throws IOException // Instcia a árvore pra caso já exista o arquivo de indíce
    {

        if (!(new File("db\\indice.db")).exists()) {
            arq = new RandomAccessFile("db\\indice.db", "rw");
            arq.seek(0);
            arq.writeLong(0);
            raiz = 0;
        } else {
            arq = new RandomAccessFile("db\\indice.db", "rw");
            arq.seek(0);
            raiz = arq.readLong();
        }
    }

    /**
     * Instancia a árvore já inserindo um elemento
     * 
     * @param id  Id do primeiro elemento a ser inserido
     * @param pos Posição no arquivo binário do primeiro elemento a ser inserido
     * @throws IOException
     */
    public Arvore(int id, long pos) throws IOException { // Instacia função para caso o índíce não existe

        if (!(new File("db\\indice.db")).exists()) {
            arq = new RandomAccessFile("db\\indice.db", "rw");
            arq.seek(0);
            arq.writeLong(8);
            raiz = this.criaPaginaFolha(id, pos);
            arq.seek(0);
        } else {
            arq.seek(0);
            raiz = arq.readLong();
        }
    }

    /**
     * Cria uma nova página com apenas Folhas ao fim do índice
     * 
     * @param id  Id do primeiro elemento da página criada
     * @param pos Posição no arquivo binário do primeiro elemento da página criada
     * @return Retorna em long a posição da página criada
     * @throws IOException
     */
    private long criaPaginaFolha(int id, long pos) throws IOException { // Cria uma nova página ao fim do índice
        long posPagina = arq.length();
        arq.seek(posPagina);
        arq.writeShort(1);
        arq.writeLong(-1);
        arq.writeInt(id);
        arq.writeLong(pos);
        arq.writeLong(-1);
        for (int i = 1; i < ordem - 1; i++) {
            arq.writeInt(0);
            arq.writeLong(0);
            arq.writeLong(0);
        }

        return posPagina;
    }

    /**
     * Cria uma nova página não folha ao fim do índice
     * 
     * @param esq Posição da página com elmentos menores no arquivo de indice
     * @param id  Id do primeiro elemento da página criada
     * @param pos Posição no arquivo binário do primeiro elemento da página criada
     * @param dir Posição da página com elmentos maiores no arquivo de indice
     * @return Retorna em long a posição da página criada
     * @throws IOException
     */
    private long criaPaginaNaoFolha(long esq, int id, long pos, long dir) throws IOException {

        long posPagina = arq.length();
        arq.seek(posPagina);
        arq.writeShort(1);
        arq.writeLong(esq);
        arq.writeInt(id);
        arq.writeLong(pos);
        arq.writeLong(dir);
        for (int i = 1; i < ordem - 1; i++) {
            arq.writeInt(0);
            arq.writeLong(0);
            arq.writeLong(0);
        }

        return posPagina;
    }

    /**
     * Insere um elemento
     * 
     * @param id  Id do elemento a ser inserido
     * @param pos Posição no arquivo binário do elmeento a ser inserido
     * @throws IOException
     */
    public void inserir(int id, long pos) throws IOException { // Inserie um elemento no índice
        if (raiz == 0) {
            raiz = criaPaginaFolha(id, pos);
            arq.seek(0);
            arq.writeLong(raiz);
        }

        int tamPag;
        arq.seek(raiz);
        boolean continua = false;
        long proxPos = raiz;
        long pagsAnterior[] = new long[7];
        for (int i = 0; i < pagsAnterior.length; i++) {
            pagsAnterior[i] = -1;
        }

        int contPags = 0; // Realiza uma contagem da "altura"

        while (!continua) {
            long pagAtual = proxPos;
            arq.seek(pagAtual);
            tamPag = arq.readShort();

            long tempPos; // Posição do elemento no arquivo de dados
            int tempID = 0; // Id do elemento
            boolean fimPag = true;
            for (int i = 0; i < tamPag; i++) {
                proxPos = arq.readLong();
                tempID = arq.readInt();
                tempPos = arq.getFilePointer();
                if (id < tempID) {
                    fimPag = false;
                    if (proxPos != -1) {

                        // Registra o caminho da raiz até a página desejada
                        pagsAnterior[contPags] = pagAtual;

                        contPags++;

                        arq.seek(proxPos);
                        // i = tamPag;
                        i = tamPag;
                        // return;
                    } else {
                        continua = inserirFolha(id, pos, pagAtual, i + 1, pagsAnterior);
                        // i = tamPag;
                        i = tamPag;
                        // return;
                    }
                } else {
                    arq.seek(tempPos);
                    arq.readLong();
                }
            }
            if (arq.getFilePointer() < pagAtual + 10 + (tamPag * 20) && fimPag) {
                proxPos = arq.readLong();

                if (proxPos != -1) {
                    // pagsAnterior[0] = raiz; // Registra o caminho da raiz até a página desejada
                    pagsAnterior[contPags] = pagAtual;

                    arq.seek(proxPos);
                    contPags++;
                } else {
                    continua = inserirFolha(id, pos, pagAtual, tamPag, pagsAnterior);
                }

            }

        }

    }

    /**
     * Inserir elementos como folha
     * 
     * @param id           Id do elemento a ser inserido
     * @param pos          Posição no arquivo binário do elmento a ser inserido
     * @param posPag       Posição da página do elmento a ser inserio
     * @param numElemento  Número que o elemento ocupará
     * @param pagsAnterior Vetor com o caminho das páginas até a página atual
     * @return Retorna true em caso de sucesso
     * @throws IOException
     */
    private boolean inserirFolha(int id, long pos, long posPag, int numElemento, long pagsAnterior[])
            throws IOException {
        arq.seek(posPag);

        int tamPag = arq.readShort();

        if (tamPag < ordem - 1) {
            this.insereNaPaginaEsq(posPag, id, pos, -1, -1);
            return true;
        } else {

            // Inserção caso a página esteja cheia
            int tempID; // Id do elemento lido
            long tempPos; // Posição no arquivo de dados do elemento lido
            arq.seek(posPag);

            arq.writeShort(((ordem - 1) / 2));

            // Salva o elemento do meio
            if (numElemento == ((ordem - 1) / 2) + 1) {
                tempID = id;
                tempPos = pos;

            } else {
                arq.seek(posPag + 82); // Lê o elemento do meio
                arq.readLong();
                tempID = arq.readInt();
                tempPos = arq.readLong();
            }
            arq.seek(posPag + 102); // Vai para a parte da página apagada

            arq.readLong(); // lê a posição da página apontada

            long temp = arq.getFilePointer();
            long posPaginaDois = this.criaPaginaFolha(arq.readInt(), arq.readLong());
            arq.seek(temp + 12);

            for (int i = 0; i < 3; i++) { // Loop para inserir os demais elementos na segunda página
                arq.readLong();
                temp = arq.getFilePointer();
                this.insereNaPaginaEsq(posPaginaDois, arq.readInt(), arq.readLong(), -1, -1);
                arq.seek(temp + 12);
            }
            // this.inserirFolha(id, pos, posPag, posElemento, numElemento);

            this.insereNaoFolha(tempID, tempPos, posPaginaDois, posPag, pagsAnterior);
            if (read(id) == -1) {
                this.inserir(id, pos);
            }
            return true;

        }
        // return false;
    }

    /**
     * Insere elementos em uma página específica focando nos elementos a esquerda
     *
     * @param posPagina Posição no arquivo binário da página
     * @param id        Id do elemento a ser inserido
     * @param pos       Posição no arquivo de daodos do elemento a ser inserido
     * @param posEsq    Posição no arquivo binário da página contendo os elementos
     *                  menores
     * @param posDir    Posição no arquivo binário da página contendo os elementos
     *                  maiores
     * @throws IOException
     */
    private void insereNaPaginaEsq(long posPagina, int id, long pos, long posEsq, long posDir) throws IOException {
        arq.seek(posPagina);

        int tamPag = arq.readShort();

        int posElemen = -1;
        for (int i = 0; i < tamPag; i++) {
            arq.readLong();
            int temp = arq.readInt();
            if (id < temp) {
                posElemen = i;
                i = tamPag;
            }
            arq.readLong();
        }
        if (posElemen == -1) {
            posElemen = tamPag;
        }
        // for (j = 0; elementos[j] < id; j++) { // Itera até a posição de um elemento
        // maiorque o elemento a ser adicionado{
        // int a;
        // }

        arq.seek(posPagina + 2 + 20 * (posElemen));
        long tempPos[] = new long[2]; // Posição da página com os elementos menores
        int tempId[] = new int[2]; // Id do elemento
        long tempElem[] = new long[2]; // Posição do elemento no arquivo de dados
        long tempDir = 0;

        if (posElemen == tamPag) {
            arq.writeLong(posEsq);
            arq.writeInt(id);
            arq.writeLong(pos);
            arq.writeLong(posDir);
        } else {

            tempPos[0] = arq.readLong();
            tempId[0] = arq.readInt();
            tempElem[0] = arq.readLong();

            arq.seek(arq.getFilePointer() - 20);

            arq.writeLong(posEsq);
            arq.writeInt(id);
            arq.writeLong(pos);
            if (posElemen == tamPag - 1) {
                tempDir = arq.readLong();
                arq.seek(arq.getFilePointer() - 8);
            }
            int k = 0;
            for (int i = posElemen; i < tamPag && posElemen != tamPag - 1; i++) {
                k = (k + 1) % 2;
                tempPos[k] = arq.readLong();
                tempId[k] = arq.readInt();
                tempElem[k] = arq.readLong();
                if (i + 1 == tamPag) {
                    tempDir = arq.readLong();
                    arq.seek(arq.getFilePointer() - 8);
                }
                arq.seek(arq.getFilePointer() - 20);
                k = (k + 1) % 2;

                arq.writeLong(tempPos[k]);
                arq.writeInt(tempId[k]);
                arq.writeLong(tempElem[k]);
                // if (1 + i == tamPag) {
                // arq.writeLong(tempDir);
                // }
                k = (k + 1) % 2;

            }
            arq.writeLong(tempPos[k]);
            arq.writeInt(tempId[k]);
            arq.writeLong(tempElem[k]);
            arq.writeLong(tempDir);

        }
        arq.seek(posPagina);
        ++tamPag;
        arq.writeShort(tamPag);

    }

    /**
     * Insere elementos no ínicio de uma página espécifica, alterando a primeira
     * posEsq
     * 
     * @param posPagina Posição no arquivo binário da página
     * @param id        Id do elemento a ser inserido
     * @param pos       Posição no arquivo de daodos do elemento a ser inserido
     * @param posEsq    Poisção no arquivo binário da página contendo os elementos
     *                  menores
     * @param posDir    Poisção no arquivo binário da página contendo os elementos
     *                  menores
     * 
     */
    private void insereInicio(long posPagina, int id, long pos, long posEsq, long posDir) throws IOException {
        arq.seek(posPagina);

        int tamPag = arq.readShort();

        // for (j = 0; elementos[j] < id; j++) { // Itera até a posição de um elemento
        // maiorque o elemento a ser adicionado{
        // int a;
        // }

        arq.seek(posPagina + 2);
        long tempPos[] = new long[2]; // Posição da página com os elementos menores
        int tempId[] = new int[2]; // Id do elemento
        long tempElem[] = new long[2]; // Posição do elemento no arquivo de dados
        long tempDir = 0;
        arq.readLong();
        tempId[0] = arq.readInt();
        tempElem[0] = arq.readLong();
        tempPos[1] = arq.readLong();

        arq.seek(arq.getFilePointer() - 28);

        arq.writeLong(posEsq);
        arq.writeInt(id);
        arq.writeLong(pos);
        arq.writeLong(posDir);

        arq.seek(arq.getFilePointer() - 8);
        tempPos[0] = arq.readLong();
        // arq.seek(arq.getFilePointer() - 8);
        // arq.writeLong(tempPos[0]);

        int k = 0;
        for (int i = 0; i < tamPag; i++) {
            k = (k + 1) % 2;
            if (i > 0) {
                tempPos[k] = arq.readLong();
            }
            tempId[k] = arq.readInt();
            tempElem[k] = arq.readLong();
            if (i + 1 == tamPag) {
                tempDir = arq.readLong();
                arq.seek(arq.getFilePointer() - 8);
            }
            arq.seek(arq.getFilePointer() - 20);
            k = (k + 1) % 2;

            arq.writeLong(tempPos[k]);
            arq.writeInt(tempId[k]);
            arq.writeLong(tempElem[k]);
            // if (1 + i == tamPag) {
            // arq.writeLong(tempDir);
            // }
            k = (k + 1) % 2;

        }
        arq.writeLong(tempPos[k]);
        arq.writeInt(tempId[k]);
        arq.writeLong(tempElem[k]);
        arq.writeLong(tempDir);

        arq.seek(posPagina);
        ++tamPag;
        arq.writeShort(tamPag);

    }

    /**
     * Insere elementos em uma página específica focando na posição a direita
     * 
     * @param posPagina  Posição no arquivo binário da página
     * @param id         Id do elemento a ser inserido
     * @param pos        Posição no arquivo de daodos do elemento a ser inserido
     * @param proxPagina Posição no arquivo binário da página contendo os elementos
     *                   maiores
     * 
     */
    private void insereNaPaginaDir(long posPagina, int id, long pos, long proxPagina) throws IOException

    {

        arq.seek(posPagina);

        int tamPag = arq.readShort();

        int elementos;
        int posElemen = -1;
        for (int i = 0; i < tamPag; i++) {
            arq.readLong();
            elementos = arq.readInt();
            if (elementos > id) {
                posElemen = i;
                i = tamPag;
            }
            arq.readLong();
        }
        if (posElemen == -1) {

            posElemen = tamPag;
        }

        arq.seek(2 + 8 + posPagina + 20 * (posElemen));

        long tempPos[] = new long[2];
        int tempId[] = new int[2];
        long tempElem[] = new long[2];

        if (posElemen == tamPag) {

            arq.writeInt(id);
            arq.writeLong(pos);
            arq.writeLong(proxPagina);
        } else {
            tempId[0] = arq.readInt();
            tempElem[0] = arq.readLong();
            tempPos[0] = arq.readLong();

            arq.seek(arq.getFilePointer() - 20);

            arq.writeInt(id);
            arq.writeLong(pos);
            arq.writeLong(proxPagina);

            int k = 0;
            for (int i = posElemen; i < tamPag; i++) {
                k = (k + 1) % 2;
                tempId[k] = arq.readInt();
                tempElem[k] = arq.readLong();
                tempPos[k] = arq.readLong();
                arq.seek(arq.getFilePointer() - 20);

                k = (k + 1) % 2;

                arq.writeInt(tempId[k]);
                arq.writeLong(tempElem[k]);
                arq.writeLong(tempPos[k]);

                k = (k + 1) % 2;

            }

        }
        arq.seek(posPagina);
        ++tamPag;
        arq.writeShort(tamPag);
    }

    /**
     * Insere elementos em uma página com não folhas
     * 
     * @param id           Id do elemento a ser inserido
     * @param posElem      Posição no arquivo de daodos do elemento a ser inserido
     * @param dir          Posição da página a direita do elemento a ser inserido
     * @param esq          Posição da página a esquerda do elemento a ser inserido
     * @param pagsAnterior Vetor contendo o caminho das páginas até a atual página
     */
    private boolean insereNaoFolha(int id, long posElem, long dir, long esq, long pagsAnterior[]) throws IOException {

        int pagAtual = -1;
        // for (pagAtual = 0; pagAtual+1 < pagsAnterior.length && pagsAnterior[pagAtual
        // + 1] != -1; pagAtual++)

        for (int i = 0; i < pagsAnterior.length; i++) {
            if (pagsAnterior[i] != -1) {
                pagAtual = i;

            }
        }

        return insereNaoFolha(id, posElem, dir, esq, pagsAnterior, pagAtual);

    }

    /**
     * Insere elementos em uma página com não folhas, de maneira recursiva
     * 
     * @param id           Id do elemento a ser inserido
     * @param posElem      Posição no arquivo de daodos do elemento a ser inserido
     * @param dir          Posição da página a direita do elemento a ser inserido
     * @param esq          Posição da página a esquerda do elemento a ser inserido
     * @param pagsAnterior Vetor contendo o caminho das páginas até a atual página
     * @param numPag       Número da página a ser inserida
     */
    private boolean insereNaoFolha(int id, long posElem, long dir, long esq, long pagsAnterior[], int numPag)
            throws IOException {
        int tamPag = 0;
        if (numPag == -1) {

            raiz = criaPaginaNaoFolha(esq, id, posElem, dir);
            arq.seek(0);
            arq.writeLong(raiz);
        } else {
            arq.seek(pagsAnterior[numPag]);
            tamPag = arq.readShort();
            int posElemen = -1;
            for (int i = 0; i < tamPag; i++) {
                arq.readLong();
                int temp = arq.readInt();
                if (id < temp) {
                    posElemen = i;
                    i = tamPag;
                }
                arq.readLong();
            }
            if (posElemen == -1) {
                posElemen = tamPag;
            }
            if (tamPag < ordem - 1) {

                if (posElemen == tamPag) {

                    insereNaPaginaEsq(pagsAnterior[numPag], id, posElem, esq, dir);

                } else if (posElem == 0) {
                    insereInicio(pagsAnterior[numPag], id, posElem, esq, dir);
                } else {
                    insereNaPaginaDir(pagsAnterior[numPag], id, posElem, dir);
                }

            } else {
                int tempID;
                long tempPos;

                arq.seek(pagsAnterior[numPag]);
                arq.writeShort((ordem - 1) / 2);

                if (posElemen == (((ordem - 1) / 2)) + 1) {
                    tempID = id;
                    tempPos = posElem;
                } else {

                    arq.seek(pagsAnterior[numPag] + 82); // Lê o elemento do meio

                    // Salva o elemento do meio
                    arq.readLong();
                    tempID = arq.readInt();
                    tempPos = arq.readLong();
                }
                arq.seek(pagsAnterior[numPag] + 102); // Vai para a parte da página apagada

                // arq.readLong();
                long temp = arq.getFilePointer();
                arq.getFilePointer();
                long posPaginaDois = this.criaPaginaNaoFolha(arq.readLong(), arq.readInt(), arq.readLong(),
                        arq.readLong());
                arq.seek(temp + 20);
                for (int i = 0; i < 3; i++) { // Loop para inserir os demais elementos na segunda página
                    // arq.readLong();
                    long e = arq.readLong();
                    int di = arq.readInt();
                    long p = arq.readLong();
                    temp = arq.getFilePointer();
                    long d = arq.readLong();
                    this.insereNaPaginaEsq(posPaginaDois, di, p, e, d);
                    arq.seek(temp);
                }
                if (posElemen > (((ordem - 1) / 2))) {
                    insereNaPaginaDir(posPaginaDois, id, posElem, dir);
                } else {
                    insereNaPaginaDir(pagsAnterior[numPag], id, posElem, dir);
                }

                if (numPag > 0) {
                    this.insereNaoFolha(tempID, tempPos, posPaginaDois, pagsAnterior[numPag], pagsAnterior, numPag - 1);

                } else {

                    this.insereNaoFolha(tempID, tempPos, posPaginaDois, pagsAnterior[0], pagsAnterior, numPag - 1);

                }

            }
        }
        return false;
    }

    /**
     * Realiza a leitura do índice
     * 
     * @param id id do elemento a ser lido
     * @return Retorna a posição do elemento lido
     * @throws IOException
     */
    public Long read(int id) throws IOException {

        int tamPag;

        arq.seek(0);
        arq.seek(raiz);
        boolean continua = false;
        long proxPos = raiz;

        while (!continua) {
            long pagAtual = proxPos;
            arq.seek(pagAtual);
            tamPag = arq.readShort();

            int tempID = 0;
            boolean pula = true;
            for (int i = 0; i < tamPag; i++) {

                proxPos = arq.readLong();
                tempID = arq.readInt();
                if (tempID == id) {
                    return arq.readLong();
                } else if (id < tempID) {
                    pula = false;
                    if (proxPos != -1) {
                        arq.seek(proxPos);
                        i = tamPag;
                    } else {
                        return (long) -1;

                    }
                }
                arq.readLong();
            }
            if (arq.getFilePointer() < pagAtual + 10 + (tamPag * 20) && pula) {
                proxPos = arq.readLong();

                if (proxPos != -1 && !continua) {

                    arq.seek(proxPos);
                } else {
                    return (long) -1;
                }

            }

        }

        return (long) -1;
    }

    /**
     * Realiza a leitura do índice
     * 
     * @param id id do elemento a ser lido
     * @return Retorna a posição no arquivo de indice da posiçao do elemento no
     *         arquivo de dados
     * @throws IOException
     */
    public Long readArvore(int id) throws IOException {

        int tamPag;

        arq.seek(0);
        arq.seek(raiz);
        boolean continua = false;
        long proxPos = raiz;

        while (!continua) {
            long pagAtual = proxPos;
            arq.seek(pagAtual);
            tamPag = arq.readShort();

            int tempID = 0;
            boolean pula = true;
            for (int i = 0; i < tamPag; i++) {

                proxPos = arq.readLong();
                tempID = arq.readInt();
                if (tempID == id) {
                    return arq.getFilePointer();
                } else if (id < tempID) {
                    pula = false;
                    if (proxPos != -1) {
                        arq.seek(proxPos);
                        i = tamPag;
                    } else {
                        return (long) -1;

                    }
                }
                arq.readLong();
            }
            if (arq.getFilePointer() < pagAtual + 10 + (tamPag * 20) && pula) {
                proxPos = arq.readLong();

                if (proxPos != -1 && !continua) {

                    arq.seek(proxPos);
                } else {
                    return (long) -1;
                }

            }

        }

        return (long) -1;
    }

    /**
     * Função para deletar um elemento do índice
     * 
     * @param id Id do elemento a ser deletado
     * @return Retorna true em caso de sucesso, false em caso de falha
     */
    public boolean delete(int id) throws IOException {
        int tamPag;
        arq.seek(0);
        arq.seek(raiz);
        boolean continua = false;
        long proxPos = raiz;
        int contPag = 0;
        long pagsAnterior[] = new long[7];
        for (int i = 0; i < pagsAnterior.length; i++) {
            pagsAnterior[i] = -1;
        }

        while (!continua) {
            long pagAtual = proxPos;
            arq.seek(pagAtual);
            tamPag = arq.readShort();

            int tempID = 0;
            boolean pula = true;
            for (int i = 0; i < tamPag; i++) {

                proxPos = arq.readLong();
                tempID = arq.readInt();
                if (tempID == id) {
                    long posElemento = arq.readLong();
                    posElemento = arq.getFilePointer() - 20;
                    return this.metodoRemocao(tempID, pagAtual, pagsAnterior, posElemento, i);

                } else if (id < tempID) {
                    pula = false;
                    if (proxPos != -1) {
                        pagsAnterior[contPag] = pagAtual;
                        contPag++;
                        arq.seek(proxPos);
                        i = tamPag;
                    } else {
                        return false;

                    }
                }
                arq.readLong();
            }
            if (arq.getFilePointer() < pagAtual + 10 + (tamPag * 20) && pula) {
                proxPos = arq.readLong();

                if (proxPos != -1 && !continua) {
                    pagsAnterior[contPag] = pagAtual;
                    contPag++;
                    arq.seek(proxPos);
                } else {
                    return false;
                }

            }

        }

        return false;
    }

    /**
     * Define o método de deleção do elemento
     * 
     * @param id           Id do elemento a ser deletado
     * @param pagAtual     Posição no arquivo de índice da página do elemento
     * @param pagsAnterior Vetor contendo as páginas anteriores que levaram até o
     *                     elemento a ser deletado
     * @param posElemento  Posição do elemento a ser deletado na página
     * @return Retorna true em caso de sucesso na deleção
     * @throws IOException
     */
    private boolean metodoRemocao(int id, long pagAtual, long pagsAnterior[], long posElemento, int numElemento)
            throws IOException {

        arq.seek(pagAtual);
        int tamPag = arq.readShort();

        boolean ehFolha = this.ehFolha(pagAtual);

        if (tamPag > (ordem - 1) / 2 && ehFolha) {
            this.delete1(posElemento, tamPag, pagAtual, numElemento);
            return true;
        } else if (!this.ehFolha(pagAtual)) {
            arq.seek(posElemento);

            long maiorElemento = 0;

            long proxPag = arq.readLong();

            long posDelete = 0;
            while (proxPag != -1) {
                arq.seek(proxPag);
                posDelete = arq.getFilePointer();
                int tamanho = arq.readShort();
                arq.seek(arq.getFilePointer() + (tamanho - 1) * 20);

                proxPag = arq.readLong();

                if (proxPag == -1) {
                    maiorElemento = arq.getFilePointer() - 8;
                } else {
                    arq.seek(arq.getFilePointer() + 12);
                    proxPag = arq.readLong();
                }
            }
            long anterior = maiorElemento;

            arq.seek(anterior);
            arq.readLong();
            int tempID = arq.readInt();
            long tempPos = arq.readLong();
            this.trocaElemento(posElemento, tempID, tempPos);

            arq.seek(posElemento);
            long tempProx = arq.readLong();
            arq.seek(tempProx);

            int tempTam = arq.readShort();

            int numPag = -1;
            for (int i = 0; i < pagsAnterior.length; i++) {
                if (pagsAnterior[i] != -1) {
                    numPag = i;
                }
            }

            long tempAnteriores[] = pagsAnterior;
            boolean teste = true;
            while (arq.readLong() != -1 && numPag + 1 < pagsAnterior.length) {
                teste = false;
                arq.seek(tempProx + 2 + tempTam * 20);
                arq.seek(arq.readLong());
                numPag++;
                pagsAnterior[numPag] = arq.getFilePointer();
                tempTam = arq.readShort();
            }
            if (teste) {
                numPag++;
                pagsAnterior[numPag] = pagAtual;

            }

            metodoRemocao(tempID, posDelete, pagsAnterior, anterior, tempTam);
            pagsAnterior = tempAnteriores;
            return true;
        } else if (tamPag <= (ordem - 1) / 2 && irmaFolhaPossui(pagsAnterior, pagAtual)) {
            this.delete1(posElemento, tamPag, pagAtual, numElemento);

            int pagAnterior = -1;
            for (int i = 0; i < pagsAnterior.length; i++) {
                if (pagsAnterior[i] != -1) {
                    pagAnterior = i;
                }
            }
            arq.seek(pagsAnterior[pagAnterior]);

            int tempTam = arq.readShort();

            long tempProx = 0;
            long posTroca = 0;
            for (int i = 0; i < tempTam; i++) {
                posTroca = arq.getFilePointer();
                tempProx = arq.readLong();
                if (tempProx == pagAtual) {
                    break;
                } else {
                    arq.seek(arq.getFilePointer() + 12);
                }

            }
            int tempId = arq.readInt();
            long tempPos = arq.readLong();

            this.insereNaPaginaEsq(pagAtual, tempId, tempPos, -1, -1);

            arq.seek(posTroca + 20);
            tempProx = arq.readLong();
            arq.seek(tempProx);
            tempTam = arq.readShort();
            arq.seek(arq.getFilePointer() + 8);
            trocaElemento(posTroca, arq.readInt(), arq.readLong());

            delete1(tempProx + 2, tempTam, tempProx, 0);

            return true;
        } else if (tamPag <= (ordem - 1) / 2 && !irmaFolhaPossui(pagsAnterior, pagAtual)) {
            this.delete1(posElemento, tamPag, pagAtual, numElemento);

            int pagAnterior = -1;
            for (int i = 0; i < pagsAnterior.length; i++) {
                if (pagsAnterior[i] != -1) {
                    pagAnterior = i;
                }
            }

            long posDelete = mesclaFolha(pagsAnterior, pagAtual, pagAnterior);

            int posPagAnterior = 0;
            arq.seek(pagsAnterior[pagAnterior]);
            int tempTam = arq.readShort();

            // Procura o número da posição para ser feita a remoção do elemento não folha
            for (int i = 0; i <= tempTam; i++) {
                if ((pagsAnterior[pagAnterior] + 2 + i * 20) == posDelete) {
                    posPagAnterior = i;

                }
            }
            // Caso o tamanho da página do não folha seja maior que 1 realiza a deleção
            // simples

            if (tempTam <= (ordem - 1) / 2 && pagAnterior > 0) {
                char irmaGalho = ' ';
                irmaGalho = this.irmaGalhoPossui(pagsAnterior, pagsAnterior[pagAnterior], pagAnterior - 1);
                boolean deletaNaRaiz = true;
                delete1(posDelete, tempTam, pagsAnterior[pagAnterior], posPagAnterior);
                while (tempTam <= (ordem - 1) / 2 && pagAnterior > 1 && irmaGalho == 'n') {
                    deletaNaRaiz = true;
                    if (irmaGalho == 'n') {
                        posDelete = mesclaGalho(pagsAnterior, pagsAnterior[pagAnterior], pagAnterior - 1, pagAtual);
                        arq.seek(pagsAnterior[pagAnterior - 1]);
                        tempTam = arq.readShort();

                        for (int i = 0; i <= tempTam; i++) {
                            long tempProx = arq.readLong();
                            if (tempProx == pagsAnterior[pagAnterior]) {

                                posPagAnterior = i;
                                arq.seek(arq.getFilePointer() + 12);
                            } else {
                                arq.seek(arq.getFilePointer() + 12);
                            }
                        }

                        pagAtual = pagsAnterior[pagAnterior];
                        pagAnterior--;

                    }

                    delete1(posDelete, tempTam, pagsAnterior[pagAnterior], posPagAnterior);
                    if (tempTam >= (ordem - 1) / 2) {
                        deletaNaRaiz = false;
                    }
                    irmaGalho = this.irmaGalhoPossui(pagsAnterior, pagsAnterior[pagAnterior], pagAnterior - 1);
                }
                if (irmaGalho == 'd') {
                    buscaGalhoIrma(pagsAnterior, pagAnterior - 1, pagsAnterior[pagAnterior], irmaGalho);
                    System.out.println("TESTE");
                    deletaNaRaiz = false;
                    // delete1(posDelete, tempTam, pagsAnterior[pagAnterior], 0);

                } else if (irmaGalho == 'e') {
                    buscaGalhoIrma(pagsAnterior, pagAnterior - 1, pagsAnterior[pagAnterior], irmaGalho);
                    deletaNaRaiz = false;
                    System.out.println("TESTE");
                    // delete1(posDelete, tempTam, pagsAnterior[pagAnterior], tempTam);
                }

                arq.seek(pagsAnterior[pagAnterior - 1]);
                tempTam = arq.readShort();
                arq.seek(pagsAnterior[pagAnterior]);

                if (pagAnterior == 1 && tempTam >= 1 && deletaNaRaiz) {
                    posDelete = mesclaGalho(pagsAnterior, pagsAnterior[pagAnterior], pagAnterior - 1, pagAtual);
                    arq.seek(pagsAnterior[pagAnterior - 1]);
                    tempTam = arq.readShort();

                    for (int i = 0; i <= tempTam; i++) {
                        long tempProx = arq.readLong();
                        if (tempProx == pagsAnterior[pagAnterior]) {

                            posPagAnterior = i;
                            arq.seek(arq.getFilePointer() + 12);
                        } else {
                            arq.seek(arq.getFilePointer() + 12);
                        }
                    }

                    pagAtual = pagsAnterior[pagAnterior];
                    // pagAnterior--;
                    delete1(posDelete, tempTam, pagsAnterior[pagAnterior - 1], posPagAnterior);

                    if (tempTam == 1) {
                        arq.seek(0);
                        raiz = pagsAnterior[pagAnterior];
                        arq.writeLong(raiz);

                    }
                }

            } else {
                delete1(posDelete, tempTam, pagsAnterior[pagAnterior], posPagAnterior);
                arq.seek(pagsAnterior[pagAnterior]);
                tempTam = arq.readShort();
                if (pagAnterior == 0 && tempTam <= 0) {
                    arq.seek(0);
                    raiz = pagAtual;
                    arq.writeLong(raiz);

                }
            }

            return true;
        }

        return false;

    }

    /**
     * Verifica se a página atual contém folhas
     * 
     * @param pagAtual Posição da página a ser verificada
     * @return true em caso seja uma folha
     * @throws IOException
     */
    private boolean ehFolha(long pagAtual) throws IOException {

        arq.seek(pagAtual);
        arq.readShort();

        return (arq.readLong() != -1) ? false : true;
    }

    /**
     * Método de remoção simples de um elemento
     * 
     * @param posElemento Posicao do elemento na pagina
     * @param tamPag      Tamanhho da página
     * @param pagAtual    Posição da página atual
     * @param numElemento Número da posição do elemento na página
     * @throws IOException
     */
    private void delete1(long posElemento, int tamPag, long pagAtual, int numElemento) throws IOException {
        arq.seek(posElemento + 20);

        if (numElemento < tamPag - 1) {
            long posEsqs[] = new long[tamPag - (numElemento + 1)];
            int tempId[] = new int[tamPag - (numElemento + 1)];
            long posDad[] = new long[tamPag - (numElemento + 1)];
            long posDir;
            for (int i = numElemento + 1, j = 0; i < tamPag; i++, j++) {
                posEsqs[j] = arq.readLong();
                tempId[j] = arq.readInt();
                posDad[j] = arq.readLong();
            }
            posDir = arq.readLong();

            arq.seek(posElemento);
            for (int i = numElemento + 1, j = 0; i < tamPag; i++, j++) {
                if ((j) > 0) {
                    arq.writeLong(posEsqs[j]);
                } else {
                    arq.seek(arq.getFilePointer() + 8);
                }

                arq.writeInt(tempId[j]);
                arq.writeLong(posDad[j]);
            }
            arq.writeLong(posDir);

        }

        arq.seek(pagAtual);
        tamPag--;
        arq.writeShort(tamPag);

    }

    /**
     * deleta o primeiro elemento, não salvando o primeiro apontamento a esquerda
     * 
     * @param posElemento Posição direta do elemento
     * @param tamPag      Tamanho da pagina a ser deletada
     * @param pagAtual    Página com elemento a ser deletado
     * @param numElemento Posição do elemento refente a página
     * @throws IOException
     */
    private void deletePrimeiro(long posElemento, int tamPag, long pagAtual, int numElemento) throws IOException {
        arq.seek(posElemento + 20);

        if (numElemento < tamPag - 1) {
            long posEsqs[] = new long[tamPag - (numElemento + 1)];
            int tempId[] = new int[tamPag - (numElemento + 1)];
            long posDad[] = new long[tamPag - (numElemento + 1)];
            long posDir;
            for (int i = numElemento + 1, j = 0; i < tamPag; i++, j++) {
                posEsqs[j] = arq.readLong();
                tempId[j] = arq.readInt();
                posDad[j] = arq.readLong();
            }
            posDir = arq.readLong();

            arq.seek(posElemento);
            for (int i = numElemento + 1, j = 0; i < tamPag; i++, j++) {
                arq.writeLong(posEsqs[j]);
                arq.writeInt(tempId[j]);
                arq.writeLong(posDad[j]);
            }
            arq.writeLong(posDir);

        }

        arq.seek(pagAtual);
        tamPag--;
        arq.writeShort(tamPag);

    }

    /**
     * Troca o id e a posição e um elemento
     * 
     * @param posOriginal Posição do elemento que receberá um novo id
     * @param novoId      Novo id
     * @param novaPos     Novo apontamento ao arquivo de dados.
     * @throws IOException
     */
    private void trocaElemento(long posOriginal, int novoId, long novaPos) throws IOException {
        arq.seek(posOriginal);
        arq.readLong();
        arq.writeInt(novoId);
        arq.writeLong(novaPos);
    }

    /**
     * Verifica de se a página irmã a direita possui um elemento a ser "doado"
     * 
     * @param pagsAnterior Vetor contendo o caminho das páginas até a atual página
     * @param pagAtual     Página atual
     * @return Retorna true caso haja um elemento a ser doado
     * @throws IOException
     */
    private boolean irmaFolhaPossui(long pagsAnterior[], long pagAtual) throws IOException {
        int posElemen = -1;
        for (int i = 0; i < pagsAnterior.length; i++) {
            if (pagsAnterior[i] != -1) {
                posElemen = i;
            }
        }

        if (posElemen != -1) {
            arq.seek(pagsAnterior[posElemen]);

            int tempTam = arq.readShort();

            for (int i = 0; i < tempTam; i++) {
                long tempProx = arq.readLong();
                if (tempProx == pagAtual) {
                    arq.seek(arq.getFilePointer() + 12);
                    arq.seek(arq.readLong());
                    tempTam = arq.readShort();
                    // i = tempTam;
                    if (tempTam > (ordem - 1) / 2)
                        return true;

                } else {
                    arq.seek(arq.getFilePointer() + 12);
                }

            }

        }

        return false;
    }

    /**
     * Verifica se o galho a direita ou esquerda possui elementos a ser doado
     * 
     * @param pagsAnterior Vetor contendo o caminho das páginas até a atual página
     * @param pagAtual     Página atual a ser comparada
     * @param posPag       Posição que indica a página interior no vetor
     *                     pagsAnterior
     * @return retorna true caso haja
     * @throws IOException
     */
    private char irmaGalhoPossui(long pagsAnterior[], long pagAtual, int posPag) throws IOException {

        arq.seek(pagsAnterior[posPag]);

        int tempTam = arq.readShort();

        for (int i = 0; i < tempTam; i++) {
            long tempProx = arq.readLong();
            long temp = arq.getFilePointer();
            if (tempProx == pagAtual) {
                arq.seek(arq.getFilePointer() + 12);
                arq.seek(arq.readLong());
                tempTam = arq.readShort();
                if (tempTam > (ordem - 1) / 2) {
                    return 'd';
                }
                if (i > 0) {
                    arq.seek(temp - 28);
                    arq.seek(arq.readLong());
                    tempTam = arq.readShort();
                    if (tempTam > (ordem - 1) / 2) {
                        return 'e';
                    }
                }
                break;
            } else {
                arq.seek(arq.getFilePointer() + 12);
            }

        }
        return 'n';
    }

    /**
     * Mescla páginas contendo folhas
     * 
     * @param pagsAnterior Vetor contendo o caminho das páginas até a atual página
     * @param pagAtual     Página atual a receber o elementos mesclado
     * @param pagAnterior  Posição do vetor contendo a página imediatamente anterior
     *                     a pagina atual
     * @return retorna a posição do elemento a ser deletado da página anterior
     * @throws IOException
     */
    private long mesclaFolha(long pagsAnterior[], long pagAtual, int pagAnterior) throws IOException {

        arq.seek(pagsAnterior[pagAnterior]);

        int tempTam = arq.readShort();
        int posPagAnterior;
        long posDelete = -1;
        // int idPagAnterior;
        // long posElemAnterior;

        for (posPagAnterior = 0; posPagAnterior <= tempTam; posPagAnterior++) {
            posDelete = arq.getFilePointer();
            long tempProx = arq.readLong();
            if (tempProx == pagAtual) {
                if (posPagAnterior == tempTam) {
                    posDelete = posDelete - 20;
                    arq.seek(posDelete);
                    pagAtual = arq.readLong();
                    long temp = arq.getFilePointer();
                    this.insereNaPaginaEsq(pagAtual, arq.readInt(), arq.readLong(), -1, -1);
                    arq.seek(temp + 12);
                    arq.seek(tempProx);

                } else {
                    long temp = arq.getFilePointer();
                    this.insereNaPaginaEsq(pagAtual, arq.readInt(), arq.readLong(), -1, -1);
                    arq.seek(temp + 12);
                    tempProx = arq.readLong();
                    arq.seek(tempProx);

                }
                break;

            } else {
                arq.seek(arq.getFilePointer() + 12);
            }
        }
        tempTam = arq.readShort();

        for (int i = 0; i < tempTam; i++) {
            arq.readLong();
            long tempPos = arq.getFilePointer();
            this.insereNaPaginaEsq(pagAtual, arq.readInt(), arq.readLong(), -1, -1);
            arq.seek(tempPos + 12);

        }

        return posDelete;
    }

    /**
     * Mescla páginas contendo galhos
     * 
     * @param pagsAnterior Vetor contendo o caminho das páginas até a atual
     *                     página
     * @param pagAtual     Página atual a receber o elementos mesclado
     * @param pagAnterior  Posição do vetor contendo a página imediatamente anterior
     *                     a pagina atual
     * @param pagFolha     Posição da página folha que o elemento da pagina atual
     *                     deve aponta
     * @return retorna a posição do elemento a ser deletado da página anterior
     * @throws IOException
     */
    private long mesclaGalho(long pagsAnterior[], long pagAtual, int pagAnterior, long pagFolha) throws IOException {
        arq.seek(pagsAnterior[pagAnterior]);

        int tempTam = arq.readShort();
        int posPagAnterior;
        long posDelete = -1;
        long tempProx = 0;
        for (posPagAnterior = 0; posPagAnterior <= tempTam; posPagAnterior++) {
            posDelete = arq.getFilePointer();
            tempProx = arq.readLong();
            if (tempProx == pagAtual) {
                if (posPagAnterior == tempTam) {
                    posDelete = posDelete - 20;
                    arq.seek(posDelete);
                    pagAtual = arq.readLong();

                }
                this.insereNaPaginaDir(pagAtual, arq.readInt(), arq.readLong(), -1);

                // delete1(posDelete, tempTam, pagsAnterior[pagAnterior], posPagAnterior);
                arq.seek(posDelete + 20);
                tempProx = arq.readLong();

                break;

            } else {
                arq.seek(arq.getFilePointer() + 12);
            }
        }
        arq.seek(pagAtual);
        tempTam = arq.readShort();
        long temp = arq.getFilePointer() + tempTam * 20;
        long tempEsq = 0;

        arq.seek(tempProx);
        tempTam = arq.readShort();

        for (int j = 0; j < tempTam; j++) {
            if (j == 0) {
                tempEsq = arq.readLong();
            } else {
                arq.readLong();
            }
            long tempPos = arq.getFilePointer();

            this.insereNaPaginaDir(pagAtual, arq.readInt(), arq.readLong(), arq.readLong());
            arq.seek(tempPos + 12);

        }
        arq.seek(temp);
        arq.writeLong(tempEsq);

        return posDelete;
    }

    /**
     * Realiza a busca de um elemento em uma página de galho irmã
     * 
     * @param pagsAnterior Vetor contendo o caminho das páginas até a atual página
     * @param posPag       Posição do vetor contendo o apontamento da págnina atual
     * @param pagAtual     Página atual contendo
     * @param lado         Lado da página que terá os elemento copiado
     * @throws IOException
     */
    private void buscaGalhoIrma(long pagsAnterior[], int posPag, long pagAtual, char lado) throws IOException {
        arq.seek(pagsAnterior[posPag]);
        int tempTam = arq.readShort();
        int posPagAnterior;
        long posDelete = -1;
        for (posPagAnterior = 0; posPagAnterior <= tempTam; posPagAnterior++) {
            posDelete = arq.getFilePointer();
            long tempProx = arq.readLong();
            if (tempProx == pagAtual) {
                if (lado == 'e') {
                    posDelete = posDelete - 20;
                    arq.seek(posDelete + 8);
                    this.insereNaPaginaEsq(pagAtual, arq.readInt(), arq.readLong(), -1, -1);
                    arq.seek(posDelete);
                    long tempPag = arq.readLong();
                    arq.seek(tempPag);
                    tempTam = arq.readShort();

                    arq.seek(arq.getFilePointer() + (tempTam - 1) * 20);
                    long tempDelete = arq.getFilePointer();
                    arq.readLong();
                    trocaElemento(posDelete, arq.readInt(), arq.readLong());
                    long novaProx = arq.readLong();
                    delete1(tempDelete, tempTam, tempPag, tempTam);

                    arq.seek(pagAtual + 2);
                    arq.writeLong(novaProx);

                } else if (lado == 'd') {
                    insereNaPaginaDir(pagAtual, arq.readInt(), arq.readLong(), -1);

                    arq.seek(posDelete + 20);
                    long tempPos = arq.readLong();

                    arq.seek(tempPos);

                    tempTam = arq.readShort();
                    long temp = arq.getFilePointer();
                    tempProx = arq.readLong();

                    trocaElemento(posDelete, arq.readInt(), arq.readLong());

                    deletePrimeiro(temp, tempTam, tempPos, 0);
                    arq.seek(pagAtual);
                    arq.seek(arq.getFilePointer() + 2 + arq.readShort() * 20);
                    arq.writeLong(tempProx);

                }
                break;

            } else {
                arq.seek(arq.getFilePointer() + 12);
            }
        }
    }

    /**
     * Atualiza a posição do elemento
     * 
     * @param id      Id do elemento a ser alterado
     * @param novaPos nova posição do elemento
     */
    public void update(int id, long novaPos) throws IOException {
        long temp = this.readArvore(id);
        arq.seek(temp);
        arq.writeLong(novaPos);

    }

    /**
     * Fecha o arquivo
     */
    public void fecharArquivo() throws IOException {
        arq.close();
    }

}