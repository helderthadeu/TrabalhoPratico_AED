package com.index;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

// 
/* Classe principal para criar e armazenar o índice invertido 
 */
public class IndiceInvertido2 {
    private List<Mandante> mandantes;

    /**
     * Incializa a lista invertida, para um arquivo n criado
     */
    public IndiceInvertido2() {
        this.mandantes = new ArrayList<>();
    }

    /**
     * Inicializa a lista invertida, realizando a leitura de um indíce já criado
     * 
     * @param indice
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public IndiceInvertido2(RandomAccessFile indice) throws IOException {
        this.mandantes = new ArrayList<>();
        indice.seek(0);
        while (indice.getFilePointer() < indice.length()) {
            int tamMandante = indice.readInt();
            byte[] b = new byte[tamMandante];
            indice.read(b);
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            // DataInputStream dis = new DataInputStream(bais, );
            InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8);
            // String nomeTorneio = isr.readUTF();
            String nomeMandante = new BufferedReader(isr).readLine();
            adicionarMandante(nomeMandante);

            int tamIndice = indice.readInt();

            for (int i = 0; i < tamIndice; i++) {
                adicionarIdAoMandante(nomeMandante, indice.readLong());
            }

        }

    }

    /**
     * Adiciona indices de mandante a memória principal
     * 
     * @param mand mandante a ser adicionado
     */
    public void adicionarMandante(String mand) {
        if (!existeMandante(mand)) {
            mandantes.add(new Mandante(mand));
        }
    }

    /**
     * Adiciona um id a um mandante ao indice da memória principal
     * 
     * @param nomeMandante nome do mandante que irá receber o indice
     * @param id          id do elemento com o determinado mandante
     */
    public void adicionarIdAoMandante(String nomeMandante, long id) {
        for (Mandante mandante : mandantes) {
            if (mandante.getNome().equals(nomeMandante)) {
                mandante.adicionarId(id);
                return; // Não precisamos continuar procurando após encontrar o mandante
            }
        }
        // Se chegarmos aqui, o mandante não foi encontrado
        System.out.println("Mandante '" + nomeMandante + "' não encontrado.");
    }

    /**
     * Salva a lista de indices da memória principal a memória secundária
     * 
     * @param nomeArquivo nome do arquivo a ter elementos salvos
     */
    public void criarIndice(String nomeArquivo) {
        if (new File(nomeArquivo).exists()) {
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeArquivo))) {
                for (Mandante mandante : mandantes) {
                    // Escrever o nome do mandante
                    byte[] nomeBytes = mandante.getNome().getBytes("UTF-8");
                    dos.writeInt(nomeBytes.length);
                    dos.write(nomeBytes);

                    // Escrever o número de IDs associados ao mandante
                    dos.writeInt(mandante.getIds().size());

                    // Escrever cada ID associado ao mandante
                    for (long id : mandante.getIds()) {
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
        for (Mandante mandante : mandantes) {
            System.out.println("Mandante: " + mandante.getNome());
            System.out.println("IDs associados:");
            for (long id : mandante.getIds()) {
                System.out.print(id + " - ");
            }
            System.out.println();
        }
    }

    /**
     * Verifica se determinado mandante existe na lista
     * 
     * @param nome mandante a ser procurado
     * @return retorna true em caso verdadeiro
     */
    private boolean existeMandante(String nome) {
        for (Mandante mandante : mandantes) {
            if (mandante.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove determinado id em determinado mandante
     * @param id id a ser removido
     * @return retorna true em caso de suceso
     */
    public boolean removeID(long id){
        for (Mandante i : mandantes) {
            if (i.getIds().contains(id)) {
                if (i.getIds().size()>1) {
                    i.getIds().remove(id);
                }else{
                    this.mandantes.remove(i);
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
        List<String> tempMandantes = new ArrayList<>();
        for (Mandante t : mandantes) {
            tempMandantes.add(t.getNome());
        }

        for (String termo : termos) {
            if (!tempMandantes.contains(termo)) {
                System.out.println("Nenhum resultado encontrado. ");
                return;
            }
            List<Long> tempIds = new ArrayList<>();
            for (Mandante mandante : mandantes) {
                if (mandante.getNome().contentEquals(termo)) {
                    tempIds.addAll(mandante.getIds());
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
        List<String> tempMandantes = new ArrayList<>();
        for (Mandante t : mandantes) {
            tempMandantes.add(t.getNome());
        }

        for (String termo : termos) {
            if (!tempMandantes.contains(termo)) {
                System.out.println("Nenhum resultado encontrado. ");
                return;
            }
            for (Mandante mandante : mandantes) {
                if (mandante.getNome().contentEquals(termo)) {
                    ids.addAll(mandante.getIds());
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
 * Classe para representar um mandante com seus IDs associados
 */
class Mandante {
    private String nome;
    private List<Long> ids;

    /**
     * Instancia um mandante
     * @param nome nome do mandante
     */
    public Mandante(String nome) {
        this.nome = nome;
        this.ids = new ArrayList<>();
    }

    /**
     * Retornar o nome do mandante
     * @return nome do mandante
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Adiciona um id ao mandante
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
