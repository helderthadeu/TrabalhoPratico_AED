package com.encriptar;

/**
 * Classe que representa um alfabeto personalizado baseado em uma senha e um incremento.
 */
public class Alfabeto {

    private StringBuilder letras = new StringBuilder();

    /**
     * Construtor padrão que inicializa o alfabeto vazio.
     */
    public Alfabeto() {
    }

    /**
     * Construtor que inicializa o alfabeto baseado em uma senha e um incremento.
     *
     * @param senha      A senha usada para gerar o alfabeto.
     * @param incremento O incremento aplicado aos valores dos caracteres da senha.
     */
    public Alfabeto(String senha, int incremento) {
        int tempnum = 0;
        String chave = eleminaRepeticoes(senha);
        
        while (letras.length() < 127) {
            for (int i = 0; i < chave.length(); i++) {
                int tempval = chave.charAt(i) + incremento + tempnum;
                if (tempval > 126) {
                    tempval -= 93;
                }
                if (tempnum > 254) {
                    tempnum = 0;
                }

                letras.append((char) tempval);
            }
            tempnum += chave.length();
            String temp = eleminaRepeticoes(letras.toString());
            letras.delete(0, letras.length());
            letras.append(temp);
        }
    }

    /**
     * Remove caracteres repetidos de uma string, mantendo apenas a primeira ocorrência de cada caractere.
     *
     * @param senha A string da qual os caracteres repetidos serão removidos.
     * @return Uma string sem caracteres repetidos.
     */
    public String eleminaRepeticoes(String senha) {
        StringBuilder tempchave = new StringBuilder();
        for (int i = 0; i < senha.length(); i++) {
            int cont = 0;
            for (int j = 0; j < i + 1; j++) {
                if (senha.charAt(i) == senha.charAt(j)) {
                    cont++;
                }
            }
            if (cont <= 1) {
                tempchave.append(senha.charAt(i));
            }
        }

        return tempchave.toString();
    }

    /**
     * Retorna o alfabeto gerado como uma string.
     *
     * @return O alfabeto gerado.
     */
    public String getLetras() {
        return letras.toString();
    }
}
