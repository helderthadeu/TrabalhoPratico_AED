import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

public class App {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String arquivoCSV = "results.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoCSV))) {
            if (!(new File("db/banco.db")).exists()) {

                String line;
                int id = 0;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    Partida match = new Partida();
                    match.setID(id++);
                    match.setDatacomString(data[0]);
                    match.setMandante(data[1]);
                    match.setVisitante(data[2]);
                    match.setGolsMandante(Integer.parseInt(data[3]));
                    match.setGolsVisitante(Integer.parseInt(data[4]));
                    match.setTorneio(data[5]);

                    CRUD.create(match);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int opcao = 0;
        while (opcao != -1) {
            System.out.println(
                    "Digite as seguintes opcoes para carregar o arquivo: [0]Create - [1]Read - [2]Update - [3]Delete. Digite [-1] para encerrar");
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
                    String createMandante = sc.next();

                    System.out.println("Digite o visitante da partida que deseja criar: ");
                    String createVisitante = sc.next();

                    System.out.println("Digite o numero de gols do mandante da partida que deseja criar: ");
                    int creategolsCasa = sc.nextInt();

                    System.out.println("Digite o numero de gols do visitante da partida que deseja criar: ");
                    int creategolsFora = sc.nextInt();

                    System.out.println("Digite a competicao da partida que deseja criar: ");
                    String createComp = sc.next();

                    Data date = new Data(createAno, createMes, createDia); // Atualização do registro
                    Partida partidaCriada = new Partida(date, createMandante, createVisitante, creategolsCasa,
                            creategolsFora, createComp);

                    CRUD.create(partidaCriada);
                    break;
                }

                case 1: { // READ
                    System.out.println("Digite o ID que deseja ler: ");
                    int idread = sc.nextInt();

                    Partida p3 = CRUD.read(idread);
                    System.out.println(p3);
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
                    if(sc.hasNextLine() || sc.hasNext()){
                        sc.nextLine();
                    }
                    String novoMandante = sc.nextLine();
                    

                    System.out.println("Digite o novo visitante da partida que deseja atualizar: ");
                    if(sc.hasNextLine() || sc.hasNext()){
                        sc.nextLine();
                    }
                    String novoVisitante = sc.nextLine();

                    System.out.println("Digite o numero de gols do mandante da partida que deseja atualizar: ");
                    int golsCasa = sc.nextInt();

                    System.out.println("Digite o numero de gols do visitante da partida que deseja atualizar: ");
                    int golsFora = sc.nextInt();

                    System.out.println("Digite a competicao da partida que deseja atualizar: ");
                    if(sc.hasNextLine() || sc.hasNext()){
                        sc.nextLine();
                    }
                    String novaComp = sc.nextLine();

                    Data date = new Data(novoDia, novoMes, novoAno); // Atualização do registro
                    boolean resultado = CRUD.update(idupdate, date, novoMandante, novoVisitante, golsCasa, golsFora,
                            novaComp);

                    if (resultado) {
                        System.out.println("Alteração concluida com sucesso");
                    } else {
                        System.out.println("Falha na alteração");
                    }
                    break;
                }

                case 3: { // DELETE
                    System.out.println("Digite o id que deseja deletar: ");
                    int iddelete = sc.nextInt();
                    boolean resultado = CRUD.delete(iddelete);

                    if (resultado) {
                        System.out.println("Alteração concluida com sucesso");
                    } else {
                        System.out.println("Falha na alteração");
                    }

                    break;
                }
            }

            // Teste para a conversão

            // for (int i = 1; i < 13; i++) {
            // Data teste = new Data(28, i, 2024);
            // int num = teste.toDias();
            // // System.out.println(num);
            // teste.toDate(num);
            // System.out.println(teste.toString());

            // }

        }
        sc.close();
    }
}
