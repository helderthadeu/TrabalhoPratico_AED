public class Data {
    private int dia;
    private int mes;
    private int ano;

    public Data() {

    }

    public int getDia() {
        return dia;
    }

    public int getAno() {
        return ano;
    }

    public int getMes() {
        return mes;
    }

    public Data(int d, int a, int m) {
        this.ano = a;
        this.dia = d;
        this.mes = m;
    }

    /**
     * Função que compara duas datas para saber qual é mais antiga
     * 
     * @param d Data comparada
     * @return true para caso o objeto que chama a função é mais velho e false para
     *         caso contrário
     */

    public boolean maisVelho(Data d) {
        if (this.ano > d.ano) {
            return false;
        } else if (this.mes > d.mes) {
            return false;
        } else if (this.dia >= d.dia) {
            return false;
        }

        return true;
    }

    /**
     * Retorna a data em uma string no formato dd/mm/aaaa
     */
    public String toString() {
        return this.dia + "/" + this.mes + "/" + this.ano;
    }

    /**
     * Função que converte a data para números de dias
     * 
     * @return retorna o número de dias
     */
    public int toDias() {

        int diasMes = 0, diasAno;


        // diasAno = (this.ano % 4 == 0) ? 366 : 365;
        diasAno = (this.ano/4)*366 + (this.ano - (this.ano/4))*365;

        for (int i = 1; i < this.mes; i++) {
            switch (i) {
                case 1, 3, 5, 7, 8, 10, 12:
                    diasMes += 31;
                    break;
                case 4, 6, 9, 11:
                    diasMes += 30;
                    break;
                case 2:
                    diasMes += (this.ano % 4 == 0) ? 29 : 28;
                    break;
                default:
                    diasMes += 30;
                    break;
            }
        }

        return this.dia + diasAno + diasMes;
    }

    public Data toDate(int dias) {
        Data data = new Data();
        int ano = dias / 365;
        int mes = (dias % 365) / 30;
        int dia = ((dias % 365) % 30);

        return data;
    }
}
