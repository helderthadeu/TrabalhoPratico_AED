package com.casamentoPadroes;

/**
 * Implementação do algoritmo de busca Boyer-Moore para encontrar um padrão em um texto.
 */
public class BoyerMoore {

    /**
     * Busca um padrão dentro de um texto usando o algoritmo Boyer-Moore.
     *
     * @param texto  O texto onde o padrão será buscado.
     * @param padrao O padrão a ser encontrado no texto.
     * @return Um array de inteiros onde o primeiro elemento é o número de comparações
     *         realizadas e o segundo elemento é a posição da primeira ocorrência do padrão no texto
     *         ou -1 se o padrão não for encontrado.
     * @throws StringIndexOutOfBoundsException Se houver um acesso inválido a uma posição da string.
     */
    public static int[] buscaBoyerMoore(String texto, String padrao) throws StringIndexOutOfBoundsException {
        int i = padrao.length() - 1;
        int j = padrao.length() - 1;
        int comparacoes[] = new int[]{0, 0};
        do {
            comparacoes[0]++;

            if (padrao.charAt(j) == texto.charAt(i)) {
                comparacoes[0]++;
                if (j == 0) {
                    // Retorna o número de comparações realizadas
                    return comparacoes;
                } else {
                    i--;
                    j--;
                }
            } else {
                i = i + padrao.length() - minimo(j, 1 + last(texto.charAt(i), padrao, comparacoes), comparacoes);
                j = padrao.length() - 1;
            }
            comparacoes[0]++;
        } while (i <= texto.length() - 1);

        // Padrão não encontrado
        comparacoes[1] = -1;
        return comparacoes;
    }

    /**
     * Encontra a última ocorrência de um caractere no padrão.
     *
     * @param c           O caractere a ser encontrado.
     * @param padrao      O padrão onde o caractere será buscado.
     * @param comparacoes Um array de inteiros para contar o número de comparações.
     * @return A posição da última ocorrência do caractere no padrão ou -1 se o caractere não for encontrado.
     */
    private static int last(char c, String padrao, int comparacoes[]) {
        for (int i = padrao.length() - 1; i >= 0; i--, comparacoes[0]++) {
            comparacoes[0]++;
            if (padrao.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retorna o menor valor entre dois inteiros.
     *
     * @param a           O primeiro inteiro.
     * @param b           O segundo inteiro.
     * @param comparacoes Um array de inteiros para contar o número de comparações.
     * @return O menor valor entre a e b.
     */
    private static int minimo(int a, int b, int comparacoes[]) {
        comparacoes[0]++;
        if (a < b) {
            return a;
        } else if (b < a) {
            comparacoes[0]++;
            return b;
        } else {
            comparacoes[0]++;
            return a;
        }
    }
}
