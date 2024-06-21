package com.index;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

// 
/* Classe principal para criar e armazenar o índice invertido 
 */
public class IndiceInvertido {
    private List<Torneio> torneios;

    /**
     * Incializa a lista invertida, para um arquivo n criado
     */
    public IndiceInvertido() {
        this.torneios = new ArrayList<>();
    }

    /**
     * Inicializa a lista invertida, realizando a leitura de um indíce já criado
     * 
     * @param indice
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public IndiceInvertido(RandomAccessFile indice) throws IOException {
        this.torneios = new ArrayList<>();
        indice.seek(0);
        while (indice.getFilePointer() < indice.length()) {
            int tamToreio = indice.readInt();
            byte[] b = new byte[tamToreio];
            indice.read(b);
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            // DataInputStream dis = new DataInputStream(bais, );
            InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8);
            // String nomeTorneio = isr.readUTF();
            String nomeTorneio = new BufferedReader(isr).readLine();
            adicionarTorneio(nomeTorneio);

            int tamIndice = indice.readInt();

            for (int i = 0; i < tamIndice; i++) {
                adicionarIdAoTorneio(nomeTorneio, indice.readLong());
            }

        }

    }

    /**
     * Adiciona indices de torneio a memória principal
     * 
     * @param torn torneio a ser adicionado
     */
    public void adicionarTorneio(String torn) {
        if (!existeTorneio(torn)) {
            torneios.add(new Torneio(torn));
        }
    }

    /**
     * Adiciona um id a um torneio ao indice da memória principal
     * 
     * @param nomeTorneio nome do torneio que irá receber o indice
     * @param id          id do elemento com o determinado torneio
     */
    public void adicionarIdAoTorneio(String nomeTorneio, long id) {
        for (Torneio torneio : torneios) {
            if (torneio.getNome().equals(nomeTorneio)) {
                torneio.adicionarId(id);
                return; // Não precisamos continuar procurando após encontrar o torneio
            }
        }
        // Se chegarmos aqui, o torneio não foi encontrado
        System.out.println("Torneio '" + nomeTorneio + "' não encontrado.");
    }

    /**
     * Salva a lista de indices da memória principal a memória secundária
     * 
     * @param nomeArquivo nome do arquivo a ter elementos salvos
     */
    public void criarIndice(String nomeArquivo) {
        if (new File(nomeArquivo).exists()) {
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeArquivo))) {
                for (Torneio torneio : torneios) {
                    // Escrever o nome do torneio
                    byte[] nomeBytes = torneio.getNome().getBytes("UTF-8");
                    dos.writeInt(nomeBytes.length);
                    dos.write(nomeBytes);

                    // Escrever o número de IDs associados ao torneio
                    dos.writeInt(torneio.getIds().size());

                    // Escrever cada ID associado ao torneio
                    for (long id : torneio.getIds()) {
                        dos.writeLong(id);
                    }
                }
                System.out.println("Índice invertido criado e armazenado em '" + nomeArquivo + "'.");
            } catch (IOException e) {
                System.err.println("Erro ao criar o índice invertido: " + e.getMessage());
            }
        } else {
            System.out.println("Arquivo já criado");
        }
    }

    /**
     * Printa a lista invertida com seus elementos
     */
    public void printListaInvertida() {
        System.out.println("Lista Invertida Completa:");
        for (Torneio torneio : torneios) {
            System.out.println("Torneio: " + torneio.getNome());
            System.out.println("IDs associados:");
            for (long id : torneio.getIds()) {
                System.out.print(id + " - ");
            }
            System.out.println();
        }
    }

    /**
     * Verifica se determinado torneio existe na lista
     * 
     * @param nome torneio a ser procurado
     * @return retorna true em caso verdadeiro
     */
    private boolean existeTorneio(String nome) {
        for (Torneio torneio : torneios) {
            if (torneio.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove determinado id em determinado torneio
     * @param id id a ser removido
     * @return retorna true em caso de suceso
     */
    public boolean removeID(long id){
        for (Torneio i : torneios) {
            if (i.getIds().contains(id)) {
                if (i.getIds().size()>1) {
                    i.getIds().remove(id);
                }else{
                    this.torneios.remove(i);
                }
                return true;
            }

        }
        System.out.println("Falha ao remover elemento no índice invertido");
        return false;
    }

    /**
     * Realiza uma busca da intercessão dos elementos buscados
     * @param busca elementos a serem buscados, devem ser separados por ';'
     */
    public void buscaExclusiva(String busca) {
        String termos[] = busca.split(";");
        List<Long> ids = new ArrayList<>();
        List<String> tempTorneios = new ArrayList<>();
        for (Torneio t : torneios) {
            tempTorneios.add(t.getNome());
        }

        for (String termo : termos) {
            if (!tempTorneios.contains(termo)) {
                System.out.println("Nenhum resultado encontrado. ");
                return;
            }
            List<Long> tempIds = new ArrayList<>();
            for (Torneio torneio : torneios) {
                if (torneio.getNome().contentEquals(termo)) {
                    tempIds.addAll(torneio.getIds());
                }
            }
            if (ids.isEmpty()) {
                ids.addAll(tempIds);
            } else {
                ids.retainAll(tempIds);
            }
        }
        System.out.println("Resultado da busca: ");
        for (long id : ids) {
            System.out.print(id + " - ");
        }
    }

    /**
     * Realiza uma busca da união dos elementos buscados
     * @param busca elementos a serem buscados, devem ser separados por ';'
     */
    public void buscaAdicionada(String busca) {
        String termos[] = busca.split(";");
        List<Long> ids = new ArrayList<>();
        List<String> tempTorneios = new ArrayList<>();
        for (Torneio t : torneios) {
            tempTorneios.add(t.getNome());
        }

        for (String termo : termos) {
            if (!tempTorneios.contains(termo)) {
                System.out.println("Nenhum resultado encontrado. ");
                return;
            }
            for (Torneio torneio : torneios) {
                if (torneio.getNome().contentEquals(termo)) {
                    ids.addAll(torneio.getIds());
                }
            }

        }
        System.out.println("Resultado da busca: ");
        for (long id : ids) {
            System.out.print(id + " - ");

        }

    }

}


/**
 * Classe para representar um torneio com seus IDs associados
 */
class Torneio {
    private String nome;
    private List<Long> ids;

    /**
     * Instancia um torneio
     * @param nome nome do torneio
     */
    public Torneio(String nome) {
        this.nome = nome;
        this.ids = new ArrayList<>();
    }

    /**
     * Retornar o nome do torneio
     * @return nome do torneio
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Adiciona um id ao torneio
     * @param id id a ser adicionado
     */
    public void adicionarId(long id) {
        ids.add(id);
    }

    /**
     * Retorna uma lista contendo os ids
     * @return lista com ids
     */
    public List<Long> getIds() {
        return ids;
    }
}
