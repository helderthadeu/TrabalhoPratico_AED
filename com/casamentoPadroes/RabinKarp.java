package com.casamentoPadroes;

/**
 * Implementação do algoritmo de busca Rabin-Karp para encontrar um padrão em um texto.
 */
public class RabinKarp {
    static final int d = 256; // número de caracteres no alfabeto de entrada
    static final int q = 46301; // um número primo

    /**
     * Busca um padrão dentro de um texto usando o algoritmo Rabin-Karp.
     *
     * @param texto  O texto onde o padrão será buscado.
     * @param padrao O padrão a ser encontrado no texto.
     * @return Um array de inteiros onde o primeiro elemento é o número de comparações
     *         realizadas e o segundo elemento é a posição da primeira ocorrência do padrão no texto
     *         ou -1 se o padrão não for encontrado.
     * @throws StringIndexOutOfBoundsException Se houver um acesso inválido a uma posição da string.
     */
    public static int[] buscaRabinKarp(String texto, String padrao) throws StringIndexOutOfBoundsException {
        int n = texto.length();
        int m = padrao.length();
        int i, j;
        int p = 0; // hash do padrão
        int t = 0; // hash do texto
        int h = 1; // valor de d^(m-1) % q
        int comparacoes[] = new int[]{0, 0};

        // Calcula h = (d^(m-1)) % q
        for (i = 0; i < m - 1; i++, comparacoes[0]++)
            h = (h * d) % q;

        // Calcula o hash inicial do padrão e do texto
        for (i = 0; i < m; i++, comparacoes[0]++) {
            p = (d * p + padrao.charAt(i)) % q;
            t = (d * t + texto.charAt(i)) % q;
        }

        // Desliza o padrão sobre o texto
        for (i = 0; i <= n - m; i++, comparacoes[0]++) {
            comparacoes[0]++;
            // Verifica se os hashes são iguais
            if (p == t) {
                // Verifica os caracteres um a um
                for (j = 0; j < m; j++, comparacoes[0]++) {
                    comparacoes[0]++;
                    if (texto.charAt(i + j) != padrao.charAt(j))
                        break;
                }

                comparacoes[0]++;
                if (j == m)
                    return comparacoes;
            }

            // Calcula o hash da próxima janela do texto
            comparacoes[0]++;
            if (i < n - m) {
                t = (d * (t - texto.charAt(i) * h) + texto.charAt(i + m)) % q;

                comparacoes[0]++;
                if (t < 0)
                    t = (t + q);
            }
        }

        // Padrão não encontrado
        comparacoes[1] = -1;
        return comparacoes;
    }
}
