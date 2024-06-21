import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import com.casamentoPadroes.BoyerMoore;
import com.casamentoPadroes.Kmp;
import com.casamentoPadroes.RabinKarp;
import com.compress.LZW;
import com.definicoes.Data;
import com.definicoes.Partida;
import com.index.Arvore;
import com.index.Hashing;
import com.index.IndiceInvertido;

public class App {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String arquivoCSV = "results.csv";
        final int totalElementos = 100;
        Arvore arvore = new Arvore();
        Hashing hash = new Hashing();
        String nomeArquivoInvertida = "db\\indiceInv.db";
        IndiceInvertido indiceInvertido = new IndiceInvertido();

        // 0 - Arvore
        // 1 - Hashing
        // System.out.println("Deseja utilizar hashing(1) ou arvore B(0) ?");
        // int tipoIndice = sc.nextInt();
        int tipoIndice = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoCSV))) {

            if (!(new File("db\\banco.db")).exists()) {

                File arquivo = new File(nomeArquivoInvertida);
                arquivo.createNewFile();
                String line;
                int id = 0;
                while ((line = br.readLine()) != null && id <= totalElementos) {

                    String[] data = line.split(",");
                    Partida match = new Partida();
                    match.setID(id++);
                    match.setDatacomString(data[0]);
                    match.setMandante(data[1]);
                    match.setVisitante(data[2]);
                    match.setGolsMandante(Integer.parseInt(data[3]));
                    match.setGolsVisitante(Integer.parseInt(data[4]));
                    match.setTorneio(data[5]);
                    long tempPos = CRUD.create(match);
                    switch (tipoIndice) {
                        case 0:
                            arvore.inserir(id, tempPos);
                            break;

                        case 1:
                            hash.inserir(id, tempPos);
                            break;
                    }
                    indiceInvertido.adicionarTorneio(match.getTorneio());
                    indiceInvertido.adicionarIdAoTorneio(match.getTorneio(), tempPos);
                }
                indiceInvertido.criarIndice(nomeArquivoInvertida);
            } else {
                indiceInvertido = new IndiceInvertido(new RandomAccessFile(nomeArquivoInvertida, "rw"));
            }
            // indiceInvertido.printListaInvertida();

            // RandomAccessFile temp = new RandomAccessFile("db\\banco.db", "rw");
            // Ordenacao teste = new Ordenacao(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int opcao = 0;
        while (opcao != -1) {
            System.out.println("Digite as seguintes opções para carregar o arquivo:");
            System.out.println("[0] Create");
            System.out.println("[1] Read");
            System.out.println("[2] Update");
            System.out.println("[3] Delete");
            System.out.println("[4] Imprimir todos");
            System.out.println("[5] Comprimir Arquivo");
            System.out.println("[6] Descompactar arquivo");
            System.out.println("[7] Buscar por padrão");
            System.out.println("[8] Printar lista Invertida");
            System.out.println("[9] Busca por torneios simultâneos");
            System.out.println("[10] Busca por ocorrência de torneios");
            System.out.println("[-1] para encerrar");

            opcao = sc.nextInt();
            switch (opcao) {
                case 0: { // CREATE
                    System.out.println("Digite o dia da partida que deseja criar: ");
                    int createDia = sc.nextInt();

                    System.out.println("Digite o mes da partida que criar: ");
                    int createMes = sc.nextInt();

                    System.out.println("Digite o ano da partida que deseja criar: ");
                    int createAno = sc.nextInt();

                    System.out.println("Digite o mandante da partida que deseja criar: ");
                    if (sc.hasNextLine() || sc.hasNext()) {
                        sc.nextLine();
                    }
                    String createMandante = sc.nextLine();

                    System.out.println("Digite o visitante da partida que deseja criar: ");
                    String createVisitante = sc.nextLine();

                    System.out.println("Digite o numero de gols do mandante da partida que deseja criar: ");
                    int creategolsCasa = sc.nextInt();

                    System.out.println("Digite o numero de gols do visitante da partida que deseja criar: ");
                    int creategolsFora = sc.nextInt();

                    System.out.println("Digite a competicao da partida que deseja criar: ");
                    if (sc.hasNextLine() || sc.hasNext()) {
                        sc.nextLine();
                    }
                    String createComp = sc.nextLine();

                    Data date = new Data(createAno, createMes, createDia); // Atualização do registro
                    Partida partidaCriada = new Partida(date, createMandante, createVisitante, creategolsCasa,
                            creategolsFora, createComp);
                    int tempId = CRUD.getLastId();
                    switch (tipoIndice) {
                        case 0:
                            arvore.inserir(++tempId, CRUD.create(partidaCriada));

                            break;

                        case 1:
                            hash.inserir(++tempId, CRUD.create(partidaCriada));
                            break;
                    }
                    break;
                }

                case 1: { // READ
                    System.out.println("Digite o ID que deseja ler: ");
                    int idread = sc.nextInt();
                    Partida p3 = null;
                    long tempPos = 0;
                    if (tipoIndice == 0) {
                        tempPos = arvore.read(idread);
                    } else {
                        tempPos = hash.read(idread);
                    }
                    if (tempPos != -1) {
                        p3 = CRUD.read(idread, tempPos);
                        System.out.println(p3);
                    } else {
                        System.out.println("Partida não encontrada");
                    }
                    break;
                }
                case 2: { // UPDATE
                    System.out.println("Digite o id que deseja atualizar: ");
                    int idupdate = sc.nextInt();

                    System.out.println("Digite o novo dia da partida que deseja atualizar: ");
                    int novoDia = sc.nextInt();

                    System.out.println("Digite o novo mes da partida que deseja atualizar: ");
                    int novoMes = sc.nextInt();

                    System.out.println("Digite o novo ano da partida que deseja atualizar: ");
                    int novoAno = sc.nextInt();

                    System.out.println("Digite o novo mandante da partida que deseja atualizar: ");
                    if (sc.hasNextLine() || sc.hasNext()) {
                        sc.nextLine();
                    }
                    String novoMandante = sc.nextLine();

                    System.out.println("Digite o novo visitante da partida que deseja atualizar: ");
                    if (sc.hasNextLine() || sc.hasNext()) {
                        sc.nextLine();
                    }
                    String novoVisitante = sc.nextLine();

                    System.out.println("Digite o numero de gols do mandante da partida que deseja atualizar: ");
                    int golsCasa = sc.nextInt();

                    System.out.println("Digite o numero de gols do visitante da partida que deseja atualizar: ");
                    int golsFora = sc.nextInt();

                    System.out.println("Digite a competicao da partida que deseja atualizar: ");
                    if (sc.hasNextLine() || sc.hasNext()) {
                        sc.nextLine();
                    }
                    String novaComp = sc.nextLine();

                    Data date = new Data(novoDia, novoMes, novoAno); // Atualização do registro
                    long resultado = CRUD.update(idupdate, date, novoMandante, novoVisitante, golsCasa, golsFora,
                            novaComp);

                    if (resultado != 0) {
                        if (resultado != -1) {
                            switch (tipoIndice) {
                                case 0:
                                    arvore.update(idupdate, resultado);

                                    break;

                                case 1:
                                    hash.update(idupdate, resultado);
                                    break;
                            }

                        }
                        System.out.println("Alteração concluida com sucesso");
                    } else {
                        System.out.println("Falha na alteração");
                    }
                    break;
                }

                case 3: { // DELETE
                    System.out.println("Digite o id que deseja deletar: ");
                    int iddelete = sc.nextInt();
                    long tempPos = 0;
                    boolean resultado = false;
                    if (tipoIndice == 0) {
                        tempPos = arvore.read(iddelete);
                        tempPos -= 4;
                        resultado = arvore.delete(iddelete);

                    } else if (tipoIndice == 1) {
                        tempPos = hash.read(iddelete);
                        tempPos -= 4;
                        resultado = hash.delete(iddelete);

                    }

                    if (resultado) {
                        CRUD.delete(iddelete, tempPos);
                        indiceInvertido.removeID(tempPos + 4);
                        System.out.println("Alteração concluida com sucesso");
                    } else {
                        System.out.println("Falha na alteração");
                    }

                    break;
                }
                case 4: { // PRINTAR TODOS
                    // Taamnho máximo 46289
                    for (int i = 1; i <= totalElementos; i++) {
                        Partida p3 = null;
                        long tempPos = 0;
                        if (tipoIndice == 0) {
                            tempPos = arvore.read(i);
                        } else {
                            tempPos = hash.read(i);
                        }
                        if (tempPos != -1) {
                            p3 = CRUD.read(i, tempPos);
                            System.out.println(p3);
                        } else {
                            System.out.println("Partida não encontrada");
                        }
                    }
                    break;
                }
                case 5: { // COMPACTAR
                    long tempoAnterior = System.currentTimeMillis();
                    LZW lzw = new LZW();
                    lzw.comprimir();
                    System.out.println("Tempo total: "
                            + ((System.currentTimeMillis()-tempoAnterior) / 1000) + " segundos");
                    break;
                }
                case 6: { // DESCOMPACTAR
                    LZW lzw = new LZW();
                    System.out.println("Escolha uma das opções de arquivo para ser descompactado: ");
                    LZW.printVersoes();
                    int opcaoDescompactar = sc.nextInt();
                    lzw.descompactar(new RandomAccessFile("db\\bancocompressao" + opcaoDescompactar + ".db", "rw"),
                            opcaoDescompactar);

                    break;
                }
                case 7: { // BUSCA POR PADRÃO
                    // ======================================= KMP
                    // ==================================================
                    System.out.println("Digite o padrao que deseja buscar: ");
                    String padrao = sc.next();

                    RandomAccessFile tempFile = new RandomAccessFile("db\\banco.db", "rw");
                    long tempoKMP = System.currentTimeMillis();
                    int comparacoesKMP[] = Kmp.buscaKmp(String.valueOf(tempFile.readInt()), padrao);
                    long posBanco = 4;
                    while (comparacoesKMP[1] == -1 && posBanco < tempFile.length()) {

                        String texto = Partida.puxaPartida(posBanco, tempFile);

                        int temp[] = Kmp.buscaKmp(texto, padrao);
                        comparacoesKMP[0] += temp[0];
                        comparacoesKMP[1] = temp[1];
                        String dados[] = texto.split("@");

                        posBanco += (comparacoesKMP[1] == -1) ? Integer.parseInt(dados[1]) + 6 : 0;
                    }
                    tempoKMP = System.currentTimeMillis() - tempoKMP;
                    System.out.println("Foram necessárias " + comparacoesKMP[0]
                            + " compararações em " + tempoKMP + " mili segundos utilizando o Kmp Melhorado.");

                    // ==================================== BoyerMoore
                    // =======================================
                    tempFile.seek(0);
                    long tempoBM = System.currentTimeMillis();
                    int comparacoesBM[] = new int[] { 0, -1 };
                    try {
                        comparacoesBM = BoyerMoore.buscaBoyerMoore(String.valueOf(tempFile.readInt()), padrao);
                    } catch (StringIndexOutOfBoundsException e) {

                    }
                    long posBancoBM = 4;
                    while (comparacoesBM[1] == -1 && posBancoBM < tempFile.length()) {

                        String texto = Partida.puxaPartida(posBancoBM, tempFile);
                        int temp[] = BoyerMoore.buscaBoyerMoore(texto, padrao);
                        comparacoesBM[0] += temp[0];
                        comparacoesBM[1] = temp[1];
                        String dados[] = texto.split("@");
                        posBancoBM += (comparacoesBM[1] == -1) ? Integer.parseInt(dados[1]) + 6 : 0;
                    }

                    tempoBM = System.currentTimeMillis() - tempoBM;
                    System.out.println("Foram necessárias " + comparacoesBM[0]
                            + " compararações em " + tempoBM + " mili segundos utilizando o Boyer moyer.");

                    tempFile.seek(0);
                    // ================ Rabin Karp ================
                    long tempoRK = System.currentTimeMillis();
                    int comparacoesRk[] = new int[] { 0, -1 };
                    try {
                        RabinKarp.buscaRabinKarp(String.valueOf(tempFile.readInt()), padrao);

                    } catch (StringIndexOutOfBoundsException e) {
                    }

                    long posBancoRK = 4;
                    while (comparacoesRk[1] == -1 && posBancoRK < tempFile.length()) {

                        String texto = Partida.puxaPartida(posBancoRK, tempFile);
                        int temp[] = RabinKarp.buscaRabinKarp(texto, padrao);
                        comparacoesRk[0] += temp[0];
                        comparacoesRk[1] = temp[1];
                        String dados[] = texto.split("@");
                        posBancoRK += (comparacoesRk[1] == -1) ? Integer.parseInt(dados[1]) + 6 : 0;
                    }

                    tempoRK = System.currentTimeMillis() - tempoRK;
                    System.out.println("Foram necessárias " + comparacoesRk[0]
                            + " compararações em " + tempoRK + " mili segundos utilizando o Rabin Karp.");

                    sc.next();
                    tempFile.close();
                }

                case 8: { // PRINTA LISTA INVERTIDA
                    indiceInvertido.printListaInvertida();
                    break;
                }
                case 9: { // POR PADRÕES SIMULTÂNEOS
                    System.out.println("Digite os padrões que deseja busca(separe por ponto e vírgula): ");
                    sc.nextLine();
                    String padrao = sc.nextLine();
                    indiceInvertido.buscaExclusiva(padrao);
                    break;
                }
                case 10: { // POR PADRÕES ADITIVOS
                    System.out.println("Digite os padrões que deseja busca(separe por ponto e vírgula): ");
                    sc.nextLine();
                    String padrao = sc.nextLine();
                    indiceInvertido.buscaAdicionada(padrao);
                    break;
                }
            }

        }
        // arvore.fecharArquivo();
        sc.close();
    }
}
