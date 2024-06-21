package com.encriptar;

import java.io.IOException;

/**
 * Classe que provê métodos para criptografar e descriptografar senhas.
 */
public class Criptografar {

    /**
     * Criptografa uma string usando um alfabeto gerado a partir de uma senha e incremento.
     *
     * @param aEncriptar A string a ser criptografada.
     * @return A string criptografada.
     * @throws IOException Se ocorrer um erro de E/S.
     */
    public static String criptografarSenhas(String aEncriptar) throws IOException {
        Alfabeto novoAlfabeto = new Alfabeto("givanildodeOliveira", 8);

        String alfabeto = novoAlfabeto.getLetras();
        int tempVal;
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < aEncriptar.length(); i++) {
            tempVal = aEncriptar.charAt(i);
            if (tempVal > 127 || tempVal < 33) {
                tempVal = 32;
            }
            tempVal -= 32;
            tmp.append(alfabeto.charAt(tempVal));
        }

        return tmp.toString();
    }

    /**
     * Descriptografa uma string criptografada usando um alfabeto gerado a partir de uma senha e incremento.
     *
     * @param desincriptar A string a ser descriptografada.
     * @return A string descriptografada.
     * @throws IOException Se ocorrer um erro de E/S.
     */
    public static String descriptografarSenhas(String desincriptar) throws IOException {
        Alfabeto novoAlfabeto = new Alfabeto("givanildodeOliveira", 8);

        String alfabeto = novoAlfabeto.getLetras();
        StringBuilder senhas = new StringBuilder();

        int tempVal = 0;

        for (int i = 0; i < desincriptar.length(); i++) {
            tempVal = desincriptar.charAt(i);
            for (int j = 0; j < alfabeto.length(); j += 1) {
                int tempnum = alfabeto.charAt(j);

                if (tempVal == tempnum) {
                    senhas.append((char) (j + 32));
                    break;
                }
            }
        }

        return senhas.toString();
    }
}
