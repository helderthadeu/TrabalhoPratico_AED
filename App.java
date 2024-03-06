
public class App {
    public static void main(String[] args) throws Exception {
        int opcao = 1;
        switch (opcao) {
            case 0: {
                Livro l1 = new Livro("O nome do vento", 600);
                CRUD.create(l1);
                System.out.println(l1);
                Livro l2 = new Livro("A Origem", 300);
                CRUD.create(l2);
                System.out.println(l2);
                Livro l3 = new Livro("Jane eyre", 634);
                CRUD.create(l3);
                System.out.println(l3);
                break;
            }
            case 1: {
                Livro l3 = CRUD.read(1);
                System.out.println(l3);
                break;
            }
            case 2: {
                boolean resultado = CRUD.update(7, "O nome dell", 600);

                if (resultado) {
                    System.out.println("Alteração concluida com sucesso");
                } else {
                    System.out.println("Falha na alteração");
                }
                break;
            }
            case 3: {
                boolean resultado = CRUD.delete(1);

                if (resultado) {
                    System.out.println("Alteração concluida com sucesso");
                } else {
                    System.out.println("Falha na alteração");
                }

                break;
            }
        }

    }
}
