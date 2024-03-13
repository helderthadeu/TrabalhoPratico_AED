
public class App {
    public static void main(String[] args) throws Exception {
        int opcao = 1;
        switch (opcao) {
        case 0: {
        Data date = new Data(15,02,2002);
        Partida p1 = new Partida(date,"Brasil","Argentina", 4,0,"Amistoso");
        CRUD.create(p1);
        System.out.println(p1);
        date = new Data(26,5,1915);
        Partida p2 = new Partida(date,"Uniao Soviética", "Iugoslávia", 6, 8, "Copa Friacastão");
        CRUD.create(p2);
        System.out.println(p2);
        date = new Data(16,8,1979);
        Partida p3 = new Partida(date,"Austrália", "República Democratica do congo", 4,1, "Copa Loucura");
        CRUD.create(p3);
        System.out.println(p3);
        break;
        }
        case 1: {
        Partida p3 = CRUD.read(2);
        System.out.println(p3);
        break;
        }
        case 2: {
        Data date = new Data(16,8,1979);
        boolean resultado = CRUD.update(3,date,"Austrália", "Republica do congo", 4,1,"Copa Loucura");

        if (resultado) {
        System.out.println("Alteração concluida com sucesso");
        } else {
        System.out.println("Falha na alteração");
        }
        break;
        }
        case 3: {
        boolean resultado = CRUD.delete(2);

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
        //     Data teste = new Data(28, i, 2024);
        //     int num = teste.toDias();
        //     // System.out.println(num);
        //     teste.toDate(num);
        //     System.out.println(teste.toString());

        // }
    }
}
