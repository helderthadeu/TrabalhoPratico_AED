package com.compress;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import com.definicoes.Partida;

public class LZW {
	private int proxArq = 0; // Id da próxima compactação
	private final int tamMaxDicio = 32766; // define o tamanho máximo do dicionário

	/**
	 * Instancia o objeto, já definindo o id do próximo arquivo
	 */
	public LZW() {
		proxArq = this.proxArquvo();
		proxArq++;
	}

	/**
	 * Procura o último id utilizado para compactar um arquivo
	 * 
	 * @return retorna o último id
	 */
	private int proxArquvo() {
		int idAtual = 0;
		String nomeArquivo = "db\\bancoCompressao" + idAtual + ".db";
		while ((new File(nomeArquivo)).exists()) {
			idAtual++;
			nomeArquivo = "db\\bancoCompressao" + idAtual + ".db";
		}

		return --idAtual;
	}

	/**
	 * Comprime o arquivo realizando
	 * 
	 * @throws IOException Exceção para erros de operação no arquivo em memória
	 *                     secundária
	 */
	@SuppressWarnings("unchecked")
	public void comprimir() throws IOException {
		RandomAccessFile arqComprimir = new RandomAccessFile("db\\banco.db", "rw");
		RandomAccessFile arqComprimido = new RandomAccessFile("db\\bancoCompressao" + proxArq + ".db", "rw");
		List<String> dicionario = this.criaDicionarioFixo();

		arqComprimir.seek(0);
		arqComprimido.seek(0);
		int numDados = arqComprimir.readInt();
		String fullFile = String.valueOf(numDados);
		fullFile += "&";

		int posString = 0;
		while (arqComprimir.getFilePointer() < arqComprimir.length()) {
			long posAtual = arqComprimir.getFilePointer();
			String temp = "";
			try {
				temp = puxaPartida(posAtual, arqComprimir);
			} catch (NullPointerException e) {
			} finally {
				fullFile += (temp != null) ? temp : "";

			}

			posAtual = arqComprimir.getFilePointer();

		}

		while (posString < fullFile.length()) {

			String elementoComparado = String.valueOf(fullFile.charAt(posString));
			int codigo = this.verificaDicionario(dicionario, elementoComparado);
			posString++;
			int codigoTeste = codigo;

			while (codigoTeste != -1 && posString < fullFile.length()) {
				codigo = codigoTeste;
				elementoComparado += fullFile.charAt(posString);
				posString++;
				codigoTeste = this.verificaDicionario(dicionario, elementoComparado);
			}
			if (dicionario.size() < tamMaxDicio) {
				dicionario.add(elementoComparado);
			}
			arqComprimido.writeShort(codigo);
			if (posString < fullFile.length()) {
				posString--;
			}
		}

		float ganho = arqComprimir.length() - arqComprimido.length();
		ganho = (ganho * 100) / arqComprimir.length();
		System.out.println("Houve uma diminuição de " + ganho + "% do tamnho do arquivo.");

		arqComprimir.close();
		arqComprimido.close();
		proxArq++;

	}

	/**
	 * Cria o dicionário Fixo
	 * 
	 * @return Retorna uma lista contendo os elementos já iniciados na árvore fixa
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private List criaDicionarioFixo() throws IOException {
		List<String> retorno = new ArrayList<>();

		RandomAccessFile temp = new RandomAccessFile("db\\dicionario.db", "rw");
		temp.seek(0);
		for (int i = 32; i <= 254; i++) {
			temp.writeChar(i);
			String tempString = String.valueOf((char) (i));
			// String tempString = String.valueOf((i));
			retorno.add(tempString);
		}
		temp.close();

		return retorno;
	}

	/**
	 * Verifica se o dicionário possui determinado elemento
	 * 
	 * @param dicionario        Dicionário em memória principal
	 * @param elementoComparado elemento a ser comparado
	 * @return retorna a posição do elemento em caso de sucesso, caso contrário
	 *         retorna {@code -1}
	 */
	private int verificaDicionario(List<String> dicionario, String elementoComparado) {
		if (dicionario.contains(elementoComparado)) {
			for (int i = 0; i < dicionario.size(); i++) {
				if (dicionario.get(i).equals(elementoComparado)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Faz a leitura de uma partida em um arquivo binário, retornando a partida como
	 * uma string
	 * 
	 * @param pos  posição da partida no arquivo
	 * @param file arquivo binário
	 * @return retorna partida como string
	 * @throws IOException Exceção a qualquer erro
	 */
	private String puxaPartida(long pos, RandomAccessFile file) throws NullPointerException, IOException {
		Partida temp = null;
		byte bt[];
		file.seek(pos);
		char lapide = ' ';
		if (file.getFilePointer() < file.length()) {

			lapide = file.readChar();
			int tamElemento = file.readInt();
			bt = new byte[tamElemento];
			file.read(bt);

			temp = new Partida();
			temp.fromByteArray(bt);

		}

		return lapide + temp.toStringLimpa();
	}

	/**
	 * Descompacta o arquivo recriando o dicionário no processo
	 * 
	 * @param comprimido Endereço do arquivo já comprimido
	 * @param versao     número da versão do arquivo
	 * @throws IOException Exceção para erros de operação no arquivo binário
	 */
	@SuppressWarnings("unchecked")
	public void descompactar(RandomAccessFile comprimido, int versao) throws IOException {
		List<String> dicionario = this.criaDicionarioFixo();
		comprimido.seek(0);
		RandomAccessFile descompactado = new RandomAccessFile("db\\bancoDesompressao" + versao + ".db", "rw");
		descompactado.seek(0);
		String fullFile = "";
		boolean primeiroTeste = true;
		while (comprimido.getFilePointer() < comprimido.length()) {

			while (comprimido.getFilePointer() < comprimido.length()) {
				int posRetorno = comprimido.readShort();
				String temp = recriaDicionario(dicionario, posRetorno);
				fullFile += temp;

			}
			String dados[] = fullFile.split("&");
			if (primeiroTeste) {
				descompactado.writeInt(Integer.parseInt(dados[0]));
				primeiroTeste = false;
			}

			for (int i = 1; i < dados.length; i++) {
				Partida temp = new Partida();

				char tempString = dados[i].charAt(0);
				String tempChars = dados[i].substring(1, dados[i].length());

				descompactado.writeChar(tempString);
				temp.stringToPartida(tempChars);

				byte[] b;
				b = temp.toByteArray();
				descompactado.writeInt(b.length);
				descompactado.write(b);

			}
			fullFile = "";
		}
		descompactado.close();
	}

	/**
	 * Recia o dicionário
	 * 
	 * @param dicionario dicionário em memória principal
	 * @param pos        pos a ser adicionada ao dicionário
	 * @return retorna o elemento adicionado
	 */
	private String recriaDicionario(List<String> dicionario, int pos) {
		String retorno = "";
		if (pos < dicionario.size()) {
			retorno = dicionario.get(pos);
			// Verifica se o dicionário é maior doq o tamanho padrão
			if (dicionario.size() > 223) {
				String temp = dicionario.get(dicionario.size() - 1);
				temp += retorno.charAt(0);
				int tempNUm = dicionario.size() - 1;
				dicionario.set(tempNUm, temp);
			}
		}
		if (dicionario.size() < tamMaxDicio) {
			dicionario.add(String.valueOf(retorno));
		}
		return retorno;
	}

	/*
	 * Prina todas as versões de compactação disponíveis
	 */
	public static void printVersoes() {
		int cont = 0;

		while ((new File("db\\bancoCompressao" + cont + ".db").exists())) {
			System.out.println("db\\bancoCompressao" + cont + ".db");
			cont++;
		}

	}
}
