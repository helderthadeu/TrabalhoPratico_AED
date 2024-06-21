package com.casamentoPadroes;

/**
 * Implementação do algoritmo de busca Knuth-Morris-Pratt (KMP) para encontrar um padrão em um texto.
 */
public class Kmp {

    /**
     * Calcula a função para o prefixo básico.
     *
     * @param padrao O padrão a ser comparado.
     * @return Um array de inteiros representando a função do prefixo básico.
     */
    private int[] calculaFuncaoPrefixo(String padrao) {
        int m = padrao.length();
        int pi[] = new int[m];
        pi[0] = -1;
        for (int i = 0, j = -1; i <= m; i++, j++, pi[i] = j) {
            while (j >= 0 && padrao.charAt(i) != padrao.charAt(j)) {
                j = pi[j];
            }
        }
        return pi;
    }

    /**
     * Calcula a função para o prefixo melhorada.
     *
     * @param padrao O padrão a ser comparado.
     * @param comparacoes Um array de inteiros para contar o número de comparações.
     * @return Um array de inteiros representando a função do prefixo melhorada.
     */
    private static int[] calculaPrefixoMelhorada(String padrao, int comparacoes[]) {
        int m = padrao.length();
        int pi[] = new int[m];
        pi[0] = -1;
        for (int i = 0, j = -1; i < m; i++, j++, comparacoes[0]++) {
            comparacoes[0] += 2;
            pi[i] = (i > 0 && padrao.charAt(i) == padrao.charAt(j)) ? pi[j] : j;
            comparacoes[0] += 2;
            while (j >= 0 && padrao.charAt(i) != padrao.charAt(j)) {
                j = pi[j];
            }
        }
        return pi;
    }

    /**
     * Busca um padrão dentro de um texto usando o algoritmo KMP.
     *
     * @param texto  O texto onde o padrão será buscado.
     * @param padrao O padrão a ser encontrado no texto.
     * @return Um array de inteiros onde o primeiro elemento é o número de comparações
     *         realizadas e o segundo elemento é a posição da primeira ocorrência do padrão no texto
     *         ou -1 se o padrão não for encontrado.
     */
    public static int[] buscaKmp(String texto, String padrao) {
        int n = texto.length();
        int m = padrao.length();
        int comparacoes[] = new int[]{0, 0};
        int i, j;
        int pi[] = Kmp.calculaPrefixoMelhorada(padrao, comparacoes);
        for (i = 0, j = 0; i < n && j < m; i++, j++, comparacoes[0] += 2) {
            comparacoes[0] += 2;
            while (j >= 0 && texto.charAt(i) != padrao.charAt(j)) {
                j = pi[j];
            }
        }
        // Padrão não encontrado
        if (j != m) {
            comparacoes[1] = -1;
        }
        return comparacoes;
    }
}
